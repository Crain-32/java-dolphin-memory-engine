package org.crain.dme4j.core.types;

/**
 * {@link Value}
 */
public final class IntValue extends Value<Integer> {

    /**
     * Default value is 0
     */
    public IntValue() {
        super(0, 4);
    }

    public IntValue(Integer value) {
        super(value, 4);
    }


}
