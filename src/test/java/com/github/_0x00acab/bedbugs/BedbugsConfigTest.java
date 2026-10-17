package com.github._0x00acab.bedbugs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BedbugsConfigTest {
    @TempDir
    Path directory;

    @Test
    void configPathUsesProvidedLoaderDirectory() {
        Path configDirectory = directory.resolve("custom-loader-config");

        assertEquals(configDirectory.resolve("bedbugs.properties"), BedbugsConfig.configPath(configDirectory));
    }
}
