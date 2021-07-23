/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.player.TimerSpeed;

public class TimerManager
extends Feature {
    private float timer = 1.0f;
    private TimerSpeed module;

    public void init() {
        this.module = OyVey.moduleManager.getModuleByClass(TimerSpeed.class);
    }

    public void unload() {
        this.timer = 1.0f;
        TimerManager.mc.timer.tickLength = 50.0f;
    }

    public void update() {
        if (this.module != null && this.module.isEnabled()) {
            this.timer = this.module.speed;
        }
        TimerManager.mc.timer.tickLength = 50.0f / (this.timer <= 0.0f ? 0.1f : this.timer);
    }

    public float getTimer() {
        return this.timer;
    }

    public void setTimer(float timer) {
        if (timer > 0.0f) {
            this.timer = timer;
        }
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
    }
}

