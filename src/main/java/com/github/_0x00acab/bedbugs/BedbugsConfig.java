package com.github._0x00acab.bedbugs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BedbugsConfig {
    public static final int DEFAULT_GLOW_COLOR = 0x8BC34A;
    private static final String FILE_NAME = "bedbugs.properties";
    private static final String KEY_COLOR = "glowColor";
    private static int glowColor = DEFAULT_GLOW_COLOR;
    private static boolean loaded = false;

    private BedbugsConfig() {
    }

    public static int getGlowColor() {
        ensureLoaded();
        return glowColor;
    }

    public static void setGlowColor(int color) {
        glowColor = color;
        save();
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        Path path = configPath();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                Properties props = new Properties();
                props.load(in);
                String value = props.getProperty(KEY_COLOR);
                if (value != null) {
                    glowColor = Integer.parseInt(value.trim().replaceFirst("^#", ""), 16);
                }
            } catch (Exception ignored) {
                glowColor = DEFAULT_GLOW_COLOR;
            }
        }
    }

    private static void save() {
        try {
            Path path = configPath();
            Files.createDirectories(path.getParent());
            Properties props = new Properties();
            props.setProperty(KEY_COLOR, String.format("%06X", glowColor & 0xFFFFFF));
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Bedbugs config");
            }
        } catch (IOException ignored) {
        }
    }

    private static Path configPath() {
        return configPath(ModLoaderAccess.INSTANCE.getConfigDir());
    }

    static Path configPath(Path configDirectory) {
        return configDirectory.resolve(FILE_NAME);
    }
}
