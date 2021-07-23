/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

public class MovementUtil
implements Util {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static double[] directionSpeed(double speed) {
        float forward = MovementUtil.mc.player.movementInput.moveForward;
        float side = MovementUtil.mc.player.movementInput.moveStrafe;
        float yaw = MovementUtil.mc.player.prevRotationYaw + (MovementUtil.mc.player.rotationYaw - MovementUtil.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
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

    public static double[] dirSpeedNew(double speed) {
        float moveForward = MovementUtil.mc.player.movementInput.moveForward;
        float moveStrafe = MovementUtil.mc.player.movementInput.moveStrafe;
        float rotationYaw = MovementUtil.mc.player.prevRotationYaw + (MovementUtil.mc.player.rotationYaw - MovementUtil.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public static double[] futureCalc1(double d) {
        double d2;
        double d3;
        MovementInput movementInput = MovementUtil.mc.player.movementInput;
        double d4 = movementInput.moveForward;
        double d5 = movementInput.moveStrafe;
        float f = MovementUtil.mc.player.rotationYaw;
        if (d4 == 0.0 && d5 == 0.0) {
            d3 = 0.0;
            d2 = 0.0;
        } else {
            if (d4 != 0.0) {
                if (d5 > 0.0) {
                    f += (float)(d4 > 0.0 ? -45 : 45);
                } else if (d5 < 0.0) {
                    f += (float)(d4 > 0.0 ? 45 : -45);
                }
                d5 = 0.0;
                if (d4 > 0.0) {
                    d4 = 1.0;
                } else if (d4 < 0.0) {
                    d4 = -1.0;
                }
            }
            d3 = d4 * d * Math.cos(Math.toRadians(f + 90.0f)) + d5 * d * Math.sin(Math.toRadians(f + 90.0f));
            d2 = d4 * d * Math.sin(Math.toRadians(f + 90.0f)) - d5 * d * Math.cos(Math.toRadians(f + 90.0f));
        }
        return new double[]{d3, d2};
    }
}

