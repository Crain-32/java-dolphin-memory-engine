package org.crain.memory.engine.dolphin;

import java.io.*;

class FileMemoryEngine extends AbstractDolphinMemoryEngine {
    private InputStream inputStream;
    private OutputStream outputStream;

    FileMemoryEngine(String filePath) throws FileNotFoundException {
        this.inputStream = new FileInputStream(filePath);
        this.outputStream = new FileOutputStream(filePath);
    }
    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean getStatus() {
        return false;
    }

    @Override
    public byte[] readFromRAM(long consoleAddress, int size) {
        return new byte[0];
    }

    @Override
    public boolean writeToRAM(long consoleAddress, byte[] val) {
        return false;
    }
}
