/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.util.Util;
import net.minecraft.client.Minecraft;

public class HudUtil
implements Util {
    public static String getPingSatus() {
        String line = "";
        int ping = OyVey.serverManager.getPing();
        line = ping > 100 ? line + ChatFormatting.RED : (ping > 50 ? line + ChatFormatting.YELLOW : line + ChatFormatting.GREEN);
        return line + " " + ping;
    }

    public static String getTpsStatus() {
        String line = "";
        double tps = Math.ceil(OyVey.serverManager.getTPS());
        line = tps > 16.0 ? line + ChatFormatting.GREEN : (tps > 10.0 ? line + ChatFormatting.YELLOW : line + ChatFormatting.RED);
        return line + " " + tps;
    }

    public static String getFpsStatus() {
        String line = "";
        int fps = Minecraft.getDebugFPS();
        line = fps > 120 ? line + ChatFormatting.GREEN : (fps > 60 ? line + ChatFormatting.YELLOW : line + ChatFormatting.RED);
        return line + " " + fps;
    }
}

