package com.github._0x00acab.bedbugs;

//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.InteractionHand;

@Mod("bedbugs")
public final class BedbugsForge {
    public BedbugsForge() {
        // 1.16.5 predates the mods.toml displayTest setting. Accept remote
        // servers without Bedbugs, and ignore accidental server installations.
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
            () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (version, network) -> true));
        BedbugsMod.LOGGER.info("Bedbugs loaded");
    }

    // FML discovers this subscriber only on clients. The @Mod constructor has
    // no client-class references, so an accidental server installation is safe.
    @Mod.EventBusSubscriber(modid = "bedbugs", value = Dist.CLIENT)
    public static final class ClientEvents {
        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (event.getHand() != InteractionHand.MAIN_HAND || event.getPlayer().isShiftKeyDown()) {
                return;
            }
            BedRightClickHandler.handle(event.getWorld(), event.getPos(), event.getPlayer());
        }
    }
}
*///?}
