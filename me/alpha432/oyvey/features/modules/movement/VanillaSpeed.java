/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.MovementUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaSpeed
extends Module {
    private final Setting<Float> speed = this.register(new Setting<Float>("Speed", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(10.0f)));

    public VanillaSpeed() {
        super("VanillaSpeed", "vs", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        double[] calc = MovementUtil.directionSpeed((double)this.speed.getValue().floatValue() / 10.0);
        event.setMotionX(calc[0]);
        event.setMotionZ(calc[1]);
    }
}

