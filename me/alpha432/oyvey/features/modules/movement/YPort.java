/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.movement.Step;
import me.alpha432.oyvey.features.modules.movement.StepTwo;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MotionUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.EntityLivingBase;

public class YPort
extends Module {
    public Setting<Boolean> useTimer = this.register(new Setting<Boolean>("UseTimer", false));
    private final Setting<Double> yPortSpeed = this.register(new Setting<Double>("Speed", 0.1, 0.0, 1.0));
    public Setting<Boolean> stepyport = this.register(new Setting<Boolean>("Step", true));
    private Timer timer = new Timer();
    private float stepheight = 2.0f;

    public YPort() {
        super("YPort", "yp", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        EntityUtil.resetTimer();
    }

    @Override
    public void onUpdate() {
        if (YPort.mc.player.isSneaking() || YPort.mc.player.isInWater() || YPort.mc.player.isInLava() || YPort.mc.player.isOnLadder() || OyVey.moduleManager.isModuleEnabled("Strafe")) {
            return;
        }
        if (YPort.mc.player == null || YPort.mc.world == null) {
            this.disable();
            return;
        }
        this.handleYPortSpeed();
        if ((!YPort.mc.player.isOnLadder() || YPort.mc.player.isInWater() || YPort.mc.player.isInLava()) && this.stepyport.getValue().booleanValue()) {
            Step.mc.player.stepHeight = this.stepheight;
            StepTwo.mc.player.stepHeight = this.stepheight;
            return;
        }
    }

    @Override
    public void onToggle() {
        Step.mc.player.stepHeight = 0.6f;
        StepTwo.mc.player.stepHeight = 0.6f;
        YPort.mc.player.motionY = -3.0;
    }

    private void handleYPortSpeed() {
        if (!MotionUtil.isMoving((EntityLivingBase)YPort.mc.player) || YPort.mc.player.isInWater() && YPort.mc.player.isInLava() || YPort.mc.player.collidedHorizontally) {
            return;
        }
        if (YPort.mc.player.onGround) {
            if (this.useTimer.getValue().booleanValue()) {
                EntityUtil.setTimer(1.15f);
            }
            YPort.mc.player.jump();
            MotionUtil.setSpeed((EntityLivingBase)YPort.mc.player, MotionUtil.getBaseMoveSpeed() + this.yPortSpeed.getValue());
        } else {
            YPort.mc.player.motionY = -1.0;
            EntityUtil.resetTimer();
        }
    }
}

