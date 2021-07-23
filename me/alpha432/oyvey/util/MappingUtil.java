/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import net.minecraft.client.Minecraft;

public class MappingUtil {
    public static final String tickLength = MappingUtil.isObfuscated() ? "field_194149_e" : "tickLength";
    public static final String timer = MappingUtil.isObfuscated() ? "field_71428_T" : "timer";
    public static final String placedBlockDirection = MappingUtil.isObfuscated() ? "field_149579_d" : "placedBlockDirection";
    public static final String playerPosLookYaw = MappingUtil.isObfuscated() ? "field_148936_d" : "yaw";
    public static final String playerPosLookPitch = MappingUtil.isObfuscated() ? "field_148937_e" : "pitch";
    public static final String isInWeb = MappingUtil.isObfuscated() ? "field_70134_J" : "isInWeb";
    public static final String cPacketPlayerYaw = MappingUtil.isObfuscated() ? "field_149476_e" : "yaw";
    public static final String cPacketPlayerPitch = MappingUtil.isObfuscated() ? "field_149473_f" : "pitch";
    public static final String renderManagerRenderPosX = MappingUtil.isObfuscated() ? "field_78725_b" : "renderPosX";
    public static final String renderManagerRenderPosY = MappingUtil.isObfuscated() ? "field_78726_c" : "renderPosY";
    public static final String renderManagerRenderPosZ = MappingUtil.isObfuscated() ? "field_78723_d" : "renderPosZ";
    public static final String rightClickDelayTimer = MappingUtil.isObfuscated() ? "field_71467_ac" : "rightClickDelayTimer";
    public static final String sPacketEntityVelocityMotionX = MappingUtil.isObfuscated() ? "field_70159_w" : "motionX";
    public static final String sPacketEntityVelocityMotionY = MappingUtil.isObfuscated() ? "field_70181_x" : "motionY";
    public static final String sPacketEntityVelocityMotionZ = MappingUtil.isObfuscated() ? "field_70179_y" : "motionZ";

    public static boolean isObfuscated() {
        try {
            return Minecraft.class.getDeclaredField("instance") == null;
        }
        catch (Exception var1) {
            return true;
        }
    }
}

