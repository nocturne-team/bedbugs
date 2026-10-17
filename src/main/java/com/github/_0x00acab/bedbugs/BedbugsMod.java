package com.github._0x00acab.bedbugs;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedbugsMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("bedbugs");
    public static final String VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MINECRAFT = /*$ minecraft*/ "26.2";

    /** Adapts to the {@link Identifier} changes introduced in 1.21. */
    public static Identifier id(String namespace, String path) {
        //? if <1.21 {
        /*return new Identifier(namespace, path);
         *///?} else
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
}
