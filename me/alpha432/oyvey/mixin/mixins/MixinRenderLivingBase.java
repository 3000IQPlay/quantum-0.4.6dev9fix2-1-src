/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.render.Wireframe;
import me.alpha432.oyvey.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render<T> {
    @Shadow
    private static final Logger LOGGER = LogManager.getLogger();
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected boolean renderMarker;
    float red = 0.0f;
    float green = 0.0f;
    float blue = 0.0f;

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Overwrite
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!MinecraftForge.EVENT_BUS.post((Event)new RenderLivingEvent.Pre(entity, (RenderLivingBase)RenderLivingBase.class.cast((Object)this), partialTicks, x, y, z))) {
            boolean shouldSit;
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
            this.mainModel.isRiding = shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
            this.mainModel.isChild = entity.isChild();
            try {
                float f = this.interpolateRotation(((EntityLivingBase)entity).prevRenderYawOffset, ((EntityLivingBase)entity).renderYawOffset, partialTicks);
                float f1 = this.interpolateRotation(((EntityLivingBase)entity).prevRotationYawHead, ((EntityLivingBase)entity).rotationYawHead, partialTicks);
                float f2 = f1 - f;
                if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
                    f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                    f2 = f1 - f;
                    float f3 = MathHelper.wrapDegrees((float)f2);
                    if (f3 < -85.0f) {
                        f3 = -85.0f;
                    }
                    if (f3 >= 85.0f) {
                        f3 = 85.0f;
                    }
                    f = f1 - f3;
                    if (f3 * f3 > 2500.0f) {
                        f += f3 * 0.2f;
                    }
                    f2 = f1 - f;
                }
                float f7 = ((EntityLivingBase)entity).prevRotationPitch + (((EntityLivingBase)entity).rotationPitch - ((EntityLivingBase)entity).prevRotationPitch) * partialTicks;
                this.renderLivingAt(entity, x, y, z);
                float f8 = this.handleRotationFloat(entity, partialTicks);
                this.applyRotations(entity, f8, f, partialTicks);
                float f4 = this.prepareScale(entity, partialTicks);
                float f5 = 0.0f;
                float f6 = 0.0f;
                if (!entity.isRiding()) {
                    f5 = ((EntityLivingBase)entity).prevLimbSwingAmount + (((EntityLivingBase)entity).limbSwingAmount - ((EntityLivingBase)entity).prevLimbSwingAmount) * partialTicks;
                    f6 = ((EntityLivingBase)entity).limbSwing - ((EntityLivingBase)entity).limbSwingAmount * (1.0f - partialTicks);
                    if (entity.isChild()) {
                        f6 *= 3.0f;
                    }
                    if (f5 > 1.0f) {
                        f5 = 1.0f;
                    }
                    f2 = f1 - f;
                }
                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
                this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
                if (this.renderOutlines) {
                    boolean flag1 = this.setScoreTeamColor(entity);
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode((int)this.getTeamColor((Entity)entity));
                    if (!this.renderMarker) {
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                    }
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                        this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                    }
                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                    if (flag1) {
                        this.unsetScoreTeamColor();
                    }
                } else {
                    if (Wireframe.getInstance().isOn() && Wireframe.getInstance().players.getValue().booleanValue() && entity instanceof EntityPlayer && Wireframe.getInstance().mode.getValue().equals((Object)Wireframe.RenderMode.SOLID)) {
                        this.red = (float)Wireframe.getInstance().red.getValue().intValue() / 255.0f;
                        this.green = (float)Wireframe.getInstance().green.getValue().intValue() / 255.0f;
                        this.blue = (float)Wireframe.getInstance().blue.getValue().intValue() / 255.0f;
                        GlStateManager.pushMatrix();
                        GL11.glPushAttrib((int)1048575);
                        GL11.glDisable((int)3553);
                        GL11.glDisable((int)2896);
                        GL11.glEnable((int)2848);
                        GL11.glEnable((int)3042);
                        GL11.glBlendFunc((int)770, (int)771);
                        GL11.glDisable((int)2929);
                        GL11.glDepthMask((boolean)false);
                        if (OyVey.friendManager.isFriend(entity.getName()) || entity == Minecraft.getMinecraft().player) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)(Wireframe.getInstance().alpha.getValue().floatValue() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)(Wireframe.getInstance().alpha.getValue().floatValue() / 255.0f));
                        }
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                        GL11.glDisable((int)2896);
                        GL11.glEnable((int)2929);
                        GL11.glDepthMask((boolean)true);
                        if (OyVey.friendManager.isFriend(entity.getName()) || entity == Minecraft.getMinecraft().player) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)(Wireframe.getInstance().alpha.getValue().floatValue() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)(Wireframe.getInstance().alpha.getValue().floatValue() / 255.0f));
                        }
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                        GL11.glEnable((int)2896);
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }
                    boolean flag1 = this.setDoRenderBrightness(entity, partialTicks);
                    if (!(entity instanceof EntityPlayer) || Wireframe.getInstance().isOn() && Wireframe.getInstance().mode.getValue().equals((Object)Wireframe.RenderMode.WIREFRAME) && Wireframe.getInstance().playerModel.getValue().booleanValue() || Wireframe.getInstance().isOff()) {
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                    }
                    if (flag1) {
                        this.unsetBrightness();
                    }
                    GlStateManager.depthMask((boolean)true);
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                        this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                    }
                    if (Wireframe.getInstance().isOn() && Wireframe.getInstance().players.getValue().booleanValue() && entity instanceof EntityPlayer && Wireframe.getInstance().mode.getValue().equals((Object)Wireframe.RenderMode.WIREFRAME)) {
                        this.red = (float)Wireframe.getInstance().red.getValue().intValue() / 255.0f;
                        this.green = (float)Wireframe.getInstance().green.getValue().intValue() / 255.0f;
                        this.blue = (float)Wireframe.getInstance().blue.getValue().intValue() / 255.0f;
                        GlStateManager.pushMatrix();
                        GL11.glPushAttrib((int)1048575);
                        GL11.glPolygonMode((int)1032, (int)6913);
                        GL11.glDisable((int)3553);
                        GL11.glDisable((int)2896);
                        GL11.glDisable((int)2929);
                        GL11.glEnable((int)2848);
                        GL11.glEnable((int)3042);
                        GL11.glBlendFunc((int)770, (int)771);
                        if (OyVey.friendManager.isFriend(entity.getName()) || entity == Minecraft.getMinecraft().player) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)(Wireframe.getInstance().alpha.getValue().floatValue() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(Wireframe.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)(Wireframe.getInstance().alpha.getValue().floatValue() / 255.0f));
                        }
                        GL11.glLineWidth((float)Wireframe.getInstance().lineWidth.getValue().floatValue());
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                        GL11.glEnable((int)2896);
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }
                }
                GlStateManager.disableRescaleNormal();
            }
            catch (Exception var20) {
                LOGGER.error("Couldn't render entity", (Throwable)var20);
            }
            GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            MinecraftForge.EVENT_BUS.post((Event)new RenderLivingEvent.Post(entity, (RenderLivingBase)RenderLivingBase.class.cast((Object)this), partialTicks, x, y, z));
        }
    }

    @Shadow
    protected abstract boolean isVisible(EntityLivingBase var1);

    @Shadow
    protected abstract float getSwingProgress(T var1, float var2);

    @Shadow
    protected abstract float interpolateRotation(float var1, float var2, float var3);

    @Shadow
    protected abstract float handleRotationFloat(T var1, float var2);

    @Shadow
    protected abstract void applyRotations(T var1, float var2, float var3, float var4);

    @Shadow
    public abstract float prepareScale(T var1, float var2);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract boolean setScoreTeamColor(T var1);

    @Shadow
    protected abstract void renderLivingAt(T var1, double var2, double var4, double var6);

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract void renderModel(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

    @Shadow
    protected abstract void renderLayers(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

    @Shadow
    protected abstract boolean setDoRenderBrightness(T var1, float var2);
}

