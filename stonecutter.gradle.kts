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
    description = "Builds every version/loader target and collects the jars into build/dist/ as {version}+{minecraft}-{loader}.jar"

    subprojects.forEach { sub ->
        val subVersion = sub.version.toString()
        val loaderName = sub.name.substringAfter('-')
        dependsOn("${sub.path}:jar")
        from(sub.tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
            rename { "bedbugs-$subVersion-$loaderName.jar" }
        }
    }

    into(layout.buildDirectory.dir("dist"))
}
