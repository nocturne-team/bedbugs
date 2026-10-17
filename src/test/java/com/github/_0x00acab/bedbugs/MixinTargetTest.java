package com.github._0x00acab.bedbugs;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Checks actual Minecraft bytecode without initializing the game or relying on compilation alone. */
class MixinTargetTest {
    @Test
    void metadataUsesTheLoadersDiscoveryPath() {
        ClassLoader loader = getClass().getClassLoader();
        //? if fabric {
        assertNotNull(loader.getResource("fabric.mod.json"));
        assertNull(loader.getResource("META-INF/mods.toml"));
        assertNull(loader.getResource("META-INF/neoforge.mods.toml"));
        //?} elif forge {
        /*assertNotNull(loader.getResource("META-INF/mods.toml"));
        assertNull(loader.getResource("META-INF/neoforge.mods.toml"));
        assertNull(loader.getResource("fabric.mod.json"));
        *///?} else {
        /*String metadata = BedbugsMod.MINECRAFT.equals("1.20.4") ? "mods.toml" : "neoforge.mods.toml";
        String otherMetadata = BedbugsMod.MINECRAFT.equals("1.20.4") ? "neoforge.mods.toml" : "mods.toml";
        assertNotNull(loader.getResource("META-INF/" + metadata));
        assertNull(loader.getResource("META-INF/" + otherMetadata));
        assertNull(loader.getResource("fabric.mod.json"));
        *///?}
    }

    @Test
    void everyInjectionMatchesMinecraftAndItsCallbackArguments() throws IOException {
        checkMixin("EntityTeamColorMixin");
        checkMixin("MinecraftGlowMixin");
        //? if fabric
        checkMixin("FabricUseBlockMixin");
    }

    private void checkMixin(String name) throws IOException {
        List<String> targets = new ArrayList<>();
        List<Injection> injections = new ArrayList<>();
        readClass("com/github/_0x00acab/bedbugs/mixin/" + name).accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (!descriptor.equals("Lorg/spongepowered/asm/mixin/Mixin;")) return null;
                return new AnnotationVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitArray(String key) {
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @Override
                            public void visit(String key, Object value) {
                                targets.add(((Type) value).getInternalName());
                            }
                        };
                    }
                };
            }

            @Override
            public MethodVisitor visitMethod(int access, String method, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String annotation, boolean visible) {
                        if (!annotation.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) return null;
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @Override
                            public AnnotationVisitor visitArray(String key) {
                                if (!key.equals("method")) return null;
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    public void visit(String key, Object selector) {
                                        injections.add(new Injection((String) selector, descriptor));
                                    }
                                };
                            }
                        };
                    }
                };
            }
        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        assertEquals(1, targets.size(), name);
        assertFalse(injections.isEmpty(), name);
        Set<String> methods = new HashSet<>();
        readClass(targets.get(0)).accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String method, String descriptor, String signature, String[] exceptions) {
                methods.add(method + descriptor);
                return null;
            }
        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        for (Injection injection : injections) {
            List<String> matches = methods.stream().filter(method -> injection.selector.contains("(")
                ? method.equals(injection.selector) : method.startsWith(injection.selector + "(")).collect(Collectors.toList());
            assertEquals(1, matches.size(), name + ": " + injection.selector);
            String targetDescriptor = matches.get(0).substring(matches.get(0).indexOf('('));
            Type[] targetArgs = Type.getArgumentTypes(targetDescriptor);
            Type[] callbackArgs = Type.getArgumentTypes(injection.callbackDescriptor);
            assertEquals(targetArgs.length + 1, callbackArgs.length, name);
            for (int i = 0; i < targetArgs.length; i++) assertEquals(targetArgs[i], callbackArgs[i], name);
            String callbackType = Type.getReturnType(targetDescriptor).equals(Type.VOID_TYPE)
                ? "CallbackInfo" : "CallbackInfoReturnable";
            assertEquals("org.spongepowered.asm.mixin.injection.callback." + callbackType,
                callbackArgs[callbackArgs.length - 1].getClassName(), name);
        }
    }

    private ClassReader readClass(String name) throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(stream, name);
            return new ClassReader(stream);
        }
    }

    private static final class Injection {
        private final String selector;
        private final String callbackDescriptor;

        private Injection(String selector, String callbackDescriptor) {
            this.selector = selector;
            this.callbackDescriptor = callbackDescriptor;
        }
    }
}
