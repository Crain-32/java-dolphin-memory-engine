package org.crain.dme4j.engine.dolphin;

import org.crain.dme4j.core.Support;
import org.crain.dme4j.core.context.ConnectionDetails;
import org.crain.dme4j.core.exception.DmeCoreModuleException;
import org.crain.dme4j.core.exception.EngineConnectionException;
import org.crain.dme4j.core.types.MemoryType;
import org.crain.dme4j.engine.AbstractReadingMemoryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

@Support("file")
class InputStreamEngine extends AbstractReadingMemoryEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamEngine.class);
    private BufferedInputStream inputStream;

    static volatile String DEFAULT_ENCODING;
    static {
        if (DEFAULT_ENCODING == null) {
            String nativeEncoding = System.getProperty("native.encoding");
            if (nativeEncoding != null) {
                try {
                    Charset nativeCharset = Charset.forName(nativeEncoding);
                    DEFAULT_ENCODING = nativeCharset.name();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    InputStreamEngine(InputStream inputStream) {
        if (inputStream instanceof BufferedInputStream bufferedInputStream) {
            this.inputStream = bufferedInputStream;
        } else {
            this.inputStream = new BufferedInputStream(inputStream, (int) Constants.MEM1_SIZE);
        }
        this.inputStream.mark((int) Constants.MEM1_SIZE);
    }

    @SuppressWarnings("unused")
    InputStreamEngine() {
    }

    @Override
    public void connect(ConnectionDetails connectionDetails) throws EngineConnectionException {
    }

    @Override
    public boolean getStatus() {
        return inputStream != null;
    }

    @Override
    public byte[] readFromRAM(long consoleAddress, int size) throws DmeCoreModuleException {
        final long strippedAddress = consoleAddress & Constants.MEM1_STRIP_START;
        if (inputStream == null) return new byte[0];
        try {
            inputStream.reset();
            var amountSkipped = inputStream.skip(strippedAddress);
            if (amountSkipped != strippedAddress) {
                throw new IllegalStateException("Could not read in enough memory");
            }
            return inputStream.readNBytes(size);
        } catch (IOException e) {
            LOGGER.error("Error while reading from file", e);
            return new byte[0];
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRead() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public boolean writeToRAM(long consoleAddress, byte[] val) {
        throw new UnsupportedOperationException("READ ONLY");
    }

    @Override
    public boolean writeFromMemoryType(long consoleAddress, MemoryType memoryType) {
        throw new UnsupportedOperationException("READ ONLY");
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

    @Override
    public void close() {
        try {
            if (this.inputStream != null) inputStream.close();
            this.inputStream = null;
        } catch (IOException e) {
            LOGGER.atError()
                    .setMessage("InputStreamEngine::close() Failed, I guess we leak memory now.")
                    .setCause(e)
                    .log();
        }
    }
}
