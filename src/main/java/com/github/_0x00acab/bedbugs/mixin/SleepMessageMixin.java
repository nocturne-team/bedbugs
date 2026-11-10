package com.github._0x00acab.bedbugs.mixin;

import com.github._0x00acab.bedbugs.SleepRefusalHandler;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Sleep refusals arrive as an action bar message; where the client handles those moved twice.
//? if >=1.19 {
@Mixin(net.minecraft.client.multiplayer.chat.ChatListener.class)
//?} else
/*@Mixin(net.minecraft.client.gui.Gui.class)*/
public class SleepMessageMixin {
	//? if >=26.2 {
	@Inject(method = "handleOverlay", at = @At("HEAD"))
	private void bedbugs$onOverlayMessage(Component message, CallbackInfo ci) {
	//?} elif >=1.19 {
	/*@Inject(method = "handleSystemMessage", at = @At("HEAD"))
	private void bedbugs$onOverlayMessage(Component message, boolean overlay, CallbackInfo ci) {
	*///?} else {
	/*@Inject(method = "setOverlayMessage", at = @At("HEAD"))
	private void bedbugs$onOverlayMessage(Component message, boolean animate, CallbackInfo ci) {
	*///?}
		SleepRefusalHandler.onOverlayMessage(message);
	}
}
