package com.yitiaojiayu.easylist;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
@SuppressWarnings({"unused", "java:S100", "java:S106", "java:S108", "java:S112", "java:S1075", "java:S2259", "java:S3776", "AlibabaLowerCamelCaseVariableNaming"})
final class EasyListNative {
/*
                src/main/resources/
                └── natives/
                    ├── windows_x86_64/
                    │   └── easylist.dll
                    ├── linux_x86_64/
                    │   └── lib_easylist.so
                    ├── macos_x86_64/
                    │   └── lib_easylist.dylib
                    └── macos_aarch64/
                        └── lib_easylist.dylib
*/
    private EasyListNative(){
    }

    static {
        try {
            loadNativeLibrary();
            init();
        } catch (Exception e) {
            System.err.println("Load libraries failed: easylist.\n" + e);
            throw new UnsatisfiedLinkError("Failed to load native library: " + e.getMessage());
        }
    }

    private static void loadNativeLibrary() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String osArch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        // Base name
        String libName = "easylist";
        String libExtension;
        String platformDir;

        // Confirm OS
        if (osName.contains("win")) {
            platformDir = "windows_";
            libExtension = ".dll";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            platformDir = "linux_";
            libExtension = ".so";
            libName = "lib_" + libName;
        } else if (osName.contains("mac")) {
            platformDir = "macos_";
            libExtension = ".dylib";
            libName = "lib_" + libName;
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }


        // Confirm Arch
        if ("amd64".equals(osArch) || "x86_64".equals(osArch)) {
            platformDir += "x86_64";
        } else if ("aarch64".equals(osArch) || "arm64".equals(osArch)) {
            platformDir += "aarch64";
        } else {
            throw new UnsupportedOperationException("Unsupported architecture: " + osArch);
        }

        String resourcePath = "/natives/" + platformDir + "/" + libName + libExtension;

        InputStream libStream = EasyListNative.class.getResourceAsStream(resourcePath);
        if (libStream == null) {
            throw new RuntimeException("Cannot find native library resource: " + resourcePath);
        }

        Path tempFile = null;
        try {
            // Create temp file
            tempFile = Files.createTempFile(libName, libExtension);
            // Copy lib to temp file
            Files.copy(libStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            if (tempFile != null && Files.exists(tempFile)) {
                try { Files.delete(tempFile); } catch (Exception ignored) {}
            }
            throw new RuntimeException("Failed to extract native library to temporary file", e);
        } finally {
            try { libStream.close(); } catch (Exception ignored) {}
        }

        // load lib
        try {
            System.load(tempFile.toAbsolutePath().toString());
        } finally {
            // delete temp file when JVM exit
            tempFile.toFile().deleteOnExit();
        }
    }

    static native void init();
    static native int new_object();
    static native int size(int id);
    static native boolean is_empty(int id);
}
