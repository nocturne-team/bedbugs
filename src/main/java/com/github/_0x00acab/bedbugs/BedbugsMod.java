package com.github._0x00acab.bedbugs;

//? if >=1.21
import net.minecraft.resources.Identifier;
//? if <1.21
/*import net.minecraft.resources.ResourceLocation;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedbugsMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("bedbugs");
    public static final String VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MINECRAFT = /*$ minecraft*/ "26.3";

    /** Adapts to the {@link Identifier} changes introduced in 1.21. */
    //? if >=1.21 {
    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
    //?} else {
    /*public static ResourceLocation id(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }*/
    //?}
}
