package org.crain.dolphin;

import org.crain.memory.AbstractGamecubeMemoryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;

abstract class AbstractDolphinMemoryEngine extends AbstractGamecubeMemoryEngine {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractDolphinMemoryEngine.class);
    protected int m_PID =-1;
    protected long m_emuRAMAddressStart = 0;
    protected long m_emuARAMAddressStart = 0;
    protected long m_MEM2AddressStart = 0;
    protected boolean m_ARAMAccessible = false;
    protected boolean m_MEM2Present = false;

    private static volatile Charset DEFAULT_CHARSET;
    private static volatile String DEFAULT_ENCODING;

    protected AbstractDolphinMemoryEngine() {
        // JNA used the defaultCharset to determine which encoding to use when
        // converting strings to native char*. The defaultCharset is set from
        // the system property file.encoding. Up to JDK 17 its value defaulted
        // to the system default encoding. From JDK 18 onwards its default value
        // changed to UTF-8.
        // JDK 18+ exposes the native encoding as the new system property
        // native.encoding, prior versions don't have that property and will
        // report NULL for it.
        // The algorithm is simple: If native.encoding is set, it will be used
        // else the original implementation of Charset#defaultCharset is used
        if (DEFAULT_CHARSET == null || DEFAULT_ENCODING == null) { // This should be fine, since volatile?
            String nativeEncoding = System.getProperty("native.encoding");
            Charset nativeCharset = null;
            if (nativeEncoding != null) {
                try {
                    nativeCharset = Charset.forName(nativeEncoding);
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
            if (nativeCharset == null) {
                nativeCharset = Charset.defaultCharset();
            }
            DEFAULT_CHARSET = nativeCharset;
            DEFAULT_ENCODING = nativeCharset.name();
        }
    }

    @Override
    protected byte[] targetPlatformByteArr(String input) {
        return input.getBytes(DEFAULT_CHARSET);
    }

    @Override
    protected String sourceBytesToPlatformString(byte[] input) {
        try {
            return new String(input, DEFAULT_ENCODING);
        } catch (Exception ex) {
            LOGGER.atError()
                    .setCause(ex)
                    .setMessage("AbstractDolphinMemoryEngine::sourceBytesToPlatformString({}) Failed")
                    .addArgument(() -> Arrays.toString(input))
                    .log();
            return null;
        }
    }
}
