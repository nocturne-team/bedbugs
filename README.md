# Bedbugs

![Your walls, Your nightmares.](https://cdn.modrinth.com/data/cached_images/be16d69b6f6571d21f6747cfbd868addc0e8334c.png)

Highlights the monsters keeping you out of bed, right through walls. No more tearing the house apart to find the one zombie under the floor.

Download it from [Modrinth](https://modrinth.com/mod/bedbugs).

![Find them in a click.](https://cdn.modrinth.com/data/cached_images/7b62cbc72dce4e6539dc31793a2d83521ece168f.png)

Try to sleep, get the "You may not rest now; there are monsters nearby." message, and the mod marks the monsters responsible. They stay visible through walls, floors and ceilings, so you know where to dig, light up or fight.

![a screenshot of bed, and a wall with glowing outline zombies behind it, there's text that says "You may not rest now, there are monsters nearby".](https://cdn.modrinth.com/data/cached_images/8e1456001f184addfc42c3365ed105f44f0f00d5.png)

## Things to know

Bedbugs is client-side only. It works on any server and does not need to be installed there. It has no dependencies, so Fabric API is not required.

Markers only appear when the game refuses to let you sleep because of monsters. Clicking a bed in the daytime, or sleeping successfully, marks nothing.

The through-wall markers are limited to the sleep-check range, so they show the monsters that are actually keeping you awake. They fade after four seconds.

Zombified piglins only block sleep while angry at you, and servers do not tell the client which ones are. Bedbugs marks the ones in an attack pose, or all of them if nothing else nearby could be the cause.

The mod reacts to the vanilla "You may not rest now; there are monsters nearby" message. A server that replaces that message with its own text will not trigger the markers.

Shader packs and resource packs may change how the markers look.

## Supported versions

| Loader | Minecraft |
| --- | --- |
| Fabric | 1.19.4, 1.20 to 1.21.11, 26.1 to 26.3 |
| NeoForge | 1.20.4, 1.20.6, 1.21 to 1.21.11, 26.1.1 to 26.3 |
| Forge | 1.16.5 |

## Config

The marker colour is set in `config/bedbugs.properties`, which is created on first launch. `glowColor` is a hex RGB value. Restart the game after changing it.

```properties
glowColor=8BC34A
```

## Building

The project is one source tree built for every version with [Stonecutter](https://stonecutter.kikugie.dev/). Gradle downloads the JDK each target needs.

```sh
./gradlew :26.3-fabric:build   # one target
./gradlew buildAllJars         # every target, collected in build/dist/
```

`nix develop` gives a shell with the JDK 25 that Gradle itself needs to run.

## License

[CC0 1.0](LICENSE). The icon is the "bug" glyph from [Pixelarticons](https://github.com/halfmage/pixelarticons) (MIT).

Bedbugs is not affiliated with or endorsed by Mojang or Microsoft. Minecraft is a trademark of Mojang Studios.
