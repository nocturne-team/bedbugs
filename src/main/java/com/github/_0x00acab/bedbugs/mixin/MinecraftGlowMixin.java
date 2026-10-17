package com.github._0x00acab.bedbugs.mixin;

import com.github._0x00acab.bedbugs.HighlightTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftGlowMixin {
    // Both world replacement and teardown pass through here, including disconnects
    // that bypass setLevel. Newer versions delegate to the two-argument overload.
    //? if >=1.21.11 {
    @Inject(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At("HEAD"))
    private void bedbugs$onLevelChanged(ClientLevel level, boolean stopSound, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;)V", at = @At("HEAD"))
    private void bedbugs$onLevelChanged(ClientLevel level, CallbackInfo ci) {
    *///?}
        HighlightTracker.INSTANCE.ensureLevel(level);
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void bedbugs$shouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (HighlightTracker.INSTANCE.isHighlighted(entity.getId())) {
            cir.setReturnValue(true);
        }
    }
}
