package org.crain.memory;

import org.apache.commons.lang3.ArrayUtils;
import org.crain.memory.types.basic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class AbstractGamecubeMemoryEngine implements GamecubeMemoryEngine {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractGamecubeMemoryEngine.class);

    public Byte readByteFromRAM(final long consoleAddress) {
        return readIntoValue(consoleAddress, new ByteValue()).getValue();
    }

    public boolean writeByteToRAM(final long consoleAddress, final byte val) {
        return writeFromMemoryType(consoleAddress, new ByteValue(val));
    }

    public Short readShortFromRAM(final long consoleAddress) {
        return readIntoValue(consoleAddress, new ShortValue()).getValue();
    }

    public boolean writeShortToRAM(final long consoleAddress, final short val) {
        return writeFromMemoryType(consoleAddress, new ShortValue(val));
    }

    public Integer readIntegerFromRAM(final long consoleAddress) {
        return readIntoValue(consoleAddress, new IntValue()).getValue();
    }

    public boolean writeIntegerToRAM(final long consoleAddress, final int val) {
        return writeFromMemoryType(consoleAddress, new IntValue(val));
    }

    public Long readLongFromRAM(final long consoleAddress) {
        return readIntoValue(consoleAddress, new LongValue()).getValue();
    }

    public boolean writeLongToRAM(final long consoleAddress, final long val) {
        return writeFromMemoryType(consoleAddress, new LongValue(val));
    }

    public Float readFloatFromRAM(final long consoleAddress) {
        return readIntoValue(consoleAddress, new FloatValue()).getValue();
    }

    public boolean writeFloatToRAM(final long consoleAddress, final float val) {
        return writeFromMemoryType(consoleAddress, new FloatValue(val));
    }

    public Double readDoubleFromRAM(final long consoleAddress) {
        return readIntoValue(consoleAddress, new DoubleValue()).getValue();
    }

    public boolean writeDoubleToRAM(final long consoleAddress, final double val) {
        return writeFromMemoryType(consoleAddress, new DoubleValue(val));
    }

    @Override
    public boolean readIntoMemoryType(long consoleAddress, MemoryType memoryType) {
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
            case PointerType pt -> readIntoPointerTypeHelper(pt);
            case null -> throw new IllegalStateException("Null Provided to readIntoMemoryType!");
        };
    }

    @Override
    public boolean writeFromMemoryType(long consoleAddress, MemoryType memoryType) {
        return false;
    }


    private boolean readIntoStruct(final long consoleAddress, Struct struct) {
        byte[] structValues = readFromRAM(consoleAddress, struct.getSize());
        int offset = 0;
        boolean result = true;
        for (var structField : struct.structFieldNames()) {
            MemoryType fieldType = struct.getYourselfValue(structField);
            int fieldSize = fieldType.getSize();
            byte[] slice = Arrays.copyOfRange(structValues, offset, offset + fieldSize);
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

    private boolean readIntoPointerTypeHelper(PointerType pointer) {
        if (pointer == null || pointer.pointsNull()) throw new IllegalStateException("Null Pointer!");
        return switch (pointer) {
            case StringPointer sp -> readIntoStringPointer(sp);
            case Pointer<?> p -> readIntoPointer(p);
        };
    }

    private boolean readIntoPointer(Pointer<? extends MemoryType> pointer) {
        final long pointerAddress = pointer.getPointerAddress();
        MemoryType unknown = pointer.getValue(); // Pointer can't be created with null inner
        return switch (unknown) {
            case Struct s -> readIntoStruct(pointerAddress, s);
            case PointerType pt when pt.pointsNull() -> {
                pt.setPointerAddress(pointerAddress);
                yield readIntoPointerTypeHelper(pt);
            }
            case PointerType pt -> readIntoPointerTypeHelper(pt);
            case Value<?> value -> {
                readIntoValue(pointerAddress, value);
                yield true;
            }
        };
    }


    private boolean readIntoStringPointer(StringPointer stringPointer) {
        if (stringPointer.getMaxLength() > 0) {
            var memoryValue = readFromRAM(stringPointer.getPointerAddress(), stringPointer.getMaxLength());
            stringPointer.setStringValue(sourceBytesToPlatformString(memoryValue));
            return true;
        } else {
            throw new IllegalStateException("Maximum Length must be greater than zero!");
        }
    }

    private <T> Value<T> readIntoValue(final long consoleAddress, Value<T> value) {
        byte[] gameMemory = readFromRAM(consoleAddress, value.getSize());
        if (value instanceof PaddingValue) {
            //noinspection unchecked
            return (Value<T>) new PaddingValue(
                    ArrayUtils.toObject(gameMemory)
            );
        }
        readNumericValueFromArr(gameMemory, value);
        return value;
    }

    protected abstract byte[] targetPlatformByteArr(String input);
    protected abstract String sourceBytesToPlatformString(byte[] input);

    private void readNumericValueFromArr(byte[] inputArr, Value<?> basicValue) throws IllegalArgumentException {
        LOGGER.atTrace().setMessage("AbstractGamecubeMemoryEngine::readNumberFromArr({},{})")
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
            default -> throw new IllegalArgumentException("Value Type of " + basicValue.getClass().getSimpleName() + " is not supported!");
        };
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
