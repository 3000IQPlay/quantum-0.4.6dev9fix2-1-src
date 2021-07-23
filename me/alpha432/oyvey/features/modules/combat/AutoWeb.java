/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoWeb
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.PLACE));
    private final Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Integer> blocksPerPlace = this.register(new Setting<Object>("BlocksPerTick", Integer.valueOf(30), Integer.valueOf(1), Integer.valueOf(30), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> packet = this.register(new Setting<Object>("PacketPlace", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> antiSelf = this.register(new Setting<Object>("AntiSelf", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> lowerbody = this.register(new Setting<Object>("Feet", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> upperBody = this.register(new Setting<Object>("Face", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> ylower = this.register(new Setting<Object>("Y-1", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<TargetMode> targetMode = this.register(new Setting<Object>("Target", (Object)TargetMode.UNTRAPPED, v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> disable = this.register(new Setting<Object>("AutoDisable", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Double> targetRange = this.register(new Setting<Object>("TargetRange", Double.valueOf(10.0), Double.valueOf(0.0), Double.valueOf(20.0), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Double> range = this.register(new Setting<Object>("PlaceRange", Double.valueOf(6.0), Double.valueOf(0.0), Double.valueOf(6.0), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Integer> eventMode = this.register(new Setting<Object>("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> freecam = this.register(new Setting<Object>("Freecam", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> rotate = this.register(new Setting<Object>("Rotate", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> raytrace = this.register(new Setting<Object>("Raytrace", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> info = this.register(new Setting<Object>("Info", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER));
    public Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(false), v -> this.render.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.setting.getValue() == Settings.RENDER));
    public Setting<Boolean> Rainbow = this.register(new Setting<Object>("CSync", Boolean.valueOf(false), v -> this.render.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.valueOf(false), v -> this.render.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> rainbowhue = this.register(new Setting<Integer>("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() != false && this.render.getValue() != false && this.setting.getValue() == Settings.RENDER && this.rainbow.getValue() != false));
    private final Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue() != false && this.setting.getValue() == Settings.RENDER));
    public Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(false), v -> this.render.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.setting.getValue() == Settings.RENDER));
    public Setting<Boolean> cRainbow = this.register(new Setting<Object>("OL-Rainbow", Boolean.valueOf(false), v -> this.outline.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.setting.getValue() == Settings.RENDER));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    public static boolean isPlacing = false;
    private final Timer timer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private boolean smartRotate = false;
    private BlockPos startPos = null;
    private BlockPos renderPos = null;

    public AutoWeb() {
        super("AutoWeb", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (AutoWeb.fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)AutoWeb.mc.player);
        this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
    }

    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.smartRotate = false;
            this.doTrap();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.smartRotate = this.rotate.getValue() != false && this.blocksPerPlace.getValue() == 1;
            this.doTrap();
        }
    }

    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.smartRotate = false;
            this.doTrap();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.info.getValue().booleanValue() && this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    @Override
    public void onDisable() {
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }

    private void doTrap() {
        if (this.check()) {
            return;
        }
        this.doWebTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void doWebTrap() {
        List<Vec3d> placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }

    private List<Vec3d> getPlacements() {
        ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        Vec3d baseVec = this.target.getPositionVector();
        if (this.ylower.getValue().booleanValue()) {
            list.add(baseVec.add(0.0, -1.0, 0.0));
        }
        if (this.lowerbody.getValue().booleanValue()) {
            list.add(baseVec);
        }
        if (this.upperBody.getValue().booleanValue()) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(AutoWeb.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoWeb.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (Vec3d vec3d3 : list) {
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
            if (placeability != 3 && placeability != 1 || this.antiSelf.getValue().booleanValue() && MathUtil.areVec3dsAligned(AutoWeb.mc.player.getPositionVector(), vec3d3)) continue;
            this.placeBlock(position);
        }
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (this.isOff()) {
            return true;
        }
        if (this.disable.getValue().booleanValue() && !this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)AutoWeb.mc.player))) {
            this.disable();
            return true;
        }
        if (obbySlot == -1) {
            if (this.info.getValue().booleanValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cYou are out of Webs.");
            }
            this.disable();
            return true;
        }
        if (AutoWeb.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoWeb.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(this.targetRange.getValue(), this.targetMode.getValue() == TargetMode.UNTRAPPED);
        return this.target == null || OyVey.moduleManager.isModuleEnabled("Freecam") && this.freecam.getValue() == false || !this.timer.passedMs(this.delay.getValue().intValue());
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : AutoWeb.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, range) || trapped && player.isInWeb || EntityUtil.getRoundedBlockPos((Entity)AutoWeb.mc.player).equals((Object)EntityUtil.getRoundedBlockPos((Entity)player)) && this.antiSelf.getValue().booleanValue() || OyVey.speedManager.getPlayerSpeed(player) > (double)this.blocksPerPlace.getValue().intValue()) continue;
            if (target == null) {
                target = player;
                distance = AutoWeb.mc.player.getDistanceSq((Entity)player);
                continue;
            }
            if (!(AutoWeb.mc.player.getDistanceSq((Entity)player) < distance)) continue;
            target = player;
            distance = AutoWeb.mc.player.getDistanceSq((Entity)player);
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && AutoWeb.mc.player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue())) {
            isPlacing = true;
            int originalSlot = AutoWeb.mc.player.inventory.currentItem;
            int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
            if (webSlot == -1) {
                this.toggle();
            }
            if (this.smartRotate) {
                AutoWeb.mc.player.inventory.currentItem = webSlot == -1 ? webSlot : webSlot;
                AutoWeb.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking);
                AutoWeb.mc.player.inventory.currentItem = originalSlot;
                AutoWeb.mc.playerController.updateController();
            } else {
                AutoWeb.mc.player.inventory.currentItem = webSlot == -1 ? webSlot : webSlot;
                AutoWeb.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
                AutoWeb.mc.player.inventory.currentItem = originalSlot;
                AutoWeb.mc.playerController.updateController();
            }
            this.didPlace = true;
            ++this.placements;
        }
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getValue().booleanValue()) {
            RenderUtil.drawBoxESP(this.renderPos, this.Rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), this.cRainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true);
        }
    }

    public static enum Settings {
        PLACE,
        MISC,
        RENDER;

    }

    public static enum TargetMode {
        CLOSEST,
        UNTRAPPED;

    }
}

