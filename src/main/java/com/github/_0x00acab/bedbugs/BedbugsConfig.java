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
        load();
        return glowColor;
    }

    public static void load() {
        if (!loaded) {
            loaded = true;
            glowColor = loadOrCreate(configPath(ModLoaderAccess.INSTANCE.getConfigDir()));
        }
    }

    // Writes the defaults on first run so the option is discoverable.
    static int loadOrCreate(Path path) {
        if (!Files.exists(path)) {
            try {
                save(path, DEFAULT_GLOW_COLOR);
            } catch (IOException e) {
                BedbugsMod.LOGGER.warn("Could not write " + path, e);
            }
            return DEFAULT_GLOW_COLOR;
        }
        try (InputStream in = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(in);
            String value = props.getProperty(KEY_COLOR);
            if (value == null) {
                return DEFAULT_GLOW_COLOR;
            }
            return Integer.parseInt(value.trim().replaceFirst("^#", ""), 16) & 0xFFFFFF;
        } catch (IOException | NumberFormatException e) {
            BedbugsMod.LOGGER.warn("Ignoring invalid " + path + ", using the default glow color", e);
            return DEFAULT_GLOW_COLOR;
        }
    }

    static void save(Path path, int color) throws IOException {
        Files.createDirectories(path.getParent());
        Properties props = new Properties();
        props.setProperty(KEY_COLOR, String.format("%06X", color & 0xFFFFFF));
        try (OutputStream out = Files.newOutputStream(path)) {
            props.store(out, "Bedbugs config. glowColor is a hex RGB value, e.g. 8BC34A");
        }
    }

    static Path configPath(Path configDirectory) {
        return configDirectory.resolve(FILE_NAME);
    }
}
