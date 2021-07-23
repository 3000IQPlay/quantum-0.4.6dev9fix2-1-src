/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.network.Packet;

public class PacketLogger
extends Module {
    private Packet[] packets;
    public Setting<Boolean> incoming = this.register(new Setting<Boolean>("Incoming", true));
    public Setting<Boolean> outgoing = this.register(new Setting<Boolean>("Outgoing", true));
    public Setting<Boolean> data = this.register(new Setting<Boolean>("Data", true));

    public PacketLogger() {
        super("PacketLogger", "", Module.Category.MISC, true, false, false);
    }
}

