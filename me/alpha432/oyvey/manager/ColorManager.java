/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager;

import java.awt.Color;
import me.alpha432.oyvey.features.gui.components.Component;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.ColorUtil;

public class ColorManager {
    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float alpha = 1.0f;
    private Color color = new Color(this.red, this.green, this.blue, this.alpha);
    private static Color colorRealth = new Color(255, 255, 255);

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getColorAsInt() {
        return ColorUtil.toRGBA(this.color);
    }

    public int getColorAsIntFullAlpha() {
        return ColorUtil.toRGBA(new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 255));
    }

    public int getColorWithAlpha(int alpha) {
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            return ColorUtil.rainbow(Component.counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB();
        }
        return ColorUtil.toRGBA(new Color(this.red, this.green, this.blue, (float)alpha / 255.0f));
    }

    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.updateColor();
    }

    public void updateColor() {
        this.setColor(new Color(this.red, this.green, this.blue, this.alpha));
    }

    public void setColor(int red, int green, int blue, int alpha) {
        this.red = (float)red / 255.0f;
        this.green = (float)green / 255.0f;
        this.blue = (float)blue / 255.0f;
        this.alpha = (float)alpha / 255.0f;
        this.updateColor();
    }

    public void setRed(float red) {
        this.red = red;
        this.updateColor();
    }

    public void setGreen(float green) {
        this.green = green;
        this.updateColor();
    }

    public void setBlue(float blue) {
        this.blue = blue;
        this.updateColor();
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.updateColor();
    }

    public static int toRGBA(Color color) {
        return ColorManager.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toRGBA(double r, double g, double b, double a) {
        return ColorManager.toRGBA((float)r, (float)g, (float)b, (float)a);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorManager.toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }

    public static int toRGBA(float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorManager.toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorManager.toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorManager.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static Color getColorRealth() {
        return colorRealth;
    }
}

