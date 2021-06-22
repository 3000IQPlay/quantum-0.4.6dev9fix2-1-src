package me.alpha432.oyvey.features.gui.components.items.buttons;

import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.gui.components.Component;
import me.alpha432.oyvey.features.gui.components.items.Item;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HUD;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModuleButton extends Button {
  private final Module module;
  
  private final ResourceLocation logo = new ResourceLocation("textures/oyvey.png");
  
  private List<Item> items = new ArrayList<>();
  
  private boolean subOpen;
  
  public ModuleButton(Module module) {
    super(module.getName());
    this.module = module;
    initSettings();
  }
  
  public static void drawCompleteImage(float posX, float posY, int width, int height) {
    GL11.glPushMatrix();
    GL11.glTranslatef(posX, posY, 0.0F);
    GL11.glBegin(7);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(0.0F, height, 0.0F);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(width, height, 0.0F);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(width, 0.0F, 0.0F);
    GL11.glEnd();
    GL11.glPopMatrix();
  }
  
  public void initSettings() {
    ArrayList<Item> newItems = new ArrayList<>();
    if (!this.module.getSettings().isEmpty())
      for (Setting setting : this.module.getSettings()) {
        if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled"))
          newItems.add(new BooleanButton(setting)); 
        if (setting.getValue() instanceof me.alpha432.oyvey.features.setting.Bind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud"))
          newItems.add(new BindButton(setting)); 
        if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName"))
          newItems.add(new StringButton(setting)); 
        if (setting.isNumberSetting() && setting.hasRestriction()) {
          newItems.add(new Slider(setting));
          continue;
        } 
        if (!setting.isEnumSetting())
          continue; 
        newItems.add(new EnumButton(setting));
      }  
    newItems.add(new BindButton(this.module.getSettingByName("Keybind")));
    this.items = newItems;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (!this.items.isEmpty()) {
      if (((Boolean)(HUD.getInstance()).magenDavid.getValue()).booleanValue()) {
        mc.getTextureManager().bindTexture(this.logo);
        drawCompleteImage(this.x - 1.5F + this.width - 7.4F, this.y - 2.2F - OyVeyGui.getClickGui().getTextOffset(), 8, 8);
      } 
      if (this.subOpen) {
        float height = 1.0F;
        for (Item item : this.items) {
          Component.counter1[0] = Component.counter1[0] + 1;
          if (!item.isHidden()) {
            item.setLocation(this.x + 1.0F, this.y + (height += 15.0F));
            item.setHeight(15);
            item.setWidth(this.width - 9);
            item.drawScreen(mouseX, mouseY, partialTicks);
          } 
          item.update();
        } 
      } 
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (!this.items.isEmpty()) {
      if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
        this.subOpen = !this.subOpen;
        mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } 
      if (this.subOpen)
        for (Item item : this.items) {
          if (item.isHidden())
            continue; 
          item.mouseClicked(mouseX, mouseY, mouseButton);
        }  
    } 
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    super.onKeyTyped(typedChar, keyCode);
    if (!this.items.isEmpty() && this.subOpen)
      for (Item item : this.items) {
        if (item.isHidden())
          continue; 
        item.onKeyTyped(typedChar, keyCode);
      }  
  }
  
  public int getHeight() {
    if (this.subOpen) {
      int height = 14;
      for (Item item : this.items) {
        if (item.isHidden())
          continue; 
        height += item.getHeight() + 1;
      } 
      return height + 2;
    } 
    return 14;
  }
  
  public Module getModule() {
    return this.module;
  }
  
  public void toggle() {
    this.module.toggle();
  }
  
  public boolean getState() {
    return this.module.isEnabled();
  }
}
