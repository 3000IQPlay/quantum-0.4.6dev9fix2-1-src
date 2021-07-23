/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.alpha432.oyvey.Minecraftable;
import me.alpha432.oyvey.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class ItemUtil {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = ItemUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = ItemUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!ItemUtil.mc.player.isSneaking()) {
            ItemUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ItemUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            ItemUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            ItemUtil.faceVector(hitVec, true);
        }
        ItemUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        ItemUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        ItemUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!ItemUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(ItemUtil.mc.world.getBlockState(neighbour), false) || (blockState = ItemUtil.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = ItemUtil.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(ItemUtil.mc.player.posX, ItemUtil.mc.player.posY + (double)ItemUtil.mc.player.getEyeHeight(), ItemUtil.mc.player.posZ);
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = ItemUtil.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{ItemUtil.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - ItemUtil.mc.player.rotationYaw)), ItemUtil.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - ItemUtil.mc.player.rotationPitch))};
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = ItemUtil.getLegitRotations(vec);
        ItemUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? (float)MathHelper.normalizeAngle((int)((int)rotations[1]), (int)360) : rotations[1], ItemUtil.mc.player.onGround));
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            ItemUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            ItemUtil.mc.playerController.processRightClickBlock(ItemUtil.mc.player, ItemUtil.mc.world, pos, direction, vec, hand);
        }
        ItemUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        ItemUtil.mc.rightClickDelayTimer = 4;
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = ItemUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock)stack.getItem()).getBlock())) continue;
            return i;
        }
        return -1;
    }

    public static void switchToSlot(int slot) {
        ItemUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(slot));
        ItemUtil.mc.player.inventory.currentItem = slot;
        ItemUtil.mc.playerController.updateController();
    }

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (ItemUtil.mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            ItemUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(slot));
            ItemUtil.mc.playerController.updateController();
        } else {
            ItemUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(slot));
            ItemUtil.mc.player.inventory.currentItem = slot;
            ItemUtil.mc.playerController.updateController();
        }
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Minecraftable.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            slot = i;
        }
        return slot;
    }

    public static int getItemCount(Item item) {
        int count = 0;
        int size = Util.mc.player.inventory.mainInventory.size();
        for (int i = 0; i < size; ++i) {
            ItemStack itemStack = (ItemStack)Util.mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem() != item) continue;
            count += itemStack.getCount();
        }
        ItemStack offhandStack = Util.mc.player.getHeldItemOffhand();
        if (offhandStack.getItem() == item) {
            count += offhandStack.getCount();
        }
        return count;
    }

    public static int getItemSlot(Item item) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            if (!ItemUtil.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
            itemSlot = i;
            break;
        }
        return itemSlot;
    }

    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (int i = 3; i >= 0; --i) {
            ItemStack stack = (ItemStack)player.inventory.armorInventory.get(i);
            if (!(ItemUtil.getDamageInPercent(stack) < percent)) continue;
            return true;
        }
        return false;
    }

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
        float red = 1.0f - green;
        return 100 - (int)(red * 100.0f);
    }

    public static int getBlockFromHotbar(Block block) {
        int slot = -1;
        for (int i = 8; i >= 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem() != Item.getItemFromBlock((Block)block)) continue;
            slot = i;
            break;
        }
        return slot;
    }
}

