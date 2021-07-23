/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class FullBright
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Gamma));
    float oldBright;

    public FullBright() {
        super("FullBright", "", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (FullBright.nullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.Potion) {
            FullBright.mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 80950, 1, false, false)));
        }
    }

    @Override
    public void onEnable() {
        if (FullBright.nullCheck()) {
            return;
        }
        this.oldBright = FullBright.mc.gameSettings.gammaSetting;
        if (this.mode.getValue() == Mode.Gamma) {
            FullBright.mc.gameSettings.gammaSetting = 100.0f;
        }
    }

    @Override
    public void onDisable() {
        FullBright.mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        if (this.mode.getValue() == Mode.Gamma) {
            FullBright.mc.gameSettings.gammaSetting = this.oldBright;
        }
    }

    public static enum Mode {
        Gamma,
        Potion;

    }
}

