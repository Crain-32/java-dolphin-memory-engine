package org.crain.memory.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaddingValue extends Value<Byte[]> {
    Logger logger = LoggerFactory.getLogger(PaddingValue.class);

    public PaddingValue(final Byte[] value) {
        super(value, value.length);
    }

    public PaddingValue(final int length) {
        super(new Byte[length], length);
    }

    @Override
    public void setValue(Byte[] value) {
        if (value.length != getSize()) {
            logger.atTrace()
                    .setMessage("Mismatching Buffer value passed into PaddingValue::setValue {}")
                    .addArgument(value.length)
                    .log();
        }
        // We don't actually set any values in Padding.
    }

    @Override
    public String getRepresentation() {
        return "Padding of Length: " + getSize();
    }
}
