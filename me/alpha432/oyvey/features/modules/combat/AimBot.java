/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;

public class AimBot
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Rotate", Mode.None));
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    private final Setting<Boolean> onlyBow = this.register(new Setting<Boolean>("Bow Only", true));
    EntityPlayer aimTarget = null;
    RotationUtil aimbotRotation = null;

    public AimBot() {
        super("AimBot", "ab", Module.Category.COMBAT, true, false, false);
    }

    public static enum Mode {
        Legit,
        Packet,
        None;

    }
}

