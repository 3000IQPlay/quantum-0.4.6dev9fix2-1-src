/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.util.Util;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryManager
implements Util {
    public int currentPlayerItem;
    private int recoverySlot = -1;

    public void update() {
        if (this.recoverySlot != -1) {
            InventoryManager.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.recoverySlot == 8 ? 7 : this.recoverySlot + 1));
            InventoryManager.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.recoverySlot));
            int i = InventoryManager.mc.player.inventory.currentItem = this.recoverySlot;
            if (i != this.currentPlayerItem) {
                this.currentPlayerItem = i;
                InventoryManager.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.currentPlayerItem));
            }
            this.recoverySlot = -1;
        }
    }

    public void recoverSilent(int slot) {
        this.recoverySlot = slot;
    }
}

