package dev.zenqrt.clownchase.utils.file;

import dev.zenqrt.clownchase.ClownChasePlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class MyFileUtils {

    private MyFileUtils() {}

    public static void copyResourceFolder(String folder, Path targetDir) throws IOException {
        CodeSource source = ClownChasePlugin.class.getProtectionDomain().getCodeSource();

        if (source == null)
            throw new IllegalStateException("Unable to access plugin jar");

        try (ZipInputStream zip = new ZipInputStream(source.getLocation().openStream())) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();

                if (!name.startsWith(folder + "/"))
                    continue;

                Path outputPath = targetDir.resolve(name.substring(folder.length() + 1));

                if (entry.isDirectory()) {
                    Files.createDirectories(outputPath);
                    continue;
                }

                Files.createDirectories(outputPath.getParent());

                try (OutputStream out = Files.newOutputStream(outputPath)) {
                    zip.transferTo(out);
                }
            }
        }
    }
}
