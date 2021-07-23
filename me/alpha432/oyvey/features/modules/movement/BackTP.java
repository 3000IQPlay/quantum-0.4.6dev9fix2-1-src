/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Util;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class BackTP
extends Module {
    private final Setting<RubbeMode> mode = this.register(new Setting<RubbeMode>("Mode", RubbeMode.Motion));
    private final Setting<Integer> Ym = this.register(new Setting<Object>("Motion", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(15), v -> this.mode.getValue() == RubbeMode.Motion));

    public BackTP() {
        super("BackTP", "Teleports u to the latest ground pos", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (Feature.fullNullCheck()) {
            return;
        }
    }

    @Override
    public void onUpdate() {
        switch (this.mode.getValue()) {
            case Motion: {
                Util.mc.player.motionY = this.Ym.getValue().intValue();
                break;
            }
            case Packet: {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(BackTP.mc.player.posX, BackTP.mc.player.posY + (double)this.Ym.getValue().intValue(), BackTP.mc.player.posZ, true));
                break;
            }
            case Teleport: {
                BackTP.mc.player.setPositionAndUpdate(BackTP.mc.player.posX, BackTP.mc.player.posY + (double)this.Ym.getValue().intValue(), BackTP.mc.player.posZ);
            }
        }
        this.toggle();
    }

    public static enum RubbeMode {
        Motion,
        Teleport,
        Packet;

    }
}

