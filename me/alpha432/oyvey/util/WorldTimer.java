/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.Wrapper;

public class WorldTimer {
    private float OverrideSpeed = 1.0f;

    private void useTimer() {
        if (this.OverrideSpeed != 1.0f && this.OverrideSpeed > 0.1f) {
            Wrapper.mc.timer.tickLength = 50.0f / this.OverrideSpeed;
        }
    }

    public void SetOverrideSpeed(float f) {
        this.OverrideSpeed = f;
        this.useTimer();
    }

    public void resetTime() {
        Wrapper.mc.timer.tickLength = 50.0f;
    }
}

