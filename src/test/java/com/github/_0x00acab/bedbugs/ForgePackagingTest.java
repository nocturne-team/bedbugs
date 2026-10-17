package com.github._0x00acab.bedbugs;

//? if forge {
/*import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForgePackagingTest {
    @Test
    void productionJarIsJava8AndPackagesOnlyClientMixins() throws IOException {
        try (JarFile jar = productionJar()) {
            assertEquals("bedbugs.mixins.json", jar.getManifest().getMainAttributes().getValue("MixinConfigs"));
            String config = text(jar, "bedbugs.mixins.json");
            assertTrue(config.contains("JAVA_8"));
            assertTrue(config.contains("\"client\""));
            assertFalse(config.contains("\"mixins\""), "No mixins may run on a dedicated server");
            assertTrue(config.contains("EntityTeamColorMixin"));
            assertTrue(config.contains("MinecraftGlowMixin"));
            assertTrue(config.contains("bedbugs.mixins-refmap.json"));
            assertTrue(text(jar, "META-INF/mods.toml").contains("versionRange=\"[1.16.5]\""));
            assertNotNull(jar.getJarEntry("LICENSE-bedbugs"));
            assertNotNull(jar.getJarEntry("assets/bedbugs/icon.png"));
            assertNull(jar.getJarEntry("fabric.mod.json"));
            assertNull(jar.getJarEntry("META-INF/neoforge.mods.toml"));
            assertNull(jar.getJarEntry("bedbugs-fabric.mixins.json"));
            Enumeration<JarEntry> entries = jar.entries();
            int classes = 0;
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class")) continue;
                byte[] bytes = bytes(jar, entry.getName());
                assertEquals(52, new ClassReader(bytes).readUnsignedShort(6), entry.getName());
                classes++;
            }
            assertTrue(classes > 10);
        }
    }

    @Test
    void everyProductionRefmapSelectorResolvesInActualSrgMinecraft() throws IOException {
        try (JarFile jar = productionJar(); JarFile minecraft = new JarFile(System.getProperty("bedbugs.srgMinecraft"))) {
            String refmap = text(jar, "bedbugs.mixins-refmap.json");
            Matcher selectors = Pattern.compile("\\\"L([^;]+);([^\\\"]+)\\\"").matcher(refmap);
            int count = 0;
            while (selectors.find()) {
                final String selector = selectors.group(2);
                assertTrue(selector.startsWith("func_"), selector);
                final Set<String> methods = new HashSet<>();
                new ClassReader(bytes(minecraft, selectors.group(1) + ".class")).accept(new ClassVisitor(Opcodes.ASM9) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        methods.add(name + descriptor);
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                assertTrue(methods.contains(selector), selector);
                count++;
            }
            assertEquals(3, count, "Color, glow and world teardown must all be remapped");
        }
    }

    @Test
    void fmlEntryPointAndClientSubscriberUseForge116Discovery() throws IOException {
        try (JarFile jar = productionJar()) {
            final Set<String> calls = new HashSet<>();
            final Set<String> markers = new HashSet<>();
            new ClassReader(bytes(jar, "com/github/_0x00acab/bedbugs/BedbugsForge.class")).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (!descriptor.equals("Lnet/minecraftforge/fml/common/Mod;")) return null;
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public void visit(String name, Object value) { markers.add(String.valueOf(value)); }
                    };
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (!name.equals("<init>")) return null;
                    assertEquals("()V", descriptor, "FML 36 requires the no-argument constructor");
                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            calls.add(owner + "." + name);
                        }
                    };
                }
            }, 0);
            assertTrue(markers.contains("bedbugs"));
            assertTrue(calls.contains("net/minecraftforge/fml/ModLoadingContext.registerExtensionPoint"));
            final Set<String> sides = new HashSet<>();
            new ClassReader(bytes(jar, "com/github/_0x00acab/bedbugs/BedbugsForge$ClientEvents.class")).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (!descriptor.equals("Lnet/minecraftforge/fml/common/Mod$EventBusSubscriber;")) return null;
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public AnnotationVisitor visitArray(String name) {
                            return new AnnotationVisitor(Opcodes.ASM9) {
                                @Override
                                public void visitEnum(String name, String descriptor, String value) { sides.add(value); }
                            };
                        }
                    };
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (!name.equals("onRightClickBlock")) return null;
                    assertTrue((access & Opcodes.ACC_STATIC) != 0);
                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.equals("Lnet/minecraftforge/eventbus/api/SubscribeEvent;")) markers.add("event");
                            return null;
                        }
                    };
                }
            }, ClassReader.SKIP_CODE);
            assertEquals(java.util.Collections.singleton("CLIENT"), sides);
            assertTrue(markers.contains("event"));
        }
    }

    private JarFile productionJar() throws IOException {
        return new JarFile(System.getProperty("bedbugs.productionJar"));
    }

    private static String text(JarFile jar, String name) throws IOException {
        return new String(bytes(jar, name), StandardCharsets.UTF_8);
    }

    private static byte[] bytes(JarFile jar, String name) throws IOException {
        JarEntry entry = jar.getJarEntry(name);
        assertNotNull(entry, name);
        try (InputStream input = jar.getInputStream(entry); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toByteArray();
        }
    }
}
*///?}
