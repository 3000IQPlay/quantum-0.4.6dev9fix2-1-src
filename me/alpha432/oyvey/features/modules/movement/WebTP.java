/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class WebTP
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Vanilla));

    public WebTP() {
        super("WebTP", "", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (WebTP.nullCheck()) {
            return;
        }
        if (WebTP.mc.player.isInWeb) {
            switch (this.mode.getValue()) {
                case Normal: {
                    for (int i = 0; i < 10; ++i) {
                        WebTP.mc.player.motionY -= 1.0;
                    }
                    break;
                }
                case Vanilla: {
                    WebTP.mc.player.isInWeb = false;
                }
            }
        }
    }

    public static enum Mode {
        Normal,
        Vanilla;

    }
}

