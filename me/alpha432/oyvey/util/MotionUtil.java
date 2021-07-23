/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class MotionUtil
implements Util {
    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0.0f || entity.moveStrafing != 0.0f;
    }

    public static boolean isMovings() {
        return (double)Minecraft.getMinecraft().player.moveForward != 0.0 || (double)Minecraft.getMinecraft().player.moveStrafing != 0.0;
    }

    public static void setSpeed(EntityLivingBase entity, double speed) {
        double[] dir = MotionUtil.forward(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isPotionActive(Potion.getPotionById((int)1))) {
            int amplifier = Minecraft.getMinecraft().player.getActivePotionEffect(Potion.getPotionById((int)1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static double[] forward(double speed) {
        float forward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float side = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
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

    public static double[] getMoveSpeed(double speed) {
        float forward = MotionUtil.mc.player.movementInput.moveForward;
        float strafe = MotionUtil.mc.player.movementInput.moveStrafe;
        float yaw = MotionUtil.mc.player.rotationYaw;
        if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
                strafe = 0.0f;
            }
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double motionX = (double)forward * speed * cos + (double)strafe * speed * sin;
        double motionZ = (double)forward * speed * sin - (double)strafe * speed * cos;
        if (!MotionUtil.isMovingMomentum()) {
            motionX = 0.0;
            motionZ = 0.0;
        }
        return new double[]{motionX, motionZ};
    }

    public static boolean isMovingMomentum() {
        return (double)MotionUtil.mc.player.moveForward != 0.0 || (double)MotionUtil.mc.player.moveStrafing != 0.0;
    }
}

