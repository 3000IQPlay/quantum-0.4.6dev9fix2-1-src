/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.AutoCrystal;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class BowSpam
extends Module {
    private final Timer timer = new Timer();
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.FAST));
    public Setting<Boolean> bowbomb = this.register(new Setting<Object>("BowBomb", Boolean.valueOf(false), v -> this.mode.getValue() != Mode.BOWBOMB));
    public Setting<Boolean> allowOffhand = this.register(new Setting<Object>("Offhand", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.AUTORELEASE));
    public Setting<Integer> ticks = this.register(new Setting<Object>("Ticks", 3, 0, 20, v -> this.mode.getValue() == Mode.BOWBOMB || this.mode.getValue() == Mode.FAST, "Speed"));
    public Setting<Integer> delay = this.register(new Setting<Object>("Delay", 50, 0, 500, v -> this.mode.getValue() == Mode.AUTORELEASE, "Speed"));
    public Setting<Boolean> tpsSync = this.register(new Setting<Boolean>("TpsSync", true));
    public Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("AutoSwitch", false));
    public Setting<Boolean> onlyWhenSave = this.register(new Setting<Object>("OnlyWhenSave", Boolean.valueOf(true), v -> this.autoSwitch.getValue()));
    public Setting<Target> targetMode = this.register(new Setting<Object>("Target", (Object)Target.LOWEST, v -> this.autoSwitch.getValue()));
    public Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.autoSwitch.getValue(), "Range of the target"));
    public Setting<Float> health = this.register(new Setting<Object>("Lethal", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.autoSwitch.getValue(), "When should it switch?"));
    public Setting<Float> ownHealth = this.register(new Setting<Object>("OwnHealth", Float.valueOf(20.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.autoSwitch.getValue(), "Own Health."));
    private boolean offhand = false;
    private boolean switched = false;
    private int lastHotbarSlot = -1;

    public BowSpam() {
        super("BowSpam", "Spams your bow", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.lastHotbarSlot = BowSpam.mc.player.inventory.currentItem;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.autoSwitch.getValue().booleanValue() && InventoryUtil.findHotbarBlock(ItemBow.class) != -1 && this.ownHealth.getValue().floatValue() <= EntityUtil.getHealth((Entity)BowSpam.mc.player) && (!this.onlyWhenSave.getValue().booleanValue() || EntityUtil.isSafe((Entity)BowSpam.mc.player))) {
            AutoCrystal crystal;
            EntityPlayer target = this.getTarget();
            if (!(target == null || (crystal = OyVey.moduleManager.getModuleByClass(AutoCrystal.class)).isOn() && InventoryUtil.holdingItem(ItemEndCrystal.class))) {
                Vec3d pos = target.getPositionVector();
                double xPos = pos.x;
                double yPos = pos.y;
                double zPos = pos.z;
                if (BowSpam.mc.player.canEntityBeSeen((Entity)target)) {
                    yPos += (double)target.eyeHeight;
                } else if (EntityUtil.canEntityFeetBeSeen((Entity)target)) {
                    yPos += 0.1;
                } else {
                    return;
                }
                if (!(BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)) {
                    this.lastHotbarSlot = BowSpam.mc.player.inventory.currentItem;
                    InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
                    BowSpam.mc.gameSettings.keyBindUseItem.pressed = true;
                    this.switched = true;
                }
                OyVey.rotationManager.lookAtVec3d(xPos, yPos, zPos);
                if (BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
                    this.switched = true;
                }
            }
        } else if (event.getStage() == 0 && this.switched && this.lastHotbarSlot != -1) {
            InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
            BowSpam.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown((int)1);
            this.switched = false;
        } else {
            BowSpam.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown((int)1);
        }
        if (this.mode.getValue() == Mode.FAST && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.isHandActive()) {
            float f3;
            float f = BowSpam.mc.player.getItemInUseMaxCount();
            float f2 = this.ticks.getValue().intValue();
            float f4 = f3 = this.tpsSync.getValue() != false ? OyVey.serverManager.getTpsFactor() : 1.0f;
            if (f >= f2 * f3) {
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                BowSpam.mc.player.stopActiveHand();
            }
        }
    }

    @Override
    public void onUpdate() {
        this.offhand = BowSpam.mc.player.getHeldItemOffhand().getItem() == Items.BOW && this.allowOffhand.getValue() != false;
        switch (this.mode.getValue()) {
            case AUTORELEASE: {
                if (!this.offhand && !(BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) || !this.timer.passedMs((int)((float)this.delay.getValue().intValue() * (this.tpsSync.getValue() != false ? OyVey.serverManager.getTpsFactor() : 1.0f)))) break;
                BowSpam.mc.playerController.onStoppedUsingItem((EntityPlayer)BowSpam.mc.player);
                this.timer.reset();
                break;
            }
            case BOWBOMB: {
                float f3;
                if (!this.offhand && !(BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) || !BowSpam.mc.player.isHandActive()) break;
                float f = BowSpam.mc.player.getItemInUseMaxCount();
                float f2 = this.ticks.getValue().intValue();
                float f4 = f3 = this.tpsSync.getValue() != false ? OyVey.serverManager.getTpsFactor() : 1.0f;
                if (!(f >= f2 * f3)) break;
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 0.0624, BowSpam.mc.player.posZ, BowSpam.mc.player.rotationYaw, BowSpam.mc.player.rotationPitch, false));
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 999.0, BowSpam.mc.player.posZ, BowSpam.mc.player.rotationYaw, BowSpam.mc.player.rotationPitch, true));
                BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                BowSpam.mc.player.stopActiveHand();
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayerDigging packet;
        if (event.getStage() == 0 && this.bowbomb.getValue().booleanValue() && this.mode.getValue() != Mode.BOWBOMB && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.getItemInUseMaxCount() >= 20 && !BowSpam.mc.player.onGround) {
            BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowSpam.mc.player.posX, BowSpam.mc.player.posY - (double)0.1f, BowSpam.mc.player.posZ, false));
            BowSpam.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 10000.0, BowSpam.mc.player.posZ, true));
        }
    }

    private EntityPlayer getTarget() {
        double maxHealth = 36.0;
        EntityPlayer target = null;
        for (EntityPlayer player : BowSpam.mc.world.playerEntities) {
            if (player == null || EntityUtil.isDead((Entity)player) || EntityUtil.getHealth((Entity)player) > this.health.getValue().floatValue() || player.equals((Object)BowSpam.mc.player) || OyVey.friendManager.isFriend(player) || BowSpam.mc.player.getDistanceSq((Entity)player) > MathUtil.square(this.range.getValue().floatValue()) || !BowSpam.mc.player.canEntityBeSeen((Entity)player) && !EntityUtil.canEntityFeetBeSeen((Entity)player)) continue;
            if (target == null) {
                target = player;
                maxHealth = EntityUtil.getHealth((Entity)player);
            }
            if (this.targetMode.getValue() == Target.CLOSEST && BowSpam.mc.player.getDistanceSq((Entity)player) < BowSpam.mc.player.getDistanceSq((Entity)target)) {
                target = player;
                maxHealth = EntityUtil.getHealth((Entity)player);
            }
            if (this.targetMode.getValue() != Target.LOWEST || !((double)EntityUtil.getHealth((Entity)player) < maxHealth)) continue;
            target = player;
            maxHealth = EntityUtil.getHealth((Entity)player);
        }
        return target;
    }

    public static enum Target {
        CLOSEST,
        LOWEST;

    }

    public static enum Mode {
        FAST,
        AUTORELEASE,
        BOWBOMB;

    }
}

