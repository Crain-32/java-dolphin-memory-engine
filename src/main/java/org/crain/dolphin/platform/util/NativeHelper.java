package org.crain.dolphin.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class NativeHelper {
    private static final Linker linker = Linker.nativeLinker();
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeHelper.class);

    public static MethodHandle findNativeMethod(String libraryName, String methodName, MemoryLayout returnLayout, MemoryLayout... args) {
        LOGGER.atDebug().setMessage("Attempting to find {}::{}(Argument Count - {}) -> Is Void - {}")
                .addArgument(libraryName)
                .addArgument(methodName)
                .addArgument(() -> (args != null && args.length > 0) ? args.length: "None")
                .addArgument(returnLayout != null)
                .log();
        SymbolLookup lookup = libraryName != null ?
                SymbolLookup.libraryLookup(libraryName, Arena.ofAuto()) : linker.defaultLookup();
        if (lookup == null) {
            throw new IllegalStateException("Failed to created a Symbol Lookup");
        }
        var methodMemorySegment = lookup.find(methodName);
        if (methodMemorySegment.isEmpty()) {
            throw new IllegalStateException("Failed to find Method %s in Library %s".formatted(methodName, libraryName));
        }
        var functionDescriptor = returnLayout == null ? FunctionDescriptor.ofVoid(args) : FunctionDescriptor.of(returnLayout, args);
        return linker.downcallHandle(methodMemorySegment.get(), functionDescriptor);
    }

    public static MethodHandle findNativeVoidMethod(String libraryName, String methodName, MemoryLayout... args) {
        return findNativeMethod(libraryName, methodName, null, args);
    }
}
