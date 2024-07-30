package org.crain.dolphin.platform.windows;

import org.crain.dolphin.platform.util.NativeHelper;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.*;

public class WindowsProcessMemory {

    private static volatile MethodHandle virtualQueryExMethod;
    private static volatile MethodHandle queryWorkingSetExMethod;
    private static volatile MethodHandle readProcessMemoryMethod;
    private static volatile MethodHandle writeProcessMemoryMethod;

    public record MEMORY_BASIC_INFORMATION(long BaseAddress, long AllocationBase, int AllocationProtect,
                                           short PartitionId, long RegionSize, int State, int Protect, int Type) {

        private static MEMORY_BASIC_INFORMATION fromNative(MemorySegment memorySegment) {
            return new MEMORY_BASIC_INFORMATION(
                    memorySegment.get(JAVA_LONG, offset("BaseAddress")),
                    memorySegment.get(JAVA_LONG, offset("AllocationBase")),
                    memorySegment.get(JAVA_INT, offset("AllocationProtect")),
                    memorySegment.get(JAVA_SHORT, offset("PartitionId")),
                    memorySegment.get(JAVA_LONG, offset("RegionSize")),
                    memorySegment.get(JAVA_INT, offset("State")),
                    memorySegment.get(JAVA_INT, offset("Protect")),
                    memorySegment.get(JAVA_INT, offset("Type"))
            );

        }

        private static long offset(String field) {
            return NATIVE_LAYOUT.byteOffset(
                    PathElement.groupElement(field)
            );
        }

        private static final StructLayout NATIVE_LAYOUT = MemoryLayout.structLayout(
                JAVA_LONG.withName("BaseAddress"),
                JAVA_LONG.withName("AllocationBase"),
                JAVA_INT.withName("AllocationProtect"),
                JAVA_SHORT.withName("PartitionId"),
                MemoryLayout.paddingLayout(2),
                JAVA_LONG.withName("RegionSize"),
                JAVA_INT.withName("State"),
                JAVA_INT.withName("Protect"),
                JAVA_INT.withName("Type"),
                MemoryLayout.paddingLayout(4)
        );

    }

    public static class PSAPI_WORKING_SET_EX_INFORMATION {
        private final long VirtualAddress;
        private final byte[] VirtualAttributes;

        private PSAPI_WORKING_SET_EX_INFORMATION(long virtualAddress, byte[] virtualAttributes) {
            this.VirtualAddress = virtualAddress;
            this.VirtualAttributes = virtualAttributes;
        }

        public boolean Valid() {
            return (VirtualAttributes[0] & 0x01) == 1;
        }

        public int ShareCount() {
            return (VirtualAttributes[0] >> 1) & 0x03;
        }

        public int Win32Protection() {
            int mediumByte = VirtualAttributes[1] & 0x7F;
            int lowNibble = VirtualAttributes[0] >> 4;
            return (mediumByte << 4) | (lowNibble);
        }

        public boolean Shared() {
            return (VirtualAttributes[1] & 0x80) == 0x80;
        }

        public int Node() {
            return VirtualAttributes[2] & 0x3F;
        }

        public boolean Locked() {
            return (VirtualAttributes[2] & 0x40) == 0x40;
        }

        public boolean LargePage() {
            return (VirtualAttributes[2] & 0x80) == 0x80;
        }

        public int Reserved() {
            return (VirtualAttributes[3] & 0x7F);
        }

        public boolean Bad() {
            return (VirtualAttributes[3] & 0x80) == 0x80;
        }

        private static PSAPI_WORKING_SET_EX_INFORMATION fromNative(MemorySegment memorySegment) {
            try {
                return new PSAPI_WORKING_SET_EX_INFORMATION(
                        memorySegment.get(JAVA_LONG, PSAPI_WORKING_SET_EX_INFORMATION.offset("VirtualAddress")),
                        memorySegment.asSlice(offset("VirtualAttributes")).toArray(JAVA_BYTE)
                );
            } catch (Exception e) {
                WindowsLogger.WINDOWS_LOGGER.atError()
                        .setCause(e)
                        .log("Failed to Map MemorySegment to PSAPI_WORKING_SET_EX_INFORMATION");
                return null;
            }
        }

        private static long offset(String field) {
            return switch (field) {
                case "VirtualAddress" -> NATIVE_LAYOUT.byteOffset(PathElement.groupElement("VirtualAddress"));
                case "VirtualAttributes" -> NATIVE_LAYOUT.byteOffset(PathElement.groupElement("VirtualAttributes"));
                default -> -1;
            };
        }

        private static final StructLayout NATIVE_LAYOUT = MemoryLayout.structLayout(
                JAVA_LONG.withName("VirtualAddress"),
                JAVA_LONG.withName("VirtualAttributes")
        );
    }

    public static MEMORY_BASIC_INFORMATION VirtualQueryEx(final int handle, final long pBaseAddress) {
        try (Arena arena = Arena.ofConfined()) {
            var nativeBasicInformation = arena.allocate(MEMORY_BASIC_INFORMATION.NATIVE_LAYOUT);
            var pBasicInfo = MemorySegment.ofAddress(nativeBasicInformation.address());
            long bufferRead = (long) virtualQueryExMethod.invoke(
                    handle,
                    pBaseAddress,
                    pBasicInfo,
                    nativeBasicInformation.byteSize()
            );
            if (bufferRead <= 0) {
                WindowsLogger.WINDOWS_LOGGER.atDebug()
                        .setMessage("VirtualQueryEx({}, {}) returned a 0 Buffer, Last Windows Error might be {}")
                        .addArgument(handle)
                        .addArgument(() -> Long.toHexString(pBaseAddress))
                        .addArgument(WindowsError::GetLastError)
                        .log();
                return null;
            } else {
                return MEMORY_BASIC_INFORMATION.fromNative(nativeBasicInformation);
            }
        } catch (Throwable t) {
            WindowsLogger.WINDOWS_LOGGER.atDebug()
                    .setMessage("VirtualQueryEx({}, {})")
                    .addArgument(handle)
                    .addArgument(() -> Long.toHexString(pBaseAddress))
                    .setCause(t)
                    .log();
            return null;
        }
    }

    public static PSAPI_WORKING_SET_EX_INFORMATION QueryWorkingSetEx(final int handle, final long virtualAddress) {
        try (Arena arena = Arena.ofConfined()) {
            var memoryLayout = PSAPI_WORKING_SET_EX_INFORMATION.NATIVE_LAYOUT;
            var nativeWorkingSetInfo = arena.allocate(memoryLayout);
            nativeWorkingSetInfo.set(
                    JAVA_LONG,
                    memoryLayout.byteOffset(PathElement.groupElement("VirtualAddress")),
                    virtualAddress
            );
            long bigBoolean = (long) queryWorkingSetExMethod.invoke(
                    handle,
                    MemorySegment.ofAddress(nativeWorkingSetInfo.address()),
                    (int) nativeWorkingSetInfo.byteSize()
            );
            if (bigBoolean == 0) {
                WindowsLogger.WINDOWS_LOGGER.atDebug()
                        .setMessage("QueryWorkingSetEx({}, {}) returned False, Last Windows Error might be {}")
                        .addArgument(handle)
                        .addArgument(() -> Long.toHexString(virtualAddress))
                        .addArgument(WindowsError::GetLastError)
                        .log();
                return null;
            } else {
                return PSAPI_WORKING_SET_EX_INFORMATION.fromNative(nativeWorkingSetInfo);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static byte[] ReadProcessMemory(final int handle, final long pBaseAddress, final long amountToRead) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment nRead = arena.allocate(JAVA_LONG);
            MemorySegment buffer = arena.allocate(amountToRead);
            long result = (long) readProcessMemoryMethod.invoke(
                    handle,
                    pBaseAddress,
                    MemorySegment.ofAddress(buffer.address()),
                    amountToRead,
                    MemorySegment.ofAddress(nRead.address()));
            if (result == 0) {
                WindowsLogger.WINDOWS_LOGGER.atDebug()
                        .setMessage("ReadProcessMemory({}, 0x{}, {}) returned {} Bytes, Last Windows Error might be {}")
                        .addArgument(handle)
                        .addArgument(() -> Long.toHexString(pBaseAddress))
                        .addArgument(amountToRead)
                        .addArgument(result)
                        .addArgument(WindowsError::GetLastError)
                        .log();
                return null;
            } else {
                return buffer.toArray(JAVA_BYTE);
            }
        } catch (Throwable t) {
            WindowsLogger.WINDOWS_LOGGER.atDebug()
                    .setMessage("ReadProcessMemory({}, {}, {}) Failed, Last Windows Error might be {}")
                    .setCause(t)
                    .addArgument(handle)
                    .addArgument(() -> Long.toHexString(pBaseAddress))
                    .addArgument(amountToRead)
                    .addArgument(WindowsError::GetLastError)
                    .log();
            return null;
        }
    }

    public static boolean WriteProcessMemory(final int handle, final long pBaseAddress, byte[] toWrite) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment nWrite = arena.allocate(JAVA_LONG);
            var buffer = arena.allocateFrom(JAVA_BYTE, toWrite);
            long result = (long) writeProcessMemoryMethod.invoke(
                    handle,
                    pBaseAddress,
                    MemorySegment.ofAddress(buffer.address()),
                    (long) toWrite.length,
                    MemorySegment.ofAddress(nWrite.address())
            );
            WindowsLogger.WINDOWS_LOGGER.atTrace()
                    .setMessage("WriteProcessMemory({}, {}, {}'s Length) wrote {} Bytes, Last Windows Error might be {}")
                    .addArgument(handle)
                    .addArgument(() -> Long.toHexString(pBaseAddress))
                    .addArgument(toWrite.length)
                    .addArgument(result)
                    .addArgument(WindowsError::GetLastError)
                    .log();
            return result != 0 || nWrite.getAtIndex(JAVA_INT, 0) == toWrite.length;
        } catch (Throwable t) {
            WindowsLogger.WINDOWS_LOGGER.atError()
                    .setMessage("WriteProcessMemory({}, {}, {}'s Length) Failed, Last Windows Error might be {}")
                    .addArgument(handle)
                    .addArgument(() -> Long.toHexString(pBaseAddress))
                    .addArgument(toWrite.length)
                    .addArgument(WindowsError::GetLastError)
                    .log();
            return false;
        }
    }


    static void linkNativeFunctions(final boolean forceFind) {
        WindowsLogger.WINDOWS_LOGGER.atTrace()
                .setMessage("WindowsProcessMemory::linkNativeFunctions({})")
                .addArgument(forceFind)
                .log();
        synchronized (WindowsProcessMemory.class) {
            if (virtualQueryExMethod == null || forceFind) {
                virtualQueryExMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "VirtualQueryEx",
                        JAVA_LONG,
                        JAVA_INT,
                        JAVA_LONG,
                        ADDRESS,
                        JAVA_LONG
                );
            }
            if (queryWorkingSetExMethod == null || forceFind) {
                queryWorkingSetExMethod = NativeHelper.findNativeMethod(
                        "Psapi.dll",
                        "QueryWorkingSetEx",
                        JAVA_LONG,
                        JAVA_INT,
                        ADDRESS.withTargetLayout(PSAPI_WORKING_SET_EX_INFORMATION.NATIVE_LAYOUT),
                        JAVA_INT
                );
            }
            if (readProcessMemoryMethod == null || forceFind) {
                readProcessMemoryMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "ReadProcessMemory",
                        JAVA_LONG,
                        JAVA_INT,
                        JAVA_LONG,
                        ADDRESS,
                        JAVA_LONG,
                        ADDRESS.withTargetLayout(JAVA_LONG)
                );
            }
            if (writeProcessMemoryMethod == null || forceFind) {
                writeProcessMemoryMethod = NativeHelper.findNativeMethod(
                        "Kernel32.dll",
                        "WriteProcessMemory",
                        JAVA_LONG,
                        JAVA_INT,
                        JAVA_LONG,
                        ADDRESS,
                        JAVA_LONG,
                        ADDRESS.withTargetLayout(JAVA_LONG)
                );
            }
        }
    }
}
