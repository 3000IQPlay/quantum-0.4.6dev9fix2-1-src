/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DupeCanceller
extends Module {
    private int PacketsCanelled = 0;

    public DupeCanceller() {
        super("DupeCancel", "dc", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.PacketsCanelled = 0;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketInput) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer.Position) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer.Rotation) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerAbilities) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketUseEntity) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketVehicleMove) {
            event.setCanceled(true);
        }
    }
}

