/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.EventStage;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BlockCollisionBoundingBoxEvent
extends EventStage {
    private BlockPos pos;
    private AxisAlignedBB _boundingBox;

    public BlockCollisionBoundingBoxEvent(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this._boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this._boundingBox = boundingBox;
    }
}

