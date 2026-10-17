plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3-fabric"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    // Makes version- and loader-specific properties apply from `stoncutter.properties.toml`
    properties {
        tags(version, loader)
    }

    // Adds constants to Stonecutter comments (i.e. for `//? if fabric {...`)
    constants {
        match(loader, "fabric", "neoforge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = properties.get<String>("mod.id") != "template"
    dependencies["fapi"] = properties.getOrNull<String>("deps.fabric_api") ?: "0"

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v2 named", "classTweaker v2 official")
        }
    }
}

tasks.register<Copy>("buildAllJars") {
    group = "build"
    description = "Builds every version/loader target and collects the jars into build/dist/ as bedbugs-{version}+{minecraft}-{loader}.jar"

    // Obfuscated versions (e.g. 1.21.1, 1.21.11) need their `jar` output remapped from
    // intermediary to real names via Loom's `remapJar`; the plain `jar` task there is a dev-only
    // artifact. Versions built against Mojang mappings directly (26.x) have no `remapJar` task
    // at all, so `jar` is already the real output. Pick whichever actually exists per subproject.
    subprojects.forEach { sub ->
        val subVersion = sub.version.toString()
        val loaderName = sub.name.substringAfter('-')
        val jarTaskName = if (sub.tasks.findByName("remapJar") != null) "remapJar" else "jar"
        from(sub.tasks.named(jarTaskName)) {
            rename { "bedbugs-$subVersion-$loaderName.jar" }
        }
    }

    into(layout.buildDirectory.dir("dist"))
}
