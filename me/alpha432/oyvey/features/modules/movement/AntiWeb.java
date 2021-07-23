/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.BlockCollisionBoundingBoxEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiWeb
extends Module {
    public Setting<Boolean> disableBB = this.register(new Setting<Boolean>("AddBB", true));
    public Setting<Float> bbOffset = this.register(new Setting<Float>("BBOffset", Float.valueOf(0.4f), Float.valueOf(-2.0f), Float.valueOf(2.0f)));
    public Setting<Boolean> onGround = this.register(new Setting<Boolean>("On Ground", true));
    public Setting<Float> motionY = this.register(new Setting<Float>("Set MotionY", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    public Setting<Float> motionX = this.register(new Setting<Float>("Set MotionX", Float.valueOf(0.8f), Float.valueOf(-1.0f), Float.valueOf(5.0f)));

    public AntiWeb() {
        super("AntiWeb", "aw", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void bbEvent(BlockCollisionBoundingBoxEvent event) {
        if (AntiWeb.nullCheck()) {
            return;
        }
        if (AntiWeb.mc.world.getBlockState(event.getPos()).getBlock() instanceof BlockWeb && this.disableBB.getValue().booleanValue()) {
            event.setCanceledE(true);
            event.setBoundingBox(Block.FULL_BLOCK_AABB.contract(0.0, (double)this.bbOffset.getValue().floatValue(), 0.0));
        }
    }

    @Override
    public void onUpdate() {
        if (OyVey.moduleManager.isModuleEnabled("WebTP")) {
            return;
        }
        if (AntiWeb.mc.player.isInWeb && !OyVey.moduleManager.isModuleEnabled("Step") || AntiWeb.mc.player.isInWeb && !OyVey.moduleManager.isModuleEnabled("StepTwo")) {
            if (Keyboard.isKeyDown((int)AntiWeb.mc.gameSettings.keyBindSneak.keyCode)) {
                AntiWeb.mc.player.isInWeb = true;
                AntiWeb.mc.player.motionY *= (double)this.motionY.getValue().floatValue();
            } else if (this.onGround.getValue().booleanValue()) {
                AntiWeb.mc.player.onGround = false;
            }
            if (Keyboard.isKeyDown((int)AntiWeb.mc.gameSettings.keyBindForward.keyCode) || Keyboard.isKeyDown((int)AntiWeb.mc.gameSettings.keyBindBack.keyCode) || Keyboard.isKeyDown((int)AntiWeb.mc.gameSettings.keyBindLeft.keyCode) || Keyboard.isKeyDown((int)AntiWeb.mc.gameSettings.keyBindRight.keyCode)) {
                AntiWeb.mc.player.isInWeb = false;
                AntiWeb.mc.player.motionX *= (double)this.motionX.getValue().floatValue();
                AntiWeb.mc.player.motionZ *= (double)this.motionX.getValue().floatValue();
            }
        }
    }
}

