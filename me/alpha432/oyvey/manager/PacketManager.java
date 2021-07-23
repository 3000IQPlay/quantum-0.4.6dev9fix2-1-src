/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager;

import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.features.Feature;
import net.minecraft.network.Packet;

public class PacketManager
extends Feature {
    private final List<Packet<?>> noEventPackets = new ArrayList();
    public static boolean skipTick = false;
    public static int debugSwings = 0;
    public static int swings = 0;

    public void sendPacketNoEvent(Packet<?> packet) {
        if (packet != null && !PacketManager.nullCheck()) {
            this.noEventPackets.add(packet);
            PacketManager.mc.player.connection.sendPacket(packet);
        }
    }

    public boolean shouldSendPacket(Packet<?> packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        }
        return true;
    }

    public static void updateTicks(boolean in) {
        skipTick = in;
    }

    public static void updateSwings() {
        ++debugSwings;
        ++swings;
    }
}

