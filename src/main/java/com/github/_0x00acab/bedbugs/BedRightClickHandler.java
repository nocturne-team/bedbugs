package com.github._0x00acab.bedbugs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;

//? if >=26.3
import net.minecraft.world.level.block.AbstractBedBlock;

public final class BedRightClickHandler {
	private BedRightClickHandler() {
	}

	// Only remembers the bed. Monsters are marked once the server refuses the
	// sleep attempt, see SleepRefusalHandler.
	public static void handle(Level level, BlockPos pos) {
		if (!level.isClientSide()) {
			return;
		}
		HighlightTracker.INSTANCE.ensureLevel(level);
		BlockState state = level.getBlockState(pos);
		if (!isBed(state)) {
			return;
		}
		if (state.getValue(bedPartProperty()) != BedPart.HEAD) {
			pos = pos.relative(state.getValue(HorizontalDirectionalBlock.FACING));
		}
		HighlightTracker.INSTANCE.rememberBedClick(pos.asLong());
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
