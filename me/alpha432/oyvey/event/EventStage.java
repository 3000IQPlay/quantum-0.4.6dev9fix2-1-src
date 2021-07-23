/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventStage
extends Event {
    private int stage;
    private boolean canceled;

    public EventStage() {
    }

    public EventStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public void setCanceledE(boolean c) {
        this.canceled = c;
    }

    public boolean isCanceledE() {
        return this.canceled;
    }
}

