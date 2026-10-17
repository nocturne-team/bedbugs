package com.github._0x00acab.bedbugs.mixin;

//? if fabric {
import com.github._0x00acab.bedbugs.BedRightClickHandler;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class FabricUseBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void bedbugs$useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()) {
            return;
        }
        BedRightClickHandler.handle(player.level(), blockHit.getBlockPos(), player);
    }
}
//?}
