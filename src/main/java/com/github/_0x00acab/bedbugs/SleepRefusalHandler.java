package com.github._0x00acab.bedbugs;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Monster;

/** Marks the monsters around the last clicked bed when the server refuses sleep because of them. */
public final class SleepRefusalHandler {
	// Vanilla's "You may not rest now; there are monsters nearby"
	static final String NOT_SAFE_KEY = "block.minecraft.bed.not_safe";

	private SleepRefusalHandler() {
	}

	public static void onOverlayMessage(Component message) {
		if (message == null || !NOT_SAFE_KEY.equals(translationKey(message))) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}
		HighlightTracker.INSTANCE.ensureLevel(minecraft.level);
		Long bed = HighlightTracker.INSTANCE.takeBedClick();
		if (bed == null) {
			return;
		}
		for (Monster monster : MonsterSleepCheck.findBlockingMonsters(minecraft.level, BlockPos.of(bed))) {
			HighlightTracker.INSTANCE.highlight(monster.getId());
		}
	}

	private static String translationKey(Component message) {
		//? if >=1.19 {
		return message.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents
			? ((net.minecraft.network.chat.contents.TranslatableContents) message.getContents()).getKey() : null;
		//?} else {
		/*return message instanceof net.minecraft.network.chat.TranslatableComponent
			? ((net.minecraft.network.chat.TranslatableComponent) message).getKey() : null;
		*///?}
	}
}
