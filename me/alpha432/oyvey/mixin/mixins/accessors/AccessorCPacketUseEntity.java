/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins.accessors;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketUseEntity.class})
public interface AccessorCPacketUseEntity {
    @Accessor(value="entityId")
    public void setEntityId(int var1);

    @Accessor(value="action")
    public void setAction(CPacketUseEntity.Action var1);
}

