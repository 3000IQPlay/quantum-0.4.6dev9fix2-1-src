/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockLag
extends Module {
    private static BlockLag INSTANCE;
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.OBSIDIAN));
    private final Setting<Boolean> smartTp = this.register(new Setting<Boolean>("SmartTP", true));
    private final Setting<Integer> tpMin = this.register(new Setting<Integer>("TPMin", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(10), v -> this.smartTp.getValue()));
    private final Setting<Integer> tpMax = this.register(new Setting<Integer>("TPMax", Integer.valueOf(25), Integer.valueOf(10), Integer.valueOf(40), v -> this.smartTp.getValue()));
    private final Setting<Boolean> noVoid = this.register(new Setting<Boolean>("NoVoid", Boolean.valueOf(true), v -> this.smartTp.getValue()));
    private final Setting<Integer> tpHeight = this.register(new Setting<Integer>("TPHeight", Integer.valueOf(2), Integer.valueOf(-40), Integer.valueOf(40), v -> this.smartTp.getValue() == false));
    private final Setting<Boolean> keepInside = this.register(new Setting<Boolean>("Center", true));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> sneaking = this.register(new Setting<Boolean>("Sneak", false));
    private final Setting<Boolean> offground = this.register(new Setting<Boolean>("Offground", false));
    private final Setting<Boolean> chat = this.register(new Setting<Boolean>("Chat Msgs", true));
    private final Setting<Boolean> tpdebug = this.register(new Setting<Boolean>("Debug", Boolean.valueOf(false), v -> this.chat.getValue() != false && this.smartTp.getValue() != false));
    private BlockPos burrowPos;
    private int lastBlock;
    private int blockSlot;
    private Class block;
    private String name;

    public BlockLag() {
        super("SelfFill", "sf", Module.Category.COMBAT, true, false, false);
        INSTANCE = this;
    }

    public static BlockLag getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlockLag();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        this.burrowPos = new BlockPos(BlockLag.mc.player.posX, Math.ceil(BlockLag.mc.player.posY), BlockLag.mc.player.posZ);
        this.blockSlot = this.findBlockSlot();
        this.lastBlock = BlockLag.mc.player.inventory.currentItem;
        if (!this.doChecks() || this.blockSlot == -1) {
            this.disable();
            return;
        }
        if (this.keepInside.getValue().booleanValue()) {
            double x = BlockLag.mc.player.posX - Math.floor(BlockLag.mc.player.posX);
            double z = BlockLag.mc.player.posZ - Math.floor(BlockLag.mc.player.posZ);
            if (x <= 0.3 || x >= 0.7) {
                double d = x = x > 0.5 ? 0.69 : 0.31;
            }
            if (z < 0.3 || z > 0.7) {
                z = z > 0.5 ? 0.69 : 0.31;
            }
            BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Math.floor(BlockLag.mc.player.posX) + x, BlockLag.mc.player.posY, Math.floor(BlockLag.mc.player.posZ) + z, BlockLag.mc.player.onGround));
            BlockLag.mc.player.setPosition(Math.floor(BlockLag.mc.player.posX) + x, BlockLag.mc.player.posY, Math.floor(BlockLag.mc.player.posZ) + z);
        }
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 0.41999998688698, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 0.7531999805211997, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 1.00133597911214, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 1.16610926093821, BlockLag.mc.player.posZ, this.offground.getValue() == false));
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(BlockLag.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)this.burrowPos.getX() + 0.5f), (double)((float)this.burrowPos.getY() + 0.5f), (double)((float)this.burrowPos.getZ() + 0.5f)));
            OyVey.rotationManager.setPlayerRotations(angle[0], angle[1]);
        }
        InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
        BlockUtil.placeBlock(this.burrowPos, this.blockSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, true, this.sneaking.getValue());
        InventoryUtil.switchToHotbarSlot(this.lastBlock, false);
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, this.smartTp.getValue() != false ? (double)this.adaptiveTpHeight() : (double)this.tpHeight.getValue().intValue() + BlockLag.mc.player.posY, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        this.disable();
    }

    private int findBlockSlot() {
        switch (this.mode.getValue()) {
            case ECHEST: {
                this.block = BlockEnderChest.class;
                this.name = "Ender Chests";
                break;
            }
            case OBSIDIAN: {
                this.block = BlockObsidian.class;
                this.name = "Obsidian";
                break;
            }
            case SOULSAND: {
                this.block = BlockSoulSand.class;
                this.name = "Soul Sand";
            }
        }
        int slot = InventoryUtil.findHotbarBlock(this.block);
        if (slot == -1) {
            if (InventoryUtil.isBlock(BlockLag.mc.player.getHeldItemOffhand().getItem(), this.block)) {
                return -2;
            }
            if (this.chat.getValue().booleanValue()) {
                Command.sendMessage("\u00a77" + (String)this.displayName.getValue() + ":\u00a7c No " + this.name + " to use");
            }
        }
        return slot;
    }

    private int adaptiveTpHeight() {
        int airblock;
        int n = airblock = this.noVoid.getValue() != false && this.tpMax.getValue() * -1 + this.burrowPos.getY() < 0 ? this.burrowPos.getY() * -1 : this.tpMax.getValue() * -1;
        while (airblock < this.tpMax.getValue()) {
            if (Math.abs(airblock) < this.tpMin.getValue() || !BlockLag.mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, airblock)) || !BlockLag.mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, airblock + 1))) {
                ++airblock;
                continue;
            }
            if (this.tpdebug.getValue().booleanValue()) {
                Command.sendMessage(Integer.toString(airblock));
            }
            return this.burrowPos.getY() + airblock;
        }
        return 69420;
    }

    private boolean doChecks() {
        if (!BlockLag.fullNullCheck()) {
            if (this.smartTp.getValue().booleanValue() && this.adaptiveTpHeight() == 69420) {
                if (this.chat.getValue().booleanValue()) {
                    Command.sendMessage("\u00a77" + (String)this.displayName.getValue() + ":\u00a7c Not enough room");
                }
                return false;
            }
            if (BlockLag.mc.world.getBlockState(this.burrowPos).getBlock().equals(Blocks.OBSIDIAN)) {
                return false;
            }
            if (!BlockLag.mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, 2))) {
                if (this.chat.getValue().booleanValue()) {
                    Command.sendMessage("\u00a77" + (String)this.displayName.getValue() + ":\u00a7c Not enough room");
                }
                return false;
            }
            for (Entity entity : BlockLag.mc.world.loadedEntityList) {
                if (entity instanceof EntityItem || entity.equals((Object)BlockLag.mc.player) || !new AxisAlignedBB(this.burrowPos).intersects(entity.getEntityBoundingBox())) continue;
                if (this.chat.getValue().booleanValue()) {
                    Command.sendMessage("\u00a77" + (String)this.displayName.getValue() + ":\u00a7c Not enough room");
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public static enum Mode {
        OBSIDIAN,
        ECHEST,
        SOULSAND;

    }
}

