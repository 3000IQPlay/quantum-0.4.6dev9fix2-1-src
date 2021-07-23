/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.command.commands;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.movement.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

public class VClipCommand
extends Command {
    public VClipCommand() {
        super("clip", new String[]{"0/1/2/3/4/5"});
    }

    @Override
    public void execute(String[] commands) {
        Phase.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Phase.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        VClipCommand.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 0.5346, VClipCommand.mc.player.posZ, false));
        VClipCommand.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 1.2836, VClipCommand.mc.player.posZ, false));
        VClipCommand.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 1.9231, VClipCommand.mc.player.posZ, false));
        VClipCommand.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 2.3957, VClipCommand.mc.player.posZ, false));
        VClipCommand.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 3.0121, VClipCommand.mc.player.posZ, false));
        VClipCommand.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.9564, VClipCommand.mc.player.posZ, false));
        VClipCommand.sendMessage("Tried clipping");
        switch (commands[0]) {
            case "0": {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 0.5346, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 1.2836, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 1.9231, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 2.3957, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 3.0121, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.9564, VClipCommand.mc.player.posZ, false));
                return;
            }
            case "1": {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 1.5346, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 2.2836, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 2.9231, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 3.3957, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.0121, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 5.9564, VClipCommand.mc.player.posZ, false));
                return;
            }
            case "2": {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 2.5346, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 3.2836, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 3.9231, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.3957, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 5.0121, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 6.9564, VClipCommand.mc.player.posZ, false));
                return;
            }
            case "3": {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 3.5346, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.2836, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.9231, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 5.3957, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 6.0121, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 7.9564, VClipCommand.mc.player.posZ, false));
                return;
            }
            case "4": {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 4.5346, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 5.2836, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 5.9231, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 6.3957, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 7.0121, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 8.9564, VClipCommand.mc.player.posZ, false));
                return;
            }
            case "5": {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 5.5346, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 6.2836, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 6.9231, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 7.3957, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 8.0121, VClipCommand.mc.player.posZ, false));
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(VClipCommand.mc.player.posX, VClipCommand.mc.player.posY - 9.9564, VClipCommand.mc.player.posZ, false));
                return;
            }
        }
    }
}

