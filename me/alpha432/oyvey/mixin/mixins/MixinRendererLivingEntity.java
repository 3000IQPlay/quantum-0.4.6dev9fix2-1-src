/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase>
extends Render<T> {
    @Shadow
    protected ModelBase entityModel;

    protected MixinRendererLivingEntity() {
        super((RenderManager)null);
    }

    @Inject(method={"doRender"}, at={@At(value="HEAD")})
    public void doRenderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (OyVey.moduleManager.isModuleEnabled("TexturedChams") && entity != null) {
            GL11.glEnable((int)32823);
            GL11.glPolygonOffset((float)1.0f, (float)-1100000.0f);
        }
    }

    @Inject(method={"doRender"}, at={@At(value="RETURN")})
    public void doRenderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (OyVey.moduleManager.isModuleEnabled("TexturedChams") && entity != null) {
            GL11.glPolygonOffset((float)1.0f, (float)1000000.0f);
            GL11.glDisable((int)32823);
        }
    }
}

