package com.github._0x00acab.bedbugs;

//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import static com.github._0x00acab.bedbugs.TemplateModCommon.*;

@Mod("template")
public class TemplateModNeoForge {
    public TemplateModNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Hello NeoForge world!");

        //? if !release
        LOGGER.warn("I'm still a template!");
    }
}
*///?}