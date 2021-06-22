package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.event.events.RenderItemEvent;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.render.HandChams;
import me.alpha432.oyvey.features.modules.render.NoRender;
import me.alpha432.oyvey.features.modules.render.SmallShield;
import me.alpha432.oyvey.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class}, priority = 2147483647)
public abstract class MixinItemRenderer {
  @Shadow
  @Final
  public Minecraft mc;
  
  private boolean injection = true;
  
  @Shadow
  public abstract void renderItemInFirstPerson(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, EnumHand paramEnumHand, float paramFloat3, ItemStack paramItemStack, float paramFloat4);
  
  @Shadow
  protected abstract void renderArmFirstPerson(float paramFloat1, float paramFloat2, EnumHandSide paramEnumHandSide);
  
  @Inject(method = {"renderFireInFirstPerson"}, at = {@At("HEAD")}, cancellable = true)
  public void renderFireInFirstPersonHook(CallbackInfo info) {
    if (NoRender.getInstance().isEnabled() && ((Boolean)(NoRender.getInstance()).fire.getValue()).booleanValue())
      info.cancel(); 
  }
  
  @Redirect(method = {"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
  public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
    RenderItemEvent event = new RenderItemEvent(0.5600000023841858D, (-0.52F + y * -0.6F), -0.7200000286102295D, -0.5600000023841858D, (-0.52F + y * -0.6F), -0.7200000286102295D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);
    MinecraftForge.EVENT_BUS.post((Event)event);
    if (hand == EnumHandSide.RIGHT) {
      GlStateManager.translate(event.getMainX(), event.getMainY(), event.getMainZ());
      GlStateManager.scale(event.getMainHandScaleX(), event.getMainHandScaleY(), event.getMainHandScaleZ());
      GlStateManager.rotate((float)event.getMainRAngel(), (float)event.getMainRx(), (float)event.getMainRy(), (float)event.getMainRz());
    } else {
      GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
      GlStateManager.scale(event.getOffHandScaleX(), event.getOffHandScaleY(), event.getOffHandScaleZ());
      GlStateManager.rotate((float)event.getOffRAngel(), (float)event.getOffRx(), (float)event.getOffRy(), (float)event.getOffRz());
    } 
  }
  
  @Inject(method = {"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at = {@At("HEAD")}, cancellable = true)
  public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
    if (this.injection) {
      info.cancel();
      SmallShield offset = SmallShield.getINSTANCE();
      float xOffset = 0.0F;
      float yOffset = 0.0F;
      this.injection = false;
      if (hand == EnumHand.MAIN_HAND) {
        if (offset.isOn()) {
          xOffset = ((Float)offset.mainX.getValue()).floatValue();
          yOffset = ((Float)offset.mainY.getValue()).floatValue();
        } 
      } else if (offset.isOn()) {
        xOffset = ((Float)offset.offX.getValue()).floatValue();
        yOffset = ((Float)offset.offY.getValue()).floatValue();
      } 
      if (HandChams.getINSTANCE().isOn() && hand == EnumHand.MAIN_HAND && stack.isEmpty()) {
        if (((HandChams.RenderMode)(HandChams.getINSTANCE()).mode.getValue()).equals(HandChams.RenderMode.WIREFRAME))
          renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset); 
        GlStateManager.pushMatrix();
        if (((HandChams.RenderMode)(HandChams.getINSTANCE()).mode.getValue()).equals(HandChams.RenderMode.WIREFRAME)) {
          GL11.glPushAttrib(1048575);
        } else {
          GlStateManager.pushAttrib();
        } 
        if (((HandChams.RenderMode)(HandChams.getINSTANCE()).mode.getValue()).equals(HandChams.RenderMode.WIREFRAME))
          GL11.glPolygonMode(1032, 6913); 
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        if (((HandChams.RenderMode)(HandChams.getINSTANCE()).mode.getValue()).equals(HandChams.RenderMode.WIREFRAME)) {
          GL11.glEnable(2848);
          GL11.glEnable(3042);
        } 
        GL11.glColor4f(((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRed() / 255.0F) : (((Integer)(HandChams.getINSTANCE()).red.getValue()).intValue() / 255.0F), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getGreen() / 255.0F) : (((Integer)(HandChams.getINSTANCE()).green.getValue()).intValue() / 255.0F), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getBlue() / 255.0F) : (((Integer)(HandChams.getINSTANCE()).blue.getValue()).intValue() / 255.0F), ((Integer)(HandChams.getINSTANCE()).alpha.getValue()).intValue() / 255.0F);
        renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
      } 
      if (SmallShield.getINSTANCE().isOn() && (!stack.isEmpty || HandChams.getINSTANCE().isOff())) {
        renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
      } else if (!stack.isEmpty || HandChams.getINSTANCE().isOff()) {
        renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_, stack, p_187457_7_);
      } 
      this.injection = true;
    } 
  }
}
