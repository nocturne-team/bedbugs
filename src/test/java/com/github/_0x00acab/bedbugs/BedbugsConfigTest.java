package com.github._0x00acab.bedbugs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BedbugsConfigTest {
    @TempDir
    Path directory;

    @Test
    void configPathUsesProvidedLoaderDirectory() {
        Path configDirectory = directory.resolve("custom-loader-config");

        assertEquals(configDirectory.resolve("bedbugs.properties"), BedbugsConfig.configPath(configDirectory));
    }

    @Test
    void firstLoadWritesTheDefaultFile() throws IOException {
        Path path = directory.resolve("config").resolve("bedbugs.properties");

        assertEquals(BedbugsConfig.DEFAULT_GLOW_COLOR, BedbugsConfig.loadOrCreate(path));
        assertTrue(new String(Files.readAllBytes(path), "UTF-8").contains("glowColor=8BC34A"));
    }

    @Test
    void savedColorRoundTrips() throws IOException {
        Path path = directory.resolve("bedbugs.properties");
        BedbugsConfig.save(path, 0xFF00AA);

        assertEquals(0xFF00AA, BedbugsConfig.loadOrCreate(path));
    }

    @Test
    void hashPrefixIsAcceptedAndGarbageFallsBackToDefault() throws IOException {
        Path path = directory.resolve("bedbugs.properties");

        Files.write(path, Collections.singletonList("glowColor=#112233"));
        assertEquals(0x112233, BedbugsConfig.loadOrCreate(path));

        Files.write(path, Collections.singletonList("glowColor=green"));
        assertEquals(BedbugsConfig.DEFAULT_GLOW_COLOR, BedbugsConfig.loadOrCreate(path));
    }
}
