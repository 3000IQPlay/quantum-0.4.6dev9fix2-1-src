/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.events.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketNiggerEvent
extends PacketEvent {
    public PacketNiggerEvent(int stage, Packet<?> packet) {
        super(stage, packet);
    }
}

