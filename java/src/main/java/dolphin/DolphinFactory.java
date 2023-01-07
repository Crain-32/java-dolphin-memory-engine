package dolphin;

import jna.LibNameUtil;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class DolphinFactory {

    public static DolphinEngine createEngine() throws IOException {
        createTmpFile();
        String oldJNA = System.getProperty("jna.library.path");
        System.setProperty("jna.library.path", System.getProperty("java.io.tmpdir"));
        System.setProperty("jna.debug_load", "true");
        DolphinEngine engine = new DolphinEngine();
        if (oldJNA != null) {
            System.setProperty("jna.library.path", oldJNA);
        }
        return engine;
    }


    private static void createTmpFile() throws IOException {
        String libName = LibNameUtil.getTargetLib() + LibNameUtil.getLibType();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File tmpDir;
        File tmpFile;
        try {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            tmpFile = new File(tmpDir, libName);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            inputStream = loader.getResourceAsStream(libName);
            outputStream = new FileOutputStream(tmpFile);
            System.out.println(tmpFile.getAbsolutePath());
            assert inputStream != null;
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            throw e;
        }
    }
}
