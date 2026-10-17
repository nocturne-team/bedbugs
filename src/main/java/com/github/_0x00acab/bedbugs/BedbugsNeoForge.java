package com.github._0x00acab.bedbugs;

//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static com.github._0x00acab.bedbugs.BedbugsMod.*;

@Mod("bedbugs")
public class BedbugsNeoForge {
    public BedbugsNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Bedbugs loaded");
        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock event) ->
            BedRightClickHandler.handle(event.getLevel(), event.getPos(), event.getEntity()));
    }
}
*///?}
