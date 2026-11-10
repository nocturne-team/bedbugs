package com.github._0x00acab.bedbugs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

//? if >=1.21.11
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
//? if <1.21.11
/*import net.minecraft.world.entity.monster.ZombifiedPiglin;*/

public final class MonsterSleepCheck {
	private MonsterSleepCheck() {
	}

	// Mirrors vanilla ServerPlayer.startSleepInBed's own box: +-8 X/Z, +-5 Y, bottom-centered on the bed.
	// Only called once the server has said monsters are the reason.
	public static List<Monster> findBlockingMonsters(Level level, BlockPos bedPos) {
		Vec3 center = Vec3.atBottomCenterOf(bedPos);
		AABB box = new AABB(
			center.x() - 8.0, center.y() - 5.0, center.z() - 8.0,
			center.x() + 8.0, center.y() + 5.0, center.z() + 8.0
		);
		// Zombified piglins only block sleep while angry at the player, which is not
		// synced to clients. Their attack pose is, so count the ones showing it.
		return preferCertain(level.getEntitiesOfClass(Monster.class, box),
			monster -> !(monster instanceof ZombifiedPiglin) || monster.isAggressive());
	}

	// If nothing is certain, something uncertain must still be the cause: return everything.
	static <T> List<T> preferCertain(List<T> all, Predicate<T> certain) {
		List<T> picked = new ArrayList<>();
		for (T candidate : all) {
			if (certain.test(candidate)) {
				picked.add(candidate);
			}
		}
		return picked.isEmpty() ? all : picked;
	}
}
