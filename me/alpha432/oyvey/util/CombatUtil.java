/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class CombatUtil
implements Util {
    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;
        int size = Util.mc.world.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            EntityPlayer player = (EntityPlayer)Util.mc.world.playerEntities.get(i);
            if (EntityUtil.isntValid((Entity)player, range)) continue;
            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }
            if (!(Util.mc.player.getDistanceSq((Entity)player) < Util.mc.player.getDistanceSq((Entity)currentTarget))) continue;
            currentTarget = player;
        }
        return currentTarget;
    }
}

