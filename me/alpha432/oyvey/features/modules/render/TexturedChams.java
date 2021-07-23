/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class TexturedChams
extends Module {
    public static Setting<Integer> red;
    public static Setting<Integer> green;
    public static Setting<Integer> blue;
    public static Setting<Integer> alpha;
    public static Setting<Boolean> rainbow;
    public static Setting<Integer> rainbowhue;

    public TexturedChams() {
        super("TexturedChams", "hi yes", Module.Category.RENDER, true, false, true);
        red = this.register(new Setting<Integer>("Red", 168, 0, 255));
        green = this.register(new Setting<Integer>("Green", 0, 0, 255));
        blue = this.register(new Setting<Integer>("Blue", 232, 0, 255));
        alpha = this.register(new Setting<Integer>("Alpha", 150, 0, 255));
        rainbow = this.register(new Setting<Boolean>("Rainbow", false));
        rainbowhue = this.register(new Setting<Integer>("Brightness", 150, 0, 255));
    }
}

