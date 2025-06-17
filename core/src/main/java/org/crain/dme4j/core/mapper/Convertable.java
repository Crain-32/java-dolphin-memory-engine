package org.crain.dme4j.core.mapper;

/**
 * Need to prevent circular converting methods. This permits Converters to specify <em>why</em> they're able to convert
 * a type, instead of just if they're able to.
 */
public enum Convertable {
    /**
     * This Converter directly defines the conversion.
     */
    DIRECT(true),
    /**
     * This Converter knows of a converter that can handle the conversion, so you
     * don't have to find the most direct converter.
     */
    DELEGATE(true),
    /**
     * This Converter uses one or more Converters in order to handle conversion.
     */
    COMPOSITE(true),
    /**
     * This Converter cannot convert the provided type.
     */
    NONCONVERTABLE(false);

    private final boolean convertibility;

    Convertable(final boolean convertibility) {
        this.convertibility = convertibility;
    }

    /**
     * @return High level response for "can this converter convert the object?"
     */
    public boolean isConvertable() {
        return convertibility;
    }
}
