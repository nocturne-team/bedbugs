import xyz.wagyourtail.unimined.api.runs.RunConfig
import xyz.wagyourtail.unimined.mapping.Namespace

plugins {
    java
    // ModDevGradle legacyforge starts at 1.17. Unimined supports 1.16.5 on
    // our existing Gradle wrapper, including full Mojang mappings and SRG remapping.
    id("xyz.wagyourtail.unimined") version "1.4.2-SNAPSHOT"
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id")}-forge"

repositories { mavenCentral() }

unimined.minecraft {
    version(sc.current.version)
    mappings { mojmap() }
    minecraftForge {
        loader(property("deps.forge_loader") as String)
        mixinConfig("bedbugs.mixins.json")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.ow2.asm:asm:9.9.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks {
    processResources {
        val props = mapOf<String, String>(
            "id" to sc.properties["mod.id"],
            "name" to sc.properties["mod.name"],
            "version" to sc.properties["mod.version"],
            "minecraft" to sc.properties["mod.mc_compat"]
        )
        inputs.properties(props)
        filesMatching("META-INF/mods.toml") { expand(props) }
        // Exclude the shared config before renaming Forge's client-only replacement.
        filesMatching("bedbugs.mixins.json") { exclude() }
        filesMatching("bedbugs-forge.mixins.json") {
            expand("java" to "JAVA_8")
            path = "bedbugs.mixins.json"
        }
        exclude("fabric.mod.json", "bedbugs-fabric.mixins.json", "META-INF/neoforge.mods.toml", "*.ct", "*.classtweaker")
    }

    withType<Jar>().matching { it.name == "jar" || it.name == "sourcesJar" }.configureEach {
        val modId = project.property("mod.id")
        inputs.property("mod_id", modId)
        from(rootProject.file("LICENSE")) { rename { "$it-$modId" } }
    }

    jar {
        manifest.attributes("MixinConfigs" to "bedbugs.mixins.json")
    }

    test {
        useJUnitPlatform()
        // Unimined adds Minecraft only to the main source set.
        classpath += sourceSets.main.get().compileClasspath
        val productionJar = named<Jar>("remapJar").flatMap { it.archiveFile }
        val srgMinecraft = unimined.minecrafts.getValue(sourceSets.main.get()).getMinecraft(Namespace("searge")).toFile()
        inputs.file(productionJar)
        inputs.file(srgMinecraft)
        systemProperty("bedbugs.productionJar", productionJar.get().asFile.absolutePath)
        systemProperty("bedbugs.srgMinecraft", srgMinecraft.absolutePath)
    }

    withType<RunConfig>().configureEach {
        // Never open saves from the shared, newer-version development run directory.
        workingDir(rootProject.file("run/forge-1.16.5"))
        javaVersion = JavaVersion.VERSION_1_8
    }
}
