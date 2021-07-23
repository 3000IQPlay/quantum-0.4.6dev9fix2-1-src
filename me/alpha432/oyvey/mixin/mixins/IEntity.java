/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Entity.class})
public interface IEntity {
    @Accessor(value="isInWeb")
    public boolean getIsInWeb();
}

