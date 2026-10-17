package com.github._0x00acab.bedbugs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;

import java.util.List;

public final class BedRightClickHandler {
    private BedRightClickHandler() {
    }

    public static void handle(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            return;
        }
        if (!(level.getBlockState(pos).getBlock() instanceof BedBlock)) {
            return;
        }
        List<Monster> blocking = MonsterSleepCheck.findBlockingMonsters(level, pos, player);
        for (Monster monster : blocking) {
            HighlightTracker.INSTANCE.highlight(monster.getId());
        }
    }
}
