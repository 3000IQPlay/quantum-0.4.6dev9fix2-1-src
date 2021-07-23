/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.PlayerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketEat
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Packet));
    private final Setting<Double> health = this.register(new Setting<Double>("Health", 28.0, 0.0, 36.0));
    private final Setting<Double> packetSize = this.register(new Setting<Double>("PacketIteration", 20.0, 0.0, 40.0));

    public PacketEat() {
        super("PacketEat", "", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (PacketEat.mc.player.isHandActive() && PacketEat.mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && (this.mode.getValue() == Mode.Packet || this.mode.getValue() == Mode.Auto)) {
            int i = 0;
            while ((double)i < this.packetSize.getValue()) {
                PacketEat.mc.player.connection.sendPacket((Packet)new CPacketPlayer());
                ++i;
            }
            PacketEat.mc.player.stopActiveHand();
        }
        if (this.mode.getValue() == Mode.Auto && PlayerUtil.getHealth() <= this.health.getValue()) {
            InventoryUtil.switchToSlotGhost(InventoryUtil.getHotbarItemSlot(Items.GOLDEN_APPLE));
            PacketEat.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
    }

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem().equals(Items.GOLDEN_APPLE) && this.mode.getValue() == Mode.Desync) {
            event.setCanceled(true);
            event.getItemStack().getItem().onItemUseFinish(event.getItemStack(), event.getWorld(), (EntityLivingBase)event.getEntityPlayer());
        }
    }

    public static enum Mode {
        Packet,
        Desync,
        Auto;

    }
}

