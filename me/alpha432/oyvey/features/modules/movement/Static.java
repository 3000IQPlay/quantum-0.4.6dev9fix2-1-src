/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import java.lang.reflect.Field;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.Mapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class Static
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Freeze));

    public Static() {
        super("AntiVoid", "av", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (Static.nullCheck()) {
            return;
        }
        if (Static.mc.player.posY <= 0.0) {
            switch (this.mode.getValue()) {
                case Float: {
                    Static.mc.player.motionY = 0.5;
                    break;
                }
                case Freeze: {
                    Static.mc.player.motionY = 0.0;
                    break;
                }
                case SlowFall: {
                    Static.mc.player.motionY /= 4.0;
                    break;
                }
                case TP: {
                    Static.mc.player.setPosition(Static.mc.player.posX, Static.mc.player.posY + 2.0, Static.mc.player.posZ);
                }
            }
        }
    }

    private void setTimer(float value) {
        try {
            Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
            timer.setAccessible(true);
            Field tickLength = Timer.class.getDeclaredField(Mapping.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0f / value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDisplayInfo() {
        return " " + (Object)((Object)this.mode.getValue());
    }

    public static enum Mode {
        Float,
        Freeze,
        SlowFall,
        TP;

    }
}

