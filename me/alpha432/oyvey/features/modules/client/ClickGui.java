/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.Gui));
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix = this.register(new Setting<Object>("Prefix", ".", v -> this.setting.getValue() == Settings.Gui));
    public Setting<Boolean> customFov = this.register(new Setting<Object>("CustomFov", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Float> fov = this.register(new Setting<Object>("Fov", Float.valueOf(150.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f), v -> this.setting.getValue() == Settings.Gui && this.customFov.getValue() != false));
    public Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Integer> hoverAlpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Integer> alphaBox = this.register(new Setting<Object>("AlphaBox", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Integer> alpha = this.register(new Setting<Object>("HoverAlpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gui));
    public Setting<Boolean> rainbow = this.register(new Setting<Object>("Rainbow", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Gui));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("HUD", (Object)rainbowMode.Static, v -> this.rainbow.getValue() != false && this.setting.getValue() == Settings.Gui));
    public Setting<rainbowModeArray> rainbowModeA = this.register(new Setting<Object>("ArrayList", (Object)rainbowModeArray.Static, v -> this.rainbow.getValue() != false && this.setting.getValue() == Settings.Gui));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue() != false && this.setting.getValue() == Settings.Gui));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Float.valueOf(255.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue() != false && this.setting.getValue() == Settings.Gui));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", Float.valueOf(100.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue() != false && this.setting.getValue() == Settings.Gui));
    public Setting<Boolean> rainbowg = this.register(new Setting<Object>("Rainbow", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Boolean> guiComponent = this.register(new Setting<Object>("Gui Component", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_red = this.register(new Setting<Object>("RedL", Integer.valueOf(105), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_green = this.register(new Setting<Object>("GreenL", Integer.valueOf(162), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_blue = this.register(new Setting<Object>("BlueL", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_red1 = this.register(new Setting<Object>("RedR", Integer.valueOf(143), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_green1 = this.register(new Setting<Object>("GreenR", Integer.valueOf(140), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_blue1 = this.register(new Setting<Object>("BlueR", Integer.valueOf(213), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_alpha = this.register(new Setting<Object>("AlphaL", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Integer> g_alpha1 = this.register(new Setting<Object>("AlphaR", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Gradient));
    public Setting<Mode> mode = this.register(new Setting<Object>("Mode", (Object)Mode.COLOR, v -> this.setting.getValue() == Settings.Background));
    public Setting<Integer> backgroundAlpha = this.register(new Setting<Object>("Background Alpha", Integer.valueOf(160), Integer.valueOf(0), Integer.valueOf(255), v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
    public Setting<Integer> gb_red = this.register(new Setting<Object>("RedBG", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255), v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
    public Setting<Integer> gb_green = this.register(new Setting<Object>("GreenBG", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255), v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
    public Setting<Integer> gb_blue = this.register(new Setting<Object>("BlueBG", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255), v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
    private int color;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue().booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                OyVey.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + OyVey.commandManager.getPrefix());
            }
            OyVey.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen((GuiScreen)OyVeyGui.getClickGui());
    }

    @Override
    public void onLoad() {
        OyVey.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        OyVey.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        this.drawBackground();
    }

    public void drawBackground() {
        if (this.mode.getValue() == Mode.COLOR) {
            if (ClickGui.getInstance().isEnabled()) {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(this.gb_red.getValue(), this.gb_green.getValue(), this.gb_blue.getValue(), this.backgroundAlpha.getValue()));
            } else {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(0, 0, 0, 0));
            }
        }
        if (this.mode.getValue() == Mode.NONE) {
            if (ClickGui.getInstance().isEnabled()) {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(this.gb_red.getValue(), this.gb_green.getValue(), this.gb_blue.getValue(), this.backgroundAlpha.getValue()));
            } else {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(0, 0, 0, 0));
            }
        }
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof OyVeyGui)) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof OyVeyGui) {
            Util.mc.displayGuiScreen(null);
        }
    }

    public final int getColor() {
        return this.color;
    }

    public static enum Mode {
        COLOR,
        BLUR,
        NONE;

    }

    public static enum Settings {
        Gui,
        Gradient,
        Background;

    }

    public static enum rainbowMode {
        Static,
        Sideway;

    }

    public static enum rainbowModeArray {
        Static,
        Up;

    }
}

