package com.github._0x00acab.bedbugs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;

//? if >=26.3
import net.minecraft.world.level.block.AbstractBedBlock;

import java.util.List;

public final class BedRightClickHandler {
    private BedRightClickHandler() {
    }

    public static void handle(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!isBed(state)) {
            return;
        }
        if (state.getValue(bedPartProperty()) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(HorizontalDirectionalBlock.FACING));
        }
        List<Monster> blocking = MonsterSleepCheck.findBlockingMonsters(level, pos, player);
        for (Monster monster : blocking) {
            HighlightTracker.INSTANCE.highlight(monster.getId());
        }
    }

    private static boolean isBed(BlockState state) {
        //? if >=26.3 {
        return state.getBlock() instanceof AbstractBedBlock;
        //?} else
        /*return state.getBlock() instanceof BedBlock;*/
    }

    private static EnumProperty<BedPart> bedPartProperty() {
        //? if >=26.3 {
        return AbstractBedBlock.PART;
        //?} else
        /*return BedBlock.PART;*/
    }
}
