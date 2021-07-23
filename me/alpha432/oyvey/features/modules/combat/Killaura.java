/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.DamageUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.MAIN));
    public static Entity target;
    private final Timer timer = new Timer();
    private final Setting<Boolean> delay = this.register(new Setting<Object>("HitDelay", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Boolean> rotate = this.register(new Setting<Object>("Rotate", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Boolean> onlySharp = this.register(new Setting<Object>("SwordOnly", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Float> raytrace = this.register(new Setting<Object>("Raytrace", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Boolean> tps = this.register(new Setting<Object>("TpsSync", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Boolean> packet = this.register(new Setting<Object>("Packet", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Boolean> info = this.register(new Setting<Object>("Info", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MAIN));
    private final Setting<TargetMode> targetMode = this.register(new Setting<TargetMode>("Target", TargetMode.CLOSEST, v -> this.setting.getValue() == Settings.MAIN));
    public Setting<Float> health = this.register(new Setting<Object>("Health", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.targetMode.getValue() == TargetMode.SMART));
    public Setting<Boolean> players = this.register(new Setting<Object>("Players", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.TARGETS));
    public Setting<Boolean> mobs = this.register(new Setting<Object>("Mobs", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.TARGETS));
    public Setting<Boolean> animals = this.register(new Setting<Object>("Animals", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.TARGETS));
    public Setting<Boolean> vehicles = this.register(new Setting<Object>("Entities", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.TARGETS));
    public Setting<Boolean> projectiles = this.register(new Setting<Object>("Projectiles", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.TARGETS));

    public Killaura() {
        super("KillAura", "ka", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (!this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    private void doKillaura() {
        int wait;
        if (this.onlySharp.getValue().booleanValue() && !EntityUtil.holdingWeapon((EntityPlayer)Killaura.mc.player)) {
            target = null;
            return;
        }
        if (this.targetMode.getValue() != TargetMode.FOCUS || target == null || !(Killaura.mc.player.getDistanceSq(target) < MathUtil.square(this.range.getValue().floatValue())) && !EntityUtil.canEntityFeetBeSeen(target) && !(Killaura.mc.player.getDistanceSq(target) < MathUtil.square(this.raytrace.getValue().floatValue()))) {
            target = this.getTarget();
        }
        int n = this.delay.getValue() == false ? 0 : (wait = (int)((float)DamageUtil.getCooldownByWeapon((EntityPlayer)Killaura.mc.player) * (this.tps.getValue() != false ? OyVey.serverManager.getTpsFactor() : 1.0f)));
        if (!this.timer.passedMs(wait)) {
            return;
        }
        target = this.getTarget();
        if (target == null) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            OyVey.rotationManager.lookAtEntity(target);
        }
        EntityUtil.attackEntity(target, this.packet.getValue(), true);
        this.timer.reset();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue().floatValue();
        double maxHealth = 36.0;
        for (Entity entity : Killaura.mc.world.loadedEntityList) {
            if (!(this.players.getValue() != false && entity instanceof EntityPlayer || this.animals.getValue() != false && EntityUtil.isPassive(entity) || this.mobs.getValue() != false && EntityUtil.isMobAggressive(entity) || this.vehicles.getValue() != false && EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue().booleanValue() || !EntityUtil.isProjectile(entity)) || entity instanceof EntityLivingBase && EntityUtil.isntValid(entity, distance) || !Killaura.mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && Killaura.mc.player.getDistanceSq(entity) > MathUtil.square(this.raytrace.getValue().floatValue())) continue;
            if (target == null) {
                target = entity;
                distance = Killaura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer)entity, 18)) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < this.health.getValue().floatValue()) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() != TargetMode.HEALTH && Killaura.mc.player.getDistanceSq(entity) < distance) {
                target = entity;
                distance = Killaura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (this.targetMode.getValue() != TargetMode.HEALTH || !((double)EntityUtil.getHealth(entity) < maxHealth)) continue;
            target = entity;
            distance = Killaura.mc.player.getDistanceSq(entity);
            maxHealth = EntityUtil.getHealth(entity);
        }
        return target;
    }

    @Override
    public String getDisplayInfo() {
        if (this.info.getValue().booleanValue() && target instanceof EntityPlayer) {
            return target.getName();
        }
        return null;
    }

    public static enum TargetMode {
        FOCUS,
        CLOSEST,
        HEALTH,
        SMART;

    }

    public static enum Settings {
        MAIN,
        TARGETS;

    }
}

