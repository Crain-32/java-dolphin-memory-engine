package org.crain.dme4j.engine;

import org.crain.dme4j.core.exception.DmeCoreModuleException;
import org.crain.dme4j.core.types.*;

import java.nio.ByteBuffer;

public abstract class AbstractWritingMemoryEngine extends AbstractReadingMemoryEngine {
    public boolean writeByteToRAM(final long consoleAddress, final byte val) {
        try {
            return writeFromMemoryType(consoleAddress, new ByteValue(val));
        } catch (final DmeCoreModuleException e) {
            // TODO - add logging
            return false;
        }
    }

    public boolean writeShortToRAM(final long consoleAddress, final short val) {
        try {
            return writeFromMemoryType(consoleAddress, new ShortValue(val));
        } catch (final DmeCoreModuleException e) {
            // TODO - add logging
            return false;
        }
    }

    public boolean writeIntegerToRAM(final long consoleAddress, final int val) {
        try {
            return writeFromMemoryType(consoleAddress, new IntValue(val));
        } catch (final DmeCoreModuleException e) {
            // TODO - add logging
            return false;
        }
    }

    public boolean writeLongToRAM(final long consoleAddress, final long val) {
        try {
            return writeFromMemoryType(consoleAddress, new LongValue(val));
        } catch (final DmeCoreModuleException e) {
            // TODO - add logging
            return false;
        }
    }

    public boolean writeFloatToRAM(final long consoleAddress, final float val) {
        try {
            return writeFromMemoryType(consoleAddress, new FloatValue(val));
        } catch (final DmeCoreModuleException e) {
            // TODO - add logging
            return false;
        }
    }

    public boolean writeDoubleToRAM(final long consoleAddress, final double val) {
        try {
            return writeFromMemoryType(consoleAddress, new DoubleValue(val));
        } catch (final DmeCoreModuleException e) {
            // TODO - add logging
            return false;
        }
    }

    @Override
    public boolean writeFromMemoryType(long consoleAddress, MemoryType memoryType) throws DmeCoreModuleException {
        return switch (memoryType) {
            case Value<?> value -> {
                var buffer = ByteBuffer.allocate(value.getSize());
                writeValueToBuffer(buffer, value);
                yield writeToRAM(consoleAddress, buffer.array());
            }
            case PointerType pointer when pointer.willFollowPointer() -> writePointerType(consoleAddress, pointer);
            case PointerType _ -> true;
            case Struct struct -> writeStruct(consoleAddress, struct);
            case Array<?> array -> {
                var buffer = ByteBuffer.allocate(array.getSize());
                evaluateArrayIntoBuffer(buffer, array);
                yield writeToRAM(consoleAddress, buffer.array());
            }
        };
    }

    public void eagerWriteIntoPointerType(PointerType type) throws DmeCoreModuleException {
        type.shouldFollowPointer(true);
        writeIntoPointerType(type);
    }

    private boolean writePointerType(final long consoleAddress, final PointerType pointerType) throws DmeCoreModuleException{
        if (pointerType.pointsNull()) {
            pointerType.setPointerAddress(consoleAddress);
        }
        boolean result = switch (pointerType) {
            case StringPointer sp -> writeString(sp);
            case ClassRefPointer<?> pointer -> writeFromMemoryType(pointer.getPointerAddress(), pointer.getValue());
        };
        pointerType.clearFollow();
        return result;
    }

    private boolean writeStruct(final long consoleAddress, final Struct struct) {
        var buffer = ByteBuffer.allocate(struct.getSize());
        evaluateStructIntoBuffer(buffer, struct);
        try {
            return writeToRAM(consoleAddress, buffer.array());
        } catch (DmeCoreModuleException dmeCoreModuleException) {
            // TODO, Log this
            return false;
        }
    }

    private void evaluateStructIntoBuffer(ByteBuffer buffer, Struct struct) {
        for (var structField : struct.structFieldNames()) {
            MemoryType fieldType = struct.checkYourselfValue(structField);
            switch (fieldType) {
                case PaddingValue pv -> buffer.put(new byte[pv.getSize()]);
                case Value<?> value -> writeValueToBuffer(buffer, value);
                case StringPointer sp -> {
                    writeValueToBuffer(buffer, new IntValue((int) sp.getPointerAddress()));
                    writeString(sp);
                }
                case PointerType pt -> writeValueToBuffer(buffer, new IntValue((int) pt.getPointerAddress()));
                case Array<?> array -> evaluateArrayIntoBuffer(buffer, array);
                case Struct subStruct -> evaluateStructIntoBuffer(buffer, subStruct);
            }
        }
    }

    private void evaluateArrayIntoBuffer(ByteBuffer buffer, Array<?> input) {
        switch (input) {
            case Array<?> array when array.isForPointers() -> {
                //noinspection unchecked
                var castedArray = (Array<PointerType>) array;
                for (int index = 0; index < array.getLength(); index++) {
                    writeValueToBuffer(buffer, new IntValue(
                            (int) castedArray.getAtIndex(index).getPointerAddress())
                    );
                }
            }
            case Array<?> array when (array.getAtIndex(0) instanceof Value<?>) -> {
                //noinspection unchecked
                var castedArray = (Array<Value<?>>) array;
                for (int index = 0; index < array.getLength(); index++) {
                    writeValueToBuffer(buffer, castedArray.getAtIndex(index));
                }
            }
            case Array<?> array -> { // has to be Struct
                //noinspection unchecked
                var castedArray = (Array<Struct>) array;
                for (int index = 0; index < array.getLength(); index++) {
                    evaluateStructIntoBuffer(buffer, castedArray.getAtIndex(index));
                }
            }
        }
    }

    private boolean writeString(StringPointer str) {
        byte[] toWrite = targetPlatformByteArr(str.getStringValue());
        try {
            return writeToRAM(str.getPointerAddress(), toWrite);
        } catch (DmeCoreModuleException dmeCoreModuleException) {
            // TODO - Log this exception
            return false;
        }
    }

    private void writeValueToBuffer(ByteBuffer buffer, Value<?> value) {
        switch (value) {
            case ByteValue bv -> buffer.put(bv.getValue());
            case ShortValue sval -> buffer.putShort(sval.getValue());
            case IntValue ival -> buffer.putInt(ival.getValue());
            case LongValue lval -> buffer.putLong(lval.getValue());
            case FloatValue fval -> buffer.putFloat(fval.getValue());
            case DoubleValue dval -> buffer.putDouble(dval.getValue());
            default -> throw new IllegalArgumentException(
                    "Value Type of " + value.getClass().getSimpleName() + " is not supported!"
            );
        }
    }

    protected abstract byte[] targetPlatformByteArr(String input);
}
