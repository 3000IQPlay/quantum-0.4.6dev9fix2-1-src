/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.EventStage;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class MoveEvent
extends EventStage {
    private MoverType type;
    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;

    public MoveEvent(int stage, MoverType type, double x, double y, double z) {
        super(stage);
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.motionX = this.motionX;
        this.motionY = this.motionY;
        this.motionZ = this.motionZ;
    }

    public MoverType getType() {
        return this.type;
    }

    public void setType(MoverType type) {
        this.type = type;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public final double getMotionX() {
        return this.motionX;
    }

    public final double getMotionY() {
        return this.motionY;
    }

    public final double getMotionZ() {
        return this.motionZ;
    }

    public void setMotionX(double x) {
        this.motionX = x;
    }

    public void setMotionY(double y) {
        this.motionY = y;
    }

    public void setMotionZ(double z) {
        this.motionZ = z;
    }
}

