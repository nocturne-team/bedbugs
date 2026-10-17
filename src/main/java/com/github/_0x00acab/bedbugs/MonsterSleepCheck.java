package com.github._0x00acab.bedbugs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

//? if >=1.21.11
import net.minecraft.world.entity.NeutralMob;

public final class MonsterSleepCheck {
    private MonsterSleepCheck() {
    }

    // Mirrors vanilla ServerPlayer.startSleepInBed's own box: +-8 X/Z, +-5 Y, bottom-centered on the bed.
    public static List<Monster> findBlockingMonsters(Level level, BlockPos bedPos, Player player) {
        Vec3 center = Vec3.atBottomCenterOf(bedPos);
        AABB box = new AABB(
            center.x() - 8.0, center.y() - 5.0, center.z() - 8.0,
            center.x() + 8.0, center.y() + 5.0, center.z() + 8.0
        );
        //? if >=1.21.11 {
        return level.getEntitiesOfClass(Monster.class, box,
            monster -> !(monster instanceof NeutralMob neutralMob) || neutralMob.isAngry());
        //?} else
        /*return level.getEntitiesOfClass(Monster.class, box, monster -> monster.isPreventingPlayerRest(player));*/
    }
}
