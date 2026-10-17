package com.github._0x00acab.bedbugs.mixin;

import com.github._0x00acab.bedbugs.HighlightTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftGlowMixin {
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void bedbugs$shouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (HighlightTracker.INSTANCE.isHighlighted(entity.getId())) {
            cir.setReturnValue(true);
        }
    }
}
