/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Flatten
extends Module {
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("BlocksPerTick", 8, 1, 30));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> packet = this.register(new Setting<Boolean>("PacketPlace", false));
    private final Setting<Boolean> autoDisable = this.register(new Setting<Boolean>("AutoDisable", true));
    private final Setting<Boolean> targetNullDisable = this.register(new Setting<Boolean>("NullTargetDisable", false));
    private final Vec3d[] offsetsDefault = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0)};
    private int offsetStep = 0;
    private int oldSlot = -1;
    private boolean placing = false;

    public Flatten() {
        super("FeetFloor", "f", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.oldSlot = Flatten.mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        this.oldSlot = -1;
    }

    @Override
    public void onUpdate() {
        EntityPlayer closest_target = this.findClosestTarget();
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (closest_target == null && this.targetNullDisable.getValue().booleanValue()) {
            this.disable();
            return;
        }
        ArrayList place_targets = new ArrayList();
        Collections.addAll(place_targets, this.offsetsDefault);
        int blocks_placed = 0;
        while (blocks_placed < this.blocksPerTick.getValue()) {
            if (this.offsetStep >= place_targets.size()) {
                this.offsetStep = 0;
                break;
            }
            this.placing = true;
            BlockPos offset_pos = new BlockPos((Vec3d)place_targets.get(this.offsetStep));
            BlockPos target_pos = new BlockPos(closest_target.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
            boolean should_try_place = Flatten.mc.world.getBlockState(target_pos).getMaterial().isReplaceable();
            for (Entity entity : Flatten.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                should_try_place = false;
                break;
            }
            if (should_try_place) {
                this.place(target_pos, obbySlot, this.oldSlot);
                ++blocks_placed;
            }
            ++this.offsetStep;
            this.placing = false;
        }
        if (this.autoDisable.getValue().booleanValue()) {
            this.disable();
        }
    }

    private void place(BlockPos pos, int slot, int oldSlot) {
        Flatten.mc.player.inventory.currentItem = slot;
        Flatten.mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), Flatten.mc.player.isSneaking());
        Flatten.mc.player.inventory.currentItem = oldSlot;
        Flatten.mc.playerController.updateController();
    }

    private EntityPlayer findClosestTarget() {
        if (Flatten.mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : Flatten.mc.world.playerEntities) {
            if (target == Flatten.mc.player || !target.isEntityAlive() || OyVey.friendManager.isFriend(target.getName()) || target.getHealth() <= 0.0f || Flatten.mc.player.getDistance((Entity)target) > 5.0f || closestTarget != null && Flatten.mc.player.getDistance((Entity)target) > Flatten.mc.player.getDistance((Entity)closestTarget)) continue;
            closestTarget = target;
        }
        return closestTarget;
    }
}

