package Dolphin;

import jna.LibNameUtil;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Optional;

public class DolphinFactory {

    public static DolphinEngine createEngine() {
        try {
            Optional<String> libTmpLocation = Optional.of(createTmpFile());
            String oldJNA = System.getProperty("jna.library.path");
            System.setProperty("jna.libary.path", libTmpLocation.orElseThrow(IllegalStateException::new));
            DolphinEngine engine = new DolphinEngine();
            if (oldJNA != null) {
                System.setProperty("jna.library.path", oldJNA);
            }
            return engine;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String createTmpFile() throws IOException {
        String libName = LibNameUtil.getTargetLib();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File tmpDir;
        File tmpFile;
        try {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            tmpFile = new File(tmpDir, libName);
            inputStream = ClassLoader.getSystemResourceAsStream(libName + LibNameUtil.getLibType());
            outputStream = new FileOutputStream(tmpFile);
            assert inputStream != null;
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            return tmpFile.getParentFile().getAbsolutePath();
        } catch (IOException e) {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
