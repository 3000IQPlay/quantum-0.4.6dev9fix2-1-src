/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.Wrapper;
import net.minecraft.client.Minecraft;

public interface Util {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static double[] directionSpeed(double speed) {
        float forward = Wrapper.INSTANCE.mc().player.movementInput.moveForward;
        float side = Wrapper.INSTANCE.mc().player.movementInput.moveStrafe;
        float yaw = Wrapper.INSTANCE.mc().player.prevRotationYaw + (Wrapper.INSTANCE.mc().player.rotationYaw - Wrapper.INSTANCE.mc().player.prevRotationYaw) * Wrapper.INSTANCE.mc().getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }
}

