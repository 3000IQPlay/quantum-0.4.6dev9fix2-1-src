/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.features.modules.render.NoRender;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.GuiToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiToast.class})
public class MixinGuiToast {
    @Inject(method={"drawToast"}, at={@At(value="HEAD")}, cancellable=true)
    public void drawToastHook(ScaledResolution resolution, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().advancements.getValue().booleanValue()) {
            info.cancel();
        }
    }
}

