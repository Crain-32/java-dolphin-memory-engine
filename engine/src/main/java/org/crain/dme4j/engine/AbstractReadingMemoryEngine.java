package org.crain.dme4j.engine;

import org.apache.commons.lang3.ArrayUtils;
import org.crain.dme4j.core.GamecubeMemoryEngine;
import org.crain.dme4j.core.exception.DmeCoreModuleException;
import org.crain.dme4j.core.exception.ExecutionException;
import org.crain.dme4j.core.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

/*
This is just incorrect.

Too much is happening regarding parsing and mappings to values.

Really all this should do is figure out the optimal queries and schedule(?) them.

Maybe review how LLVM figures out struct access? Or the JVM?

The issue is always going to be pointers. Especially field pointers.

Begs the question of if a query should return the value at the moment, or overall.

QueryBuilder Fluent API when?
 */
public abstract class AbstractReadingMemoryEngine implements GamecubeMemoryEngine {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractReadingMemoryEngine.class);

    @SuppressWarnings("unused")
    public Optional<Byte> readByteFromRAM(final long consoleAddress) {
        try {
            return Optional.of(readIntoValue(consoleAddress, new ByteValue()).getValue());
        } catch (DmeCoreModuleException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<Short> readShortFromRAM(final long consoleAddress) {
        try {
            return Optional.of(readIntoValue(consoleAddress, new ShortValue()).getValue());
        } catch (DmeCoreModuleException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<Integer> readIntegerFromRAM(final long consoleAddress) {
        try {
            return Optional.of(readIntoValue(consoleAddress, new IntValue()).getValue());
        } catch (DmeCoreModuleException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<Long> readLongFromRAM(final long consoleAddress) {
        try {
            return Optional.of(readIntoValue(consoleAddress, new LongValue()).getValue());
        } catch (DmeCoreModuleException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<Float> readFloatFromRAM(final long consoleAddress) {
        try {
            return Optional.of(readIntoValue(consoleAddress, new FloatValue()).getValue());
        } catch (DmeCoreModuleException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<Double> readDoubleFromRAM(final long consoleAddress) {
        try {
            return Optional.of(readIntoValue(consoleAddress, new DoubleValue()).getValue());
        } catch (DmeCoreModuleException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean readIntoMemoryType(final long consoleAddress, MemoryType memoryType) throws DmeCoreModuleException {
        return switch (memoryType) {
            case Value<?> value -> {
                readIntoValue(consoleAddress, value);
                yield true;
            }
            case Struct struct -> readIntoStruct(consoleAddress, struct);
            case PointerType pt when pt.pointsNull() -> {
                pt.setPointerAddress(consoleAddress);
                yield readIntoPointerTypeHelper(pt);
            }
            case Array<?> a -> readIntoArray(consoleAddress, a);
            case PointerType pt -> readIntoPointerTypeHelper(pt);
            case null -> throw new IllegalStateException("Null Provided to readIntoMemoryType!");
        };
    }

    boolean readIntoNestedPointer(final long topLevelPointerAddress, final ClassRefPointer<?> innerClassRefPointer)
            throws DmeCoreModuleException {
        if (innerClassRefPointer.pointsNull()) {
            innerClassRefPointer.setPointerAddress(
                    readLongFromRAM(topLevelPointerAddress)
                            .orElseThrow(
                                    () -> new ExecutionException("Failed to read Pointer from Address 0x%08X".formatted(topLevelPointerAddress)
                                    )
                            )
            );
        }
        return true;
    }

    private boolean readIntoStruct(final long consoleAddress, Struct struct) throws DmeCoreModuleException {
        byte[] structValues = readFromRAM(consoleAddress, struct.getSize());
        return readByteArrayIntoStruct(structValues, struct);
    }

    private boolean readByteArrayIntoStruct(byte[] memory, Struct struct) throws DmeCoreModuleException {
        int offset = 0;
        boolean result = true;
        for (var structField : struct.structFieldNames()) {
            MemoryType fieldType = struct.checkYourselfValue(structField);
            int fieldSize = fieldType.getSize();
            byte[] slice = Arrays.copyOfRange(memory, offset, offset + fieldSize);
            offset += fieldSize;
            result &= switch (fieldType) {
                case PaddingValue pv -> {
                    pv.setValue(ArrayUtils.toObject(slice));
                    yield true;
                }
                case Value<?> value -> {
                    readNumericValueFromArr(slice, value);
                    yield true;
                }
                case PointerType pt -> {
                    pt.setPointerAddress(byteArrToLong(slice));
                    yield readIntoPointerTypeHelper(pt);
                }
                default -> false;
            };
        }
        return result;
    }

    public void eagerReadIntoPointerType(PointerType type) throws DmeCoreModuleException {
        type.shouldFollowPointer(true);
        readIntoPointerTypeHelper(type);
    }

    private boolean readIntoPointerTypeHelper(PointerType pointer) throws DmeCoreModuleException {
        if (pointer == null || pointer.pointsNull()) throw new IllegalStateException("Null Pointer!");
        if (!pointer.willFollowPointer()) return true;
        boolean result = switch (pointer) {
            case StringPointer sp -> readIntoStringPointer(sp);
            case ClassRefPointer<?> p -> readIntoPointer(p);
        };
        pointer.clearFollow();
        return result;
    }

    private boolean readIntoPointer(final ClassRefPointer<? extends MemoryType> classRefPointer) throws DmeCoreModuleException {
        final long pointerAddress = classRefPointer.getPointerAddress();
        final MemoryType unknown = classRefPointer.getValue(); // Pointer can't be created with null inner
        return switch (unknown) {
            case Struct s -> readIntoStruct(pointerAddress, s);
            case PointerType pt when pt.pointsNull() -> {
                pt.setPointerAddress(pointerAddress);
                yield readIntoPointerTypeHelper(pt);
            }
            case PointerType pt -> readIntoPointerTypeHelper(pt);
            case Array<?> a -> readIntoArray(pointerAddress, a);
            case Value<?> value -> {
                readIntoValue(pointerAddress, value);
                yield true;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private boolean readIntoArray(final long consoleAddress, Array<?> array) throws DmeCoreModuleException {
        if (array.isForPointers()) {
            //Fine to assume cast this because isForPointers() will check for us
            return readIntoPointerArray(consoleAddress, (Array<PointerType>) array);
        } else {
            return readIntoValueArray(consoleAddress, (Array<MemoryType>) array);
        }
    }

    private boolean readIntoValueArray(final long consoleAddress, Array<MemoryType> array) throws DmeCoreModuleException {
        boolean result = true;
        byte[] gameMemory = readFromRAM(consoleAddress, array.getSize());
        int offset = 0;
        for (int index = 0; index < array.getLength(); index++) {
            MemoryType element = array.getAtIndex(index);
            byte[] elementMemory = Arrays.copyOfRange(gameMemory, offset, offset + element.getSize());
            offset += element.getSize();
            result &= switch (element) {
                case PaddingValue pv -> {
                    pv.setValue(ArrayUtils.toObject(elementMemory));
                    yield true;
                }
                case Value<?> value -> {
                    readNumericValueFromArr(elementMemory, value);
                    yield true;
                }
                case PointerType pt -> {
                    pt.setPointerAddress(byteArrToLong(elementMemory));
                    yield readIntoPointerTypeHelper(pt);
                }
                default -> false;
            };
        }
        return result;
    }

    private boolean readIntoPointerArray(final long consoleAddress, Array<PointerType> array) throws DmeCoreModuleException {
        byte[] toRead = readFromRAM(consoleAddress, array.getSize());
        int lastArrIndex = 0;
        for (int index = 0; index < array.getLength(); index++) {
            int lowerBound = lastArrIndex * PointerType.getPointerSize();
            int upperBound = index * PointerType.getPointerSize();
            byte[] slice = Arrays.copyOfRange(toRead, lowerBound, upperBound);
            lastArrIndex += 1;
            PointerType pointerType = array.getAtIndex(index);
            pointerType.setPointerAddress(byteArrToLong(slice));
            readIntoPointerType(pointerType);
        }
        return true;
    }


    private boolean readIntoStringPointer(StringPointer stringPointer) throws DmeCoreModuleException {
        if (stringPointer.getMaxLength() > 0) {
            var memoryValue = readFromRAM(stringPointer.getPointerAddress(), stringPointer.getMaxLength());
            stringPointer.setStringValue(sourceBytesToPlatformString(memoryValue));
            return true;
        } else {
            throw new IllegalStateException("Maximum Length must be greater than zero!");
        }
    }

    private <T> Value<T> readIntoValue(final long consoleAddress, Value<T> value) throws DmeCoreModuleException {
        byte[] gameMemory = readFromRAM(consoleAddress, value.getSize());
        if (value instanceof PaddingValue) {
            //noinspection unchecked
            return (Value<T>) PaddingValue.of(
                    ArrayUtils.toObject(gameMemory)
            );
        }
        readNumericValueFromArr(gameMemory, value);
        return value;
    }

    protected abstract String sourceBytesToPlatformString(byte[] input);

    private void readNumericValueFromArr(byte[] inputArr, Value<?> basicValue) throws IllegalArgumentException {
        LOGGER.atTrace().setMessage("readNumberFromArr({},{})")
                .addArgument(() -> Arrays.toString(inputArr))
                .addArgument(() -> basicValue.getClass().getSimpleName())
                .log();
        if (inputArr == null || inputArr.length == 0 || inputArr.length > 16) {
            throw new IllegalArgumentException("Invalid Input Array for AbstractGamecubeMemoryEngine");
        } else if (basicValue.getSize() > 16 || basicValue.getSize() == 0) {
            throw new IllegalArgumentException("Illegal Size passed in from BasicValue Parameter");
        }
        long result = byteArrToLong(inputArr);
        switch (basicValue) {
            case ByteValue bv -> bv.setValue((byte) result);
            case ShortValue sval -> sval.setValue((short) result);
            case IntValue ival -> ival.setValue((int) result);
            case LongValue lval -> lval.setValue(result);
            case FloatValue fval -> fval.setValue(Float.intBitsToFloat((int) result));
            case DoubleValue dval -> dval.setValue(Double.longBitsToDouble(result));
            default ->
                    throw new IllegalArgumentException("Value Type of " + basicValue.getClass().getSimpleName() + " is not supported!");
        }
    }

    static long byteArrToLong(byte[] arr) {
        long result = 0L;
        for (int index = 0; index < arr.length; index++) {
            final long current = result;
            final int finalIndex = index;
            LOGGER.atTrace().setMessage("current: 0x{}, index: {}, toAdd: 0x{}")
                    .addArgument(() -> Long.toHexString(current))
                    .addArgument(index)
                    .addArgument(() -> Integer.toHexString(arr[finalIndex]))
                    .log();
            result = result << 8;
            result |= (arr[index] & 0xFF);
        }
        return result;
    }
}
