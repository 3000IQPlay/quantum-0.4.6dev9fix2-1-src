/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.HudUtil;
import me.alpha432.oyvey.util.ItemUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.TextUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD
extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static RenderItem itemRender;
    private static HUD INSTANCE;
    private final Setting<Boolean> grayNess = this.register(new Setting<Boolean>("Gray", true));
    private final Setting<Boolean> renderingUp = this.register(new Setting<Boolean>("RenderingUp", false));
    private final Setting<Boolean> watermark = this.register(new Setting<Boolean>("Watermark", false));
    public final Setting<Watermark> mode = this.register(new Setting<Object>("Mode", (Object)Watermark.None, v -> this.watermark.getValue()));
    private final Setting<String> customWatermark = this.register(new Setting<String>("WatermarkName", "Quantum v0.4.6", v -> this.watermark.getValue()));
    private final Setting<Integer> wateX = this.register(new Setting<Object>("WPosX", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(600), v -> this.watermark.getValue()));
    private final Setting<Integer> wateY = this.register(new Setting<Object>("WPosY", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(530), v -> this.watermark.getValue()));
    private final Setting<Boolean> arrayList = this.register(new Setting<Boolean>("ActiveModules", true));
    private final Setting<Float> arraylistx = this.register(new Setting<Object>("PosX", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(40.0f), v -> this.arrayList.getValue()));
    private final Setting<Integer> arraylisty = this.register(new Setting<Object>("PosY", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(530), v -> this.arrayList.getValue()));
    public Setting<Integer> animationHorizontalTime = this.register(new Setting<Object>("HorizontalTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> this.arrayList.getValue()));
    public Setting<Integer> animationVerticalTime = this.register(new Setting<Object>("VerticalTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> this.arrayList.getValue()));
    private final Setting<Boolean> pvpInfo = this.register(new Setting<Boolean>("PvpInfo", false));
    private final Setting<Integer> pvpY = this.register(new Setting<Object>("PPosY", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(250), v -> this.pvpInfo.getValue()));
    private final Setting<Boolean> friendlist = this.register(new Setting<Boolean>("FriendList", false));
    private final Setting<Integer> friendlistx = this.register(new Setting<Object>("FPosX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(600), v -> this.friendlist.getValue()));
    private final Setting<Integer> friendlisty = this.register(new Setting<Object>("FPosY", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(530), v -> this.friendlist.getValue()));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", false));
    private final Setting<Integer> coordsposy = this.register(new Setting<Object>("CPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(530), v -> this.coords.getValue()));
    private final Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", false));
    private final Setting<Integer> directionposy = this.register(new Setting<Object>("DPosY", Integer.valueOf(4), Integer.valueOf(0), Integer.valueOf(530), v -> this.direction.getValue()));
    private final Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", false));
    private final Setting<Boolean> totems = this.register(new Setting<Boolean>("Totems", false));
    private final Setting<Greeter> greeter = this.register(new Setting<Greeter>("Greeter", Greeter.None));
    private final Setting<String> spoofGreeter = this.register(new Setting<Object>("GreeterName", "Welcome to Quantum", v -> this.greeter.getValue() == Greeter.Custom));
    private final Setting<Integer> greeterposy = this.register(new Setting<Object>("GPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> this.greeter.getValue() == Greeter.Custom));
    private final Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", false));
    private final Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", false));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", false));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", false));
    private final Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", false));
    private final Setting<Boolean> lag = this.register(new Setting<Boolean>("LagNotifier", false));
    public Setting<String> command = this.register(new Setting<String>("Command", "6ixGod+"));
    public Setting<TextUtil.Color> bracketColor = this.register(new Setting<TextUtil.Color>("BracketColor", TextUtil.Color.RED));
    public Setting<TextUtil.Color> commandColor = this.register(new Setting<TextUtil.Color>("NameColor", TextUtil.Color.GRAY));
    public Setting<String> commandBracket = this.register(new Setting<String>("BracketL", "["));
    public Setting<String> commandBracket2 = this.register(new Setting<String>("BracketR", "]"));
    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("ToggleNotifs", false));
    public Setting<Boolean> magenDavid = this.register(new Setting<Boolean>("FutureGUI", false));
    public Setting<RenderingMode> renderingMode = this.register(new Setting<RenderingMode>("Ordering", RenderingMode.Alphabet));
    public Setting<Boolean> time = this.register(new Setting<Boolean>("Time", Boolean.valueOf(false), "The time"));
    public Setting<Integer> lagTime = this.register(new Setting<Integer>("LagTime", 1000, 0, 2000));
    public static Entity target;
    private int color;
    public float hue;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    private final Timer timer = new Timer();
    private final Map<String, Integer> players = new HashMap<String, Integer>();
    private int startcolor1;
    private int endcolor1;

    public HUD() {
        super("HUD", "hud", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String str1;
        String fpsText;
        int i;
        String grayString;
        int j;
        if (HUD.fullNullCheck()) {
            return;
        }
        if (this.friendlist.getValue().booleanValue()) {
            this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
            this.renderFriends();
        }
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        if (this.watermark.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(this.customWatermark.getValue(), this.wateX.getValue().intValue(), this.wateY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = new int[]{1};
                    char[] stringToCharArray = this.customWatermark.getValue().toCharArray();
                    float f = 0.0f;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), (float)this.wateX.getValue().intValue() + f, this.wateY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += (float)this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(this.customWatermark.getValue(), this.wateX.getValue().intValue(), this.wateY.getValue().intValue(), this.color, true);
            }
        }
        if (this.pvpInfo.getValue().booleanValue()) {
            char[] stringToCharArray;
            OyVey.textManager.drawStringWithShadow("Quantum", 1.0f, this.pvpY.getValue() - 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color);
            String caOn = "CA" + ChatFormatting.GREEN + " ON";
            String caOff = "CA" + ChatFormatting.RED + " OFF";
            this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
            if (OyVey.moduleManager.getModuleByName("AutoCrystal").isEnabled() || OyVey.moduleManager.getModuleByName("CrystalAura").isEnabled()) {
                if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                    if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                        this.renderer.drawString(caOn, 2.0f, this.pvpY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    } else {
                        int[] arrayOfInt = new int[]{1};
                        stringToCharArray = caOn.toCharArray();
                        float f = 0.0f;
                        char[] cArray = stringToCharArray;
                        int n = cArray.length;
                        for (int k = 0; k < n; ++k) {
                            char c = cArray[k];
                            this.renderer.drawString(String.valueOf(c), 2.0f + f, this.pvpY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                            f += (float)this.renderer.getStringWidth(String.valueOf(c));
                            arrayOfInt[0] = arrayOfInt[0] + 1;
                        }
                    }
                } else {
                    this.renderer.drawString(caOn, 2.0f, this.pvpY.getValue().intValue(), this.color, true);
                }
            }
            if (OyVey.moduleManager.getModuleByName("AutoCrystal").isDisabled() && !OyVey.moduleManager.getModuleByName("CrystalAura").isEnabled() || OyVey.moduleManager.getModuleByName("CrystalAura").isDisabled() && !OyVey.moduleManager.getModuleByName("AutoCrystal").isEnabled()) {
                if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                    if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                        this.renderer.drawString(caOff, 2.0f, this.pvpY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    } else {
                        int[] arrayOfInt = new int[]{1};
                        stringToCharArray = caOff.toCharArray();
                        float f = 0.0f;
                        for (char c : stringToCharArray) {
                            this.renderer.drawString(String.valueOf(c), 2.0f + f, this.pvpY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                            f += (float)this.renderer.getStringWidth(String.valueOf(c));
                            arrayOfInt[0] = arrayOfInt[0] + 1;
                        }
                    }
                } else {
                    this.renderer.drawString(caOff, 2.0f, this.pvpY.getValue().intValue(), this.color, true);
                }
            }
            String totnull = "" + ChatFormatting.RED + "0";
            String totslot = "" + ChatFormatting.GREEN + String.valueOf(ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING));
            if (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) != 0) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static || ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Sideway) {
                    this.renderer.drawString(totslot, 2.0f, this.pvpY.getValue() + 10, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                }
            } else if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static || ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Sideway) {
                this.renderer.drawString(totnull, 2.0f, this.pvpY.getValue() + 10, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            }
            String pingg = "" + ChatFormatting.GREEN + HudUtil.getPingSatus();
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(pingg, 2.0f, this.pvpY.getValue() + 20, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                this.renderer.drawString(pingg, 2.0f, this.pvpY.getValue() + 20, this.color, true);
            }
        }
        int[] counter1 = new int[]{1};
        int n = j = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() == false ? 14 : 0;
        if (this.arrayList.getValue().booleanValue()) {
            Module module;
            String str;
            if (this.renderingUp.getValue().booleanValue()) {
                if (this.renderingMode.getValue() == RenderingMode.Alphabet) {
                    for (int k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); ++k) {
                        str = OyVey.moduleManager.sortedModulesABC.get(k);
                        this.renderer.drawString(str, width - 2 - this.renderer.getStringWidth(str), 2 + j * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); ++k) {
                        module = OyVey.moduleManager.sortedModules.get(k);
                        String str2 = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), 2 + j * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (this.renderingMode.getValue() == RenderingMode.Alphabet) {
                for (int k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); ++k) {
                    str = OyVey.moduleManager.sortedModulesABC.get(k);
                    this.renderer.drawString(str, width - 2 - this.renderer.getStringWidth(str), height - (j += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); ++k) {
                    module = OyVey.moduleManager.sortedModules.get(k);
                    String str3 = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    this.renderer.drawString(str3, width - 2 - this.renderer.getStringWidth(str3), height - (j += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        String string = grayString = this.grayNess.getValue() != false ? String.valueOf(ChatFormatting.GRAY) : "";
        int n2 = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() != false ? 13 : (i = this.renderingUp.getValue() != false ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            if (this.potions.getValue().booleanValue()) {
                ArrayList effects = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = OyVey.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str = grayString + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (this.potions.getValue().booleanValue()) {
                ArrayList effects = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = OyVey.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str = grayString + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int)HUD.mc.player.posX;
        int n3 = (int)HUD.mc.player.posY;
        int posZ = (int)HUD.mc.player.posZ;
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(HUD.mc.player.posX * (double)nether);
        int hposZ = (int)(HUD.mc.player.posZ * (double)nether);
        i = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? posX + ", " + n3 + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET : posX + ", " + n3 + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]");
        String direction = this.direction.getValue() != false ? OyVey.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue() != false ? coordinates : "";
        i += 10;
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            String rainbowCoords;
            String string2 = this.coords.getValue() != false ? "XYZ " + (inHell ? posX + ", " + n3 + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + n3 + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (rainbowCoords = "");
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0f, height - i - 11, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0f, height - i, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter2 = new int[]{1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + s, height - i - 11, ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    s += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = new int[]{1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0f;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + u, height - i, ColorUtil.rainbow(counter3[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    u += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(direction, 2.0f, height - i - 11, this.color, true);
            this.renderer.drawString(coords, 2.0f, height - i, this.color, true);
        }
        if (this.armor.getValue().booleanValue()) {
            this.renderArmorHUD(true);
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue() != Greeter.None) {
            this.renderGreeter();
        }
        if (this.lag.getValue().booleanValue()) {
            this.renderLag();
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "";
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        switch (this.greeter.getValue()) {
            case Custom: {
                text = text + this.spoofGreeter.getValue();
            }
        }
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Sideway) {
                this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, this.greeterposy.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            }
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, this.greeterposy.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] arrayOfInt = new int[]{1};
                char[] stringToCharArray = text.toCharArray();
                float f = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    f += (float)this.renderer.getStringWidth(String.valueOf(c));
                    arrayOfInt[0] = arrayOfInt[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.color, true);
        }
    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;
        if (OyVey.serverManager.isServerNotResponding()) {
            String text = ChatFormatting.RED + "Server lagging for " + MathUtil.round((float)OyVey.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::func_190916_E).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            boolean iteration = false;
            int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", x + 19 - 2 - this.renderer.getStringWidth(totems + ""), y + 9, 0xFFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            int dmg = 0;
            int itemDurability = is.getMaxDamage() - is.getItemDamage();
            float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
            float red = 1.0f - green;
            dmg = percent ? 100 - (int)(red * 100.0f) : itemDurability;
            this.renderer.drawStringWithShadow(dmg + "", x + 8 - this.renderer.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(AttackEntityEvent event) {
        this.shouldIncrement = true;
    }

    @Override
    public void onLoad() {
        OyVey.commandManager.setClientMessage(this.getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
            OyVey.commandManager.setClientMessage(this.getCommandMessage());
        }
    }

    public String getCommandMessage() {
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return this.commandBracket.getValue() + this.command.getValue() + this.commandBracket2.getValue();
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (Map.Entry<String, Integer> player : this.players.entrySet()) {
                String text = player.getKey() + " ";
                int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, y, this.color, true);
                y += textheight;
            }
        }
    }

    private void renderFriends() {
        block8: {
            ArrayList<String> friends;
            block7: {
                friends = new ArrayList<String>();
                for (EntityPlayer player : HUD.mc.world.playerEntities) {
                    if (!OyVey.friendManager.isFriend(player.getName())) continue;
                    friends.add(player.getName());
                }
                if (!ClickGui.getInstance().rainbow.getValue().booleanValue()) break block7;
                int y = this.friendlisty.getValue();
                int x = this.friendlistx.getValue();
                if (ClickGui.getInstance().rainbowModeHud.getValue() != ClickGui.rainbowMode.Static) break block8;
                if (friends.isEmpty()) {
                    this.renderer.drawString("No friends online", x, y, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    this.renderer.drawString("Friends:", x, y, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    y += 12;
                    for (String friend : friends) {
                        this.renderer.drawString(friend, x, y, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        y += 12;
                    }
                }
                break block8;
            }
            int y = this.friendlisty.getValue();
            int x = this.friendlistx.getValue();
            if (friends.isEmpty()) {
                this.renderer.drawString("No friends online", x, y, this.color, true);
            } else {
                this.renderer.drawString("Friends:", x, y, this.color, true);
                y += 12;
                for (String friend : friends) {
                    this.renderer.drawString(friend, x, y, this.color, true);
                    y += 12;
                }
            }
        }
    }

    static {
        INSTANCE = new HUD();
    }

    public static enum Watermark {
        None,
        Custom;

    }

    public static enum Greeter {
        None,
        Custom;

    }

    public static enum RenderingMode {
        Length,
        Alphabet;

    }

    public static enum Page {
        Default,
        SemiCustom,
        Custom;

    }
}

