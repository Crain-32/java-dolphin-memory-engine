package org.crain.dolphin.platform.windows;

import org.crain.dolphin.platform.util.NativeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.*;

public class WindowsKernel {

    public static class PROCESSENTRY32 {
        public int th32ProcessID;
        public char[] szExeFile;

        private PROCESSENTRY32(MemorySegment memorySegment) {
            th32ProcessID = memorySegment.get(JAVA_INT, offset("th32ProcessID"));
            szExeFile = memorySegment.getString(offset("szExeFile")).toCharArray();
        }

        private static final StructLayout NATIVE_LAYOUT = MemoryLayout.structLayout(
                JAVA_INT.withName("dwSize"),
                JAVA_INT.withName("cntUsage"),
                JAVA_INT.withName("th32ProcessID"),
                ADDRESS.withoutTargetLayout().withByteAlignment(4).withName("th32DefaultHeapID"), // No longer used ULONG_PTR
                JAVA_INT.withName("th32ModuleID"),
                JAVA_INT.withName("cntThreads"),
                JAVA_INT.withName("th32ParentProcessID"),
                JAVA_LONG.withName("pcPriClassBase"),
                JAVA_INT.withName("dwFlags"),
                MemoryLayout.sequenceLayout(260, JAVA_CHAR).withName("szExeFile")
        );

        private static long offset(String field) {
            final long offset = NATIVE_LAYOUT.byteOffset(
                    PathElement.groupElement(field)
            );
            WindowsLogger.WINDOWS_LOGGER.atDebug()
                    .setMessage("Field {} is at Offset {}")
                    .addArgument(field)
                    .addArgument(offset)
                    .log();
            return offset;
        }

        private static final VarHandle DS_SIZE_HANDLE = NATIVE_LAYOUT.varHandle(
                PathElement.groupElement("dwSize")
        );
    }


    private static volatile MethodHandle cth32SnapshotMethod;
    private static volatile MethodHandle process32NextMethod;
    private static volatile MethodHandle process32FirstMethod;
    private static volatile MethodHandle closeHandleMethod;
    private static volatile MethodHandle openProcessMethod;

    private static final int TH32CS_SNAPPROCESS = 0x00000002;

    public static int CreateToolhelp32Snapshot() {
        try {
            return (int) cth32SnapshotMethod.invoke(TH32CS_SNAPPROCESS, 0);
        } catch (Throwable t) {
            WindowsLogger.WINDOWS_LOGGER.atError()
                    .setCause(t)
                    .log("Failed to CreateToolhelp32Snapshot");
            return -1;
        }
    }

    public static PROCESSENTRY32 Process32First(int handle) {
        return Process32Helper(handle, process32FirstMethod);
    }

    public static PROCESSENTRY32 Process32Next(int handle) {
        return Process32Helper(handle, process32NextMethod);

    }

    private static PROCESSENTRY32 Process32Helper(int handle, MethodHandle methodHandle) {
        try (Arena arena = Arena.ofConfined()) {
            var nativeProcessSegment = arena.allocate(PROCESSENTRY32.NATIVE_LAYOUT);
            PROCESSENTRY32.DS_SIZE_HANDLE.set(nativeProcessSegment, 0, (int) PROCESSENTRY32.NATIVE_LAYOUT.byteSize());
            var memoryPointer = MemorySegment.ofAddress(nativeProcessSegment.address());
            int result = (int) methodHandle.invoke(handle, memoryPointer);
            if (result == 0) {
                return null;
            } else {
                return new PROCESSENTRY32(nativeProcessSegment);
            }
        } catch (Throwable t) {
            var logBuilder = WindowsLogger.WINDOWS_LOGGER.atError()
                    .setCause(t)
                    .setMessage("Process32Helper({}, {}) Failed.")
                    .addArgument(handle);
            String process32Type = "DEBUG";
            if (WindowsLogger.WINDOWS_LOGGER.isDebugEnabled()) {
                process32Type = methodHandle == process32NextMethod ? "Process32Next" : "Process32First";
            }
            logBuilder.addArgument(process32Type)
                    .log();
            return null;
        }
    }

    public static void CloseHandle(int handle) {
        try {
            closeHandleMethod.invoke(handle);
        } catch (Throwable t) {
            WindowsLogger.WINDOWS_LOGGER.atError()
                    .setCause(t)
                    .setMessage("CloseHandle({}) Failed")
                    .addArgument(handle)
                    .log();
        }
    }

    public static final int PROCESS_QUERY_INFORMATION = 0x0400;
    public static final int PROCESS_VM_OPERATION = 0x0008;
    public static final int PROCESS_VM_READ = 0x0010;
    public static final int PROCESS_VM_WRITE = 0x0020;

    public static int OpenProcess(int pid, int flags) {
        try {
            return (int) openProcessMethod.invoke(
                    flags,
                    0,
                    pid
            );
        } catch (Throwable t) {
            WindowsLogger.WINDOWS_LOGGER.atError()
                    .setCause(t)
                    .setMessage("""
                            OpenProcess({}, Flags) failed,
                            Flag State,
                            PROCESS_QUERY_INFORMATION      - {}
                            PROCESS_VM_OPERATION           - {}
                            PROCESS_VM_READ                - {}
                            PROCESS_VM_WRITE               - {}
                            """)
                    .addArgument(pid)
                    .addArgument((flags & PROCESS_QUERY_INFORMATION) == PROCESS_QUERY_INFORMATION)
                    .addArgument((flags & PROCESS_VM_OPERATION) == PROCESS_VM_OPERATION)
                    .addArgument((flags & PROCESS_VM_READ) == PROCESS_VM_READ)
                    .addArgument((flags & PROCESS_VM_WRITE) == PROCESS_VM_WRITE)
                    .log();
            return -1;
        }
    }


    static void linkNativeFunctions(final boolean forceFind) {
        WindowsLogger.WINDOWS_LOGGER.atTrace()
                .setMessage("WindowsKernel::linkNativeFunctions({})")
                .addArgument(forceFind)
                .log();
        synchronized (WindowsKernel.class) {
            if (cth32SnapshotMethod == null || forceFind) {
                cth32SnapshotMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "CreateToolhelp32Snapshot",
                        JAVA_INT,
                        JAVA_INT,
                        JAVA_INT
                );
            }
            if (process32FirstMethod == null || forceFind) {
                process32FirstMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "Process32First",
                        JAVA_INT,
                        JAVA_INT,
                        ADDRESS
                );
            }
            if (process32NextMethod == null || forceFind) {
                process32NextMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "Process32Next",
                        JAVA_INT,
                        JAVA_INT,
                        ADDRESS
                );
            }
            if (closeHandleMethod == null || forceFind) {
                closeHandleMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "CloseHandle",
                        JAVA_INT,
                        JAVA_INT
                );
            }
            if (openProcessMethod == null || forceFind) {
                openProcessMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "OpenProcess",
                        JAVA_INT,
                        JAVA_INT,
                        JAVA_INT,
                        JAVA_INT
                );
            }
        }
    }

}
