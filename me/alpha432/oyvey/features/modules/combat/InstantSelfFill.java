/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Field;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.movement.ReverseStep;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.Mapping;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.ItemUtil;
import me.alpha432.oyvey.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;

public class InstantSelfFill
extends Module {
    private final Setting<Float> height = this.register(new Setting<Float>("TPHeight", Float.valueOf(5.0f), Float.valueOf(-20.0f), Float.valueOf(20.0f)));
    private final Setting<Float> extraboost = this.register(new Setting<Float>("Extra", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f)));
    public final Setting<Page> page = this.register(new Setting<Page>("Block", Page.EChest));
    private final Setting<Boolean> packetJump = this.register(new Setting<Object>("PacketJump", Boolean.valueOf(false), v -> this.timerfill.getValue() == false));
    private final Setting<Boolean> timerfill = this.register(new Setting<Object>("TimerJump", Boolean.valueOf(true), v -> this.packetJump.getValue() == false));
    private final Setting<Boolean> autoCenter = this.register(new Setting<Boolean>("Center", false));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", Boolean.FALSE));
    private final Setting<Boolean> sneaking = this.register(new Setting<Boolean>("SneakPacket", false));
    private final Setting<Boolean> offground = this.register(new Setting<Boolean>("Offground", false));
    public BlockPos startPos = null;
    private BlockPos playerPos;
    private int hudAmount = 0;
    private int blockSlot;

    public InstantSelfFill() {
        super("Burrow", "b", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (this.timerfill.getValue().booleanValue()) {
            this.setTimer(50.0f);
            OyVey.moduleManager.getModuleByName("ReverseStep").isEnabled();
            ReverseStep.getInstance().disable();
            this.playerPos = new BlockPos(InstantSelfFill.mc.player.posX, InstantSelfFill.mc.player.posY, InstantSelfFill.mc.player.posZ);
            if (InstantSelfFill.mc.world.getBlockState(this.playerPos).getBlock().equals(Blocks.OBSIDIAN)) {
                this.disable();
                return;
            }
            InstantSelfFill.mc.player.jump();
        }
        if (this.packetJump.getValue().booleanValue()) {
            if (InstantSelfFill.fullNullCheck()) {
                this.disable();
                return;
            }
            OyVey.moduleManager.getModuleByName("ReverseStep").isEnabled();
            ReverseStep.getInstance().disable();
            if (this.page.getValue() == Page.EChest || this.page.getValue() == Page.Obsdidian) {
                this.startPos = new BlockPos(InstantSelfFill.mc.player.getPositionVector());
            }
        }
    }

    @Override
    @Subscribe
    public void onUpdate() {
        int amount = 0;
        if (this.timerfill.getValue().booleanValue()) {
            if (InstantSelfFill.nullCheck()) {
                return;
            }
            if (InstantSelfFill.mc.player.posY > (double)this.playerPos.getY() + 1.04) {
                WorldUtil.placeBlock(this.playerPos, InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN));
                InstantSelfFill.mc.player.jump();
                this.disable();
            }
        }
        if (this.autoCenter.getValue().booleanValue()) {
            OyVey.positionManager.setPositionPacket((double)this.startPos.getX() + 0.5, this.startPos.getY(), (double)this.startPos.getZ() + 0.5, true, true, true);
        }
        if (this.packetJump.getValue().booleanValue()) {
            if (InstantSelfFill.fullNullCheck()) {
                return;
            }
            int startSlot = InstantSelfFill.mc.player.inventory.currentItem;
            if (this.page.getValue() == Page.EChest) {
                int enderSlot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock((Block)Blocks.ENDER_CHEST));
                ItemUtil.switchToHotbarSlot(enderSlot, false);
                if (enderSlot == -1) {
                    Command.sendMessage("<" + this.getDisplayName() + "> out of echests.");
                    this.disable();
                    return;
                }
            }
            if (this.page.getValue() == Page.Obsdidian) {
                int obbySlot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock((Block)Blocks.OBSIDIAN));
                ItemUtil.switchToHotbarSlot(obbySlot, false);
                if (obbySlot == -1) {
                    Command.sendMessage("<" + this.getDisplayName() + "> out of obsidian.");
                    this.disable();
                    return;
                }
            }
            if (this.page.getValue() == Page.EChest || this.page.getValue() == Page.Obsdidian) {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(InstantSelfFill.mc.player.posX, InstantSelfFill.mc.player.posY + 0.4199999 + (double)this.extraboost.getValue().floatValue(), InstantSelfFill.mc.player.posZ, this.offground.getValue() == false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(InstantSelfFill.mc.player.posX, InstantSelfFill.mc.player.posY + 0.7531999 + (double)this.extraboost.getValue().floatValue(), InstantSelfFill.mc.player.posZ, this.offground.getValue() == false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(InstantSelfFill.mc.player.posX, InstantSelfFill.mc.player.posY + 1.0013359 + (double)this.extraboost.getValue().floatValue(), InstantSelfFill.mc.player.posZ, this.offground.getValue() == false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(InstantSelfFill.mc.player.posX, InstantSelfFill.mc.player.posY + 1.1661092 + (double)this.extraboost.getValue().floatValue(), InstantSelfFill.mc.player.posZ, this.offground.getValue() == false));
                BlockUtil.placeBlock(this.startPos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(InstantSelfFill.mc.player.posX, InstantSelfFill.mc.player.posY + (double)this.height.getValue().floatValue(), InstantSelfFill.mc.player.posZ, this.offground.getValue() == false));
                if (this.sneaking.getValue().booleanValue()) {
                    InstantSelfFill.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)InstantSelfFill.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    InstantSelfFill.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)InstantSelfFill.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (startSlot != -1) {
                    ItemUtil.switchToHotbarSlot(startSlot, false);
                }
                this.disable();
            }
        }
        if (!this.settings.isEmpty()) {
            for (Setting setting : this.settings) {
                if (!(setting.getValue() instanceof Boolean) || !((Boolean)setting.getValue()).booleanValue() || setting.getName().equalsIgnoreCase("Enabled") || setting.getName().equalsIgnoreCase("drawn")) continue;
                ++amount;
            }
        }
        this.hudAmount = amount;
    }

    @Override
    public void onDisable() {
        OyVey.moduleManager.getModuleByName("ReverseStep").isDisabled();
        ReverseStep.getInstance().enable();
        if (this.timerfill.getValue().booleanValue()) {
            this.setTimer(1.0f);
        }
    }

    private void setTimer(float value) {
        try {
            Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
            timer.setAccessible(true);
            Field tickLength = Timer.class.getDeclaredField(Mapping.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0f / value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.hudAmount == 0) {
            return "";
        }
        return this.hudAmount + "";
    }

    public static enum Page {
        EChest,
        Obsdidian,
        Soulsand;

    }
}

