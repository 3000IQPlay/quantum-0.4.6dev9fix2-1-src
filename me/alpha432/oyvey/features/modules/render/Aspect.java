/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.PerspectiveEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect
extends Module {
    public Setting<Float> aspect = this.register(new Setting<Float>("Aspect", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(3.0f)));

    public Aspect() {
        super("Aspect", "a", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event) {
        event.setAspect(this.aspect.getValue().floatValue());
    }
}

