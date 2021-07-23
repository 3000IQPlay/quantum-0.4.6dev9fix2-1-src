/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.combat.AutoCrystal;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.mixin.mixins.accessors.AccessorCPacketUseEntity;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.ItemUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CrystalAura
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.Place));
    private final Setting<Integer> placeDelay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> this.setting.getValue() == Settings.Place));
    private final Setting<Float> placeRange = this.register(new Setting<Object>("Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.setting.getValue() == Settings.Place));
    private final Setting<Integer> breakDelay = this.register(new Setting<Object>("BDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> this.setting.getValue() == Settings.Break));
    public Setting<Float> breakRange = this.register(new Setting<Object>("BRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.setting.getValue() == Settings.Break));
    private final Setting<Boolean> cancelcrystal = this.register(new Setting<Object>("SetDead", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Break));
    private final Setting<Boolean> antiWeakness = this.register(new Setting<Object>("AntiWeakness", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Break));
    private final Setting<Boolean> antiWeaknessSilent = this.register(new Setting<Object>("SilentWeakness", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Break && this.antiWeakness.getValue() != false));
    private final Setting<Boolean> switchBack = this.register(new Setting<Object>("SwitchBack", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Break && this.antiWeakness.getValue() != false));
    private final Setting<InfoMode> infomode = this.register(new Setting<Object>("Info", (Object)InfoMode.Target, v -> this.setting.getValue() == Settings.Render));
    private final Setting<Boolean> offhandS = this.register(new Setting<Object>("OffhandSwing", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Render));
    public Setting<Boolean> text = this.register(new Setting<Object>("DamageText", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Render));
    public Setting<RenderMode> render = this.register(new Setting<Object>("RenderMode", (Object)RenderMode.Box, v -> this.setting.getValue() == Settings.Render));
    public Setting<Boolean> colorSync = this.register(new Setting<Object>("Rainbow", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    public Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    private final Setting<Integer> red = this.register(new Setting<Object>("BoxRed", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    private final Setting<Integer> green = this.register(new Setting<Object>("BoxGreen", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    private final Setting<Integer> blue = this.register(new Setting<Object>("BoxBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    private final Setting<Integer> alpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.box.getValue() != false));
    public Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("OutlineWidth", Float.valueOf(0.1f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.outline.getValue() != false));
    public Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.outline.getValue() != false));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("CustomRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("CustomGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("CustomBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("CustomAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && this.customOutline.getValue() != false && this.outline.getValue() != false));
    private final Setting<Integer> colorRed = this.register(new Setting<Object>("TopRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> colorGreen = this.register(new Setting<Object>("TopGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> colorBlue = this.register(new Setting<Object>("TopBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> topAlpha = this.register(new Setting<Object>("TopAlpha", Integer.valueOf(6), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> colorRedDown = this.register(new Setting<Object>("DownRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> colorGreenDown = this.register(new Setting<Object>("DownGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> colorBlueDown = this.register(new Setting<Object>("DownBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> downAlpha = this.register(new Setting<Object>("DownAlpha", Integer.valueOf(46), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> outlineAlpha = this.register(new Setting<Object>("LAlpha", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Float> outlineWidth = this.register(new Setting<Object>("LWidth", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient));
    private final Setting<Integer> clawRed = this.register(new Setting<Object>("ClawRed", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw));
    private final Setting<Integer> clawGreen = this.register(new Setting<Object>("ClawGreen", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw));
    private final Setting<Integer> clawBlue = this.register(new Setting<Object>("ClawBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw));
    private final Setting<Integer> clawAlpha = this.register(new Setting<Object>("ClawAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw));
    private final Setting<Float> range = this.register(new Setting<Object>("TargetRange", Float.valueOf(9.5f), Float.valueOf(0.0f), Float.valueOf(16.0f), v -> this.setting.getValue() == Settings.Misc));
    public Setting<Rotate> rotate = this.register(new Setting<Object>("Rotate", (Object)Rotate.OFF, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Integer> rotations = this.register(new Setting<Object>("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.Misc && this.rotate.getValue() != Rotate.OFF));
    public Setting<Boolean> rotateFirst = this.register(new Setting<Object>("FirstRotation", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Misc && this.rotate.getValue() != Rotate.OFF));
    public Setting<Raytrace> raytrace = this.register(new Setting<Object>("Raytrace", (Object)Raytrace.None, v -> this.setting.getValue() == Settings.Misc));
    public Setting<Float> placetrace = this.register(new Setting<Object>("Placetrace", Float.valueOf(5.5f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.Misc && this.raytrace.getValue() != Raytrace.None && this.raytrace.getValue() != Raytrace.Break));
    public Setting<Float> breaktrace = this.register(new Setting<Object>("Breaktrace", Float.valueOf(5.5f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.Misc && this.raytrace.getValue() != Raytrace.None && this.raytrace.getValue() != Raytrace.Place));
    private final Setting<Float> breakWallRange = this.register(new Setting<Object>("WallRange", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Float> minDamage = this.register(new Setting<Object>("MinDamage", Float.valueOf(0.7f), Float.valueOf(0.0f), Float.valueOf(30.0f), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Float> maxSelf = this.register(new Setting<Object>("MaxSelf", Float.valueOf(18.5f), Float.valueOf(0.0f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Float> lethalMult = this.register(new Setting<Object>("LethalMult", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Float> armorScale = this.register(new Setting<Object>("ArmorBreak", Float.valueOf(100.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Boolean> second = this.register(new Setting<Object>("Second", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Boolean> autoSwitch = this.register(new Setting<Object>("AutoSwitch", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Misc));
    private final Map<Integer, Integer> attackMap = new HashMap<Integer, Integer>();
    private final List<BlockPos> placedList = new ArrayList<BlockPos>();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer renderTimer = new Timer();
    private int rotationPacketsSpoofed = 0;
    public static EntityPlayer currentTarget;
    private BlockPos renderPos = null;
    private double renderDamage = 0.0;
    private BlockPos placePos = null;
    private boolean offHand = false;
    public boolean rotating = false;
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private boolean offhand;

    public CrystalAura() {
        super("CrystalAura", "ca", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onToggle() {
        this.placedList.clear();
        this.breakTimer.reset();
        this.placeTimer.reset();
        this.renderTimer.reset();
        currentTarget = null;
        this.attackMap.clear();
        this.renderPos = null;
        this.offhand = false;
        this.rotating = false;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.isNull()) {
            return;
        }
        if (this.renderTimer.passedMs(500L)) {
            this.placedList.clear();
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.offhand = ((ItemStack)CrystalAura.mc.player.inventory.offHandInventory.get(0)).getItem() == Items.END_CRYSTAL;
        currentTarget = EntityUtil.getClosestPlayer(this.range.getValue().floatValue());
        if (currentTarget == null) {
            return;
        }
        this.doPlace();
        if (event.phase == TickEvent.Phase.START) {
            this.doBreak();
        }
    }

    private void doBreak() {
        Entity maxCrystal = null;
        Entity crystal = null;
        double maxDamage = 0.5;
        int size = CrystalAura.mc.world.loadedEntityList.size();
        for (int i = 0; i < size; ++i) {
            float selfDamage;
            float targetDamage;
            Entity entity = (Entity)CrystalAura.mc.world.loadedEntityList.get(i);
            if (!(entity instanceof EntityEnderCrystal) || !(CrystalAura.mc.player.getDistance(entity) < (CrystalAura.mc.player.canEntityBeSeen(entity) ? this.breakRange.getValue() : this.breakWallRange.getValue()).floatValue()) || !((targetDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase)currentTarget)) > this.minDamage.getValue().floatValue()) && !(targetDamage * this.lethalMult.getValue().floatValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount()) && !ItemUtil.isArmorUnderPercent(currentTarget, this.armorScale.getValue().floatValue()) || (selfDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase)CrystalAura.mc.player)) > this.maxSelf.getValue().floatValue() || selfDamage + 2.0f > CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount() || selfDamage >= targetDamage || maxDamage > (double)targetDamage) continue;
            maxDamage = targetDamage;
            maxCrystal = crystal = entity;
        }
        if (crystal != null && this.breakTimer.passedMs(this.breakDelay.getValue().intValue())) {
            mc.getConnection().sendPacket((Packet)new CPacketUseEntity(crystal));
            CrystalAura.mc.player.swingArm(this.offhandS.getValue() != false ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            this.breakTimer.reset();
        }
        if (maxCrystal != null && this.breakTimer.hasReached(this.breakDelay.getValue().intValue())) {
            int lastSlot = -1;
            if (this.antiWeakness.getValue().booleanValue() && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                boolean swtch = !CrystalAura.mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(CrystalAura.mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() != 2;
                int swordSlot = ItemUtil.getItemSlot(Items.DIAMOND_SWORD);
                if (swtch && swordSlot != -1) {
                    lastSlot = CrystalAura.mc.player.inventory.currentItem;
                    if (this.antiWeaknessSilent.getValue().booleanValue()) {
                        mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(swordSlot));
                    } else {
                        CrystalAura.mc.player.inventory.currentItem = swordSlot;
                    }
                }
            }
            mc.getConnection().sendPacket((Packet)new CPacketUseEntity(maxCrystal));
            this.attackMap.put(maxCrystal.getEntityId(), this.attackMap.containsKey(maxCrystal.getEntityId()) ? this.attackMap.get(maxCrystal.getEntityId()) + 1 : 1);
            CrystalAura.mc.player.swingArm(EnumHand.OFF_HAND);
            if (lastSlot != -1 && this.switchBack.getValue().booleanValue()) {
                if (this.antiWeaknessSilent.getValue().booleanValue()) {
                    mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
                } else {
                    CrystalAura.mc.player.inventory.currentItem = lastSlot;
                }
            }
            this.breakTimer.reset();
        }
    }

    private void doPlace() {
        BlockPos placePos = null;
        double maxDamage = 0.5;
        List<BlockPos> sphere = BlockUtil.getSphereRealth(this.placeRange.getValue().floatValue(), true);
        int size = sphere.size();
        for (int i = 0; i < size; ++i) {
            float selfDamage;
            float targetDamage;
            BlockPos pos = sphere.get(i);
            if (!BlockUtil.canPlaceCrystalRealth(pos, this.second.getValue()) || !((targetDamage = EntityUtil.calculate((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, (EntityLivingBase)currentTarget)) > this.minDamage.getValue().floatValue()) && !(targetDamage * this.lethalMult.getValue().floatValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount()) && !ItemUtil.isArmorUnderPercent(currentTarget, this.armorScale.getValue().floatValue()) || (selfDamage = EntityUtil.calculate((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, (EntityLivingBase)CrystalAura.mc.player)) > this.maxSelf.getValue().floatValue() || selfDamage + 2.0f > CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount() || selfDamage >= targetDamage || maxDamage > (double)targetDamage) continue;
            maxDamage = targetDamage;
            placePos = pos;
            this.renderPos = pos;
            this.renderDamage = targetDamage;
        }
        boolean flag = false;
        if (!this.offhand && CrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.END_CRYSTAL) {
            flag = true;
            if (!this.autoSwitch.getValue().booleanValue() || CrystalAura.mc.player.inventory.getCurrentItem().getItem() == Items.GOLDEN_APPLE && CrystalAura.mc.player.isHandActive()) {
                return;
            }
        }
        if (placePos != null) {
            if (this.placeTimer.passedMs(this.placeDelay.getValue().intValue())) {
                if (flag) {
                    int slot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
                    if (slot == -1) {
                        return;
                    }
                    CrystalAura.mc.player.inventory.currentItem = slot;
                }
                this.placedList.add(placePos);
                mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                this.placeTimer.reset();
            }
            this.renderPos = placePos;
        }
        for (BlockPos pos : BlockUtil.possiblePlacePositionsCa(this.placeRange.getValue().floatValue())) {
            if (BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == Raytrace.Place || this.raytrace.getValue() == Raytrace.Both) && AutoCrystal.mc.player.getDistanceSq(pos) > MathUtil.square(this.placetrace.getValue().floatValue()), 1.0f)) continue;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSpawnObject packet;
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject)event.getPacket()).getType() == 51 && this.placedList.contains(new BlockPos(packet.getX(), packet.getY() - 1.0, packet.getZ()))) {
            AccessorCPacketUseEntity use = (AccessorCPacketUseEntity)new CPacketUseEntity();
            use.setEntityId(packet.getEntityID());
            use.setAction(CPacketUseEntity.Action.ATTACK);
            mc.getConnection().sendPacket((Packet)use);
            CrystalAura.mc.player.swingArm(this.offhandS.getValue() != false ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            this.breakTimer.reset();
            return;
        }
        if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            new ArrayList<Entity>(CrystalAura.mc.world.loadedEntityList).forEach(arg_0 -> CrystalAura.lambda$onPacketReceive$54((SPacketSoundEffect)packet, arg_0));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        EntityEnderCrystal crystal;
        CPacketUseEntity packet;
        if (this.rotate.getValue() != Rotate.OFF && this.rotating && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
            packet2.yaw = this.yaw;
            packet2.pitch = this.pitch;
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
        BlockPos pos = null;
        if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            pos = packet.getEntityFromWorld((World)AutoCrystal.mc.world).getPosition();
        }
        if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal && EntityUtil.isCrystalAtFeet(crystal = (EntityEnderCrystal)packet.getEntityFromWorld((World)AutoCrystal.mc.world), this.range.getValue().floatValue()) && pos != null) {
            this.rotateToPos(pos);
            BlockUtil.placeCrystalOnBlock2(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, false);
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal && this.cancelcrystal.getValue().booleanValue()) {
            Objects.requireNonNull(packet.getEntityFromWorld((World)AutoCrystal.mc.world)).setDead();
            AutoCrystal.mc.world.removeEntityFromWorld(packet.entityId);
        }
    }

    private void rotateToPos(BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case Break: {
                break;
            }
            case Place: 
            case All: {
                float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() - 0.5f), (double)((float)pos.getZ() + 0.5f)));
                if (this.rotate.getValue() != Rotate.OFF) {
                    OyVey.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderPos != null && this.render.getValue() != RenderMode.None && (this.box.getValue().booleanValue() || this.text.getValue().booleanValue() || this.outline.getValue().booleanValue())) {
            if (this.render.getValue() == RenderMode.Gradient) {
                RenderUtil.drawGradientFilledBoxVulcan(this.renderPos, new Color(this.colorRedDown.getValue(), this.colorGreenDown.getValue(), this.colorBlueDown.getValue(), this.downAlpha.getValue()), new Color(this.colorRed.getValue(), this.colorGreen.getValue(), this.colorBlue.getValue(), this.topAlpha.getValue()));
                RenderUtil.prepare(7);
                RenderUtil.drawBoundingBoxBottom2(this.renderPos, this.outlineWidth.getValue().floatValue(), this.colorRedDown.getValue(), this.colorGreenDown.getValue(), this.colorBlueDown.getValue(), this.outlineAlpha.getValue());
                RenderUtil.release();
                if (this.text.getValue().booleanValue()) {
                    RenderUtil.drawText(this.renderPos, (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
                }
            }
            if (this.render.getValue() == RenderMode.Box) {
                RenderUtil.drawBoxESP(this.renderPos, this.colorSync.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), this.colorSync.getValue() != false ? this.getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                if (this.text.getValue().booleanValue()) {
                    RenderUtil.drawText(this.renderPos, (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
                }
            }
            if (this.render.getValue() == RenderMode.Claw) {
                RenderUtil.drawBoxBlockPos(this.renderPos, 0.0, 0.0, 0.0, new Color(this.clawRed.getValue(), this.clawGreen.getValue(), this.clawBlue.getValue()), this.clawAlpha.getValue(), RenderMode.Claw);
                if (this.text.getValue().booleanValue()) {
                    RenderUtil.drawText(this.renderPos, (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
                }
            }
        }
    }

    public Color getCurrentColor() {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    @Override
    public String getDisplayInfo() {
        if (currentTarget != null) {
            if (this.infomode.getValue() == InfoMode.Target) {
                return currentTarget.getName();
            }
            if (this.infomode.getValue() == InfoMode.Damage) {
                return (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "";
            }
            if (this.infomode.getValue() == InfoMode.Both) {
                return currentTarget.getName() + ", " + (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "";
            }
        }
        return null;
    }

    private static /* synthetic */ void lambda$onPacketReceive$54(SPacketSoundEffect packet, Entity e) {
        if (e instanceof EntityEnderCrystal && e.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36.0) {
            e.setDead();
        }
    }

    public static enum RenderMode {
        Box,
        Gradient,
        Flat,
        Claw,
        None;

    }

    public static enum Raytrace {
        None,
        Place,
        Break,
        Both;

    }

    public static enum Rotate {
        OFF,
        Place,
        Break,
        All;

    }

    public static enum InfoMode {
        Target,
        Damage,
        Both;

    }

    public static enum Settings {
        Place,
        Break,
        Render,
        Misc;

    }
}

