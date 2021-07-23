/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wireframe
extends Module {
    private static Wireframe INSTANCE = new Wireframe();
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.TRUE));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("RBrightness", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue()));
    public Setting<Integer> red = this.register(new Setting<Integer>("PRed", 168, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("PGreen", 0, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("PBlue", 232, 0, 255));
    public Setting<Integer> Cred = this.register(new Setting<Integer>("CRed", 168, 0, 255));
    public Setting<Integer> Cgreen = this.register(new Setting<Integer>("CGreen", 0, 0, 255));
    public Setting<Integer> Cblue = this.register(new Setting<Integer>("CBlue", 232, 0, 255));
    public final Setting<Float> alpha = this.register(new Setting<Float>("PAlpha", Float.valueOf(255.0f), Float.valueOf(0.1f), Float.valueOf(255.0f)));
    public final Setting<Float> cAlpha = this.register(new Setting<Float>("CAlpha", Float.valueOf(255.0f), Float.valueOf(0.1f), Float.valueOf(255.0f)));
    public final Setting<Float> lineWidth = this.register(new Setting<Float>("PLineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    public final Setting<Float> crystalLineWidth = this.register(new Setting<Float>("CLineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    public Setting<RenderMode> mode = this.register(new Setting<RenderMode>("PMode", RenderMode.SOLID));
    public Setting<RenderMode> cMode = this.register(new Setting<RenderMode>("CMode", RenderMode.SOLID));
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", Boolean.FALSE));
    public Setting<Boolean> playerModel = this.register(new Setting<Boolean>("PlayerModel", Boolean.FALSE));
    public Setting<Boolean> crystals = this.register(new Setting<Boolean>("Crystals", Boolean.FALSE));
    public Setting<Boolean> crystalModel = this.register(new Setting<Boolean>("CrystalModel", Boolean.FALSE));

    public Wireframe() {
        super("Wireframe", "Draws a wireframe esp around other players.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static Wireframe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Wireframe();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }

    public static enum RenderMode {
        SOLID,
        WIREFRAME;

    }
}

