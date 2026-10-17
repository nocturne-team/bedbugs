package com.github._0x00acab.bedbugs.mixin;

import com.github._0x00acab.bedbugs.HighlightTracker;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityTeamColorMixin {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void bedbugs$getTeamColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        if (!self.level().isClientSide()) {
            return;
        }
        if (HighlightTracker.INSTANCE.isHighlighted(self.getId())) {
            cir.setReturnValue(com.github._0x00acab.bedbugs.BedbugsConfig.getGlowColor());
        }
    }
}
