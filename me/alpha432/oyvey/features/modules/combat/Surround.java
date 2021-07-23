/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.PlayerUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Surround
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.PLACE));
    private final Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Object>("BlocksPerTick", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> noGhost = this.register(new Setting<Object>("PacketPlace", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> floor = this.register(new Setting<Object>("Floor", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> alwaysHelp = this.register(new Setting<Object>("AlwaysHelp", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Boolean> helpingBlocks = this.register(new Setting<Object>("HelpingBlocks", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE));
    private final Setting<Integer> eventMode = this.register(new Setting<Object>("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Center> centerPlayer = this.register(new Setting<Object>("Center", (Object)Center.None, v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> rotate = this.register(new Setting<Object>("Rotate", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Integer> retryer = this.register(new Setting<Object>("Retries", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(15), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER));
    private final Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER));
    private final Setting<Integer> rainbowhue = this.register(new Setting<Integer>("RainbowHue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public final Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    public final Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    public final Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("cRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("cGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("cBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("cAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set<Vec3d> extendingBlocks = new HashSet<Vec3d>();
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private List<BlockPos> placeVectors = new ArrayList<BlockPos>();
    private int isSafe;
    public static boolean isPlacing = false;
    private BlockPos startPos;
    private boolean didPlace = false;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private int obbySlot = -1;
    private boolean offHand = false;
    Vec3d center = Vec3d.ZERO;

    public Surround() {
        super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (Surround.fullNullCheck()) {
            this.disable();
        }
        this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)Surround.mc.player);
        this.center = PlayerUtil.getCenter(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posZ);
        switch (this.centerPlayer.getValue()) {
            case TP: {
                Surround.mc.player.motionX = 0.0;
                Surround.mc.player.motionZ = 0.0;
                Surround.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.center.x, this.center.y, this.center.z, true));
                Surround.mc.player.setPosition(this.center.x, this.center.y, this.center.z);
                break;
            }
            case NCP: {
                Surround.mc.player.motionX = (this.center.x - Surround.mc.player.posX) / 2.0;
                Surround.mc.player.motionZ = (this.center.z - Surround.mc.player.posZ) / 2.0;
            }
        }
        this.retries.clear();
        this.retryTimer.reset();
    }

    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.doFeetPlace();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.doFeetPlace();
        }
    }

    @Override
    public void onUpdate() {
        boolean inEChest;
        if (this.eventMode.getValue() == 1) {
            this.doFeetPlace();
        }
        if (this.check()) {
            return;
        }
        boolean onWeb = Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.WEB;
        if (!BlockUtil.isSafe((Entity)Surround.mc.player, onWeb ? 1 : 0, this.floor.getValue())) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(Surround.mc.player.getPositionVector(), onWeb ? 1 : 0, this.floor.getValue()), this.helpingBlocks.getValue(), false, false);
        } else if (!BlockUtil.isSafe((Entity)Surround.mc.player, onWeb ? 0 : -1, false) && this.alwaysHelp.getValue().booleanValue()) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(Surround.mc.player.getPositionVector(), onWeb ? 0 : -1, false), false, false, true);
        }
        boolean bl = inEChest = Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (Surround.mc.player.posY - (double)((int)Surround.mc.player.posY) < 0.7) {
            inEChest = false;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
        if (this.isSafe == 2) {
            this.placeVectors = new ArrayList<BlockPos>();
        }
    }

    @Override
    public void onDisable() {
        if (Surround.nullCheck()) {
            return;
        }
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getValue().booleanValue() && (this.isSafe == 0 || this.isSafe == 1)) {
            this.placeVectors = this.rushehacj();
            for (BlockPos pos : this.placeVectors) {
                if (!(Surround.mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) continue;
                RenderUtil.drawBoxESP(pos, this.rainbow.getValue() != false ? ColorUtil.rainbow(this.rainbowhue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        switch (this.isSafe) {
            case 0: {
                return ChatFormatting.RED + "Unsafe";
            }
            case 1: {
                return ChatFormatting.YELLOW + "Safe";
            }
        }
        return ChatFormatting.GREEN + "Safe";
    }

    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        if (!EntityUtil.isSafeOy((Entity)Surround.mc.player, 0, true)) {
            this.isSafe = 0;
            this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray2((Entity)Surround.mc.player, 0, true), true, false, false);
        } else if (!EntityUtil.isSafeOy((Entity)Surround.mc.player, -1, false)) {
            this.isSafe = 1;
            this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray2((Entity)Surround.mc.player, -1, false), false, false, true);
        } else {
            this.isSafe = 2;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
            while (iterator.hasNext()) {
                Vec3d vec3d;
                array[i] = vec3d = iterator.next();
                ++i;
            }
            int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), BlockUtil.getUnsafeBlockArray(this.areClose(array), 0, this.floor.getValue()), this.helpingBlocks.getValue(), false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
            this.extendingBlocks.clear();
        }
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : BlockUtil.getUnsafeBlockArray(Surround.mc.player.getPositionVector(), 0, this.floor.getValue())) {
                if (!vec3d.equals((Object)pos)) continue;
                ++matches;
            }
        }
        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        boolean gotHelp = true;
        block5: for (Vec3d vec3d : vec3ds) {
            gotHelp = true;
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, false)) {
                case 1: {
                    if (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue()) {
                        this.placeBlock(position);
                        this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
                        this.retryTimer.reset();
                        continue block5;
                    }
                    if (OyVey.speedManager.getSpeedKpH() != 0.0 || isExtending || this.extenders >= 1) continue block5;
                    this.placeBlocks(Surround.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d2(Surround.mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    continue block5;
                }
                case 2: {
                    if (!hasHelpingBlocks) continue block5;
                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (!isHelping) continue block5;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check() {
        if (Surround.nullCheck()) {
            return true;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
        }
        this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        isPlacing = false;
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.isOff()) {
            return true;
        }
        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (Surround.mc.player.inventory.currentItem != this.lastHotbarSlot && Surround.mc.player.inventory.currentItem != this.obbySlot && Surround.mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        }
        if (!this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)Surround.mc.player))) {
            this.disable();
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue()) {
            int originalSlot = Surround.mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }
            isPlacing = true;
            Surround.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            Surround.mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking);
            Surround.mc.player.inventory.currentItem = originalSlot;
            Surround.mc.playerController.updateController();
            this.didPlace = true;
            ++this.placements;
        }
    }

    private List<BlockPos> rushehacj() {
        if (this.floor.getValue().booleanValue()) {
            return Arrays.asList(new BlockPos(Surround.mc.player.getPositionVector()).add(0, -1, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(-1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, -1), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, 1));
        }
        return Arrays.asList(new BlockPos(Surround.mc.player.getPositionVector()).add(1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(-1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, -1), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, 1));
    }

    public static enum Center {
        TP,
        NCP,
        None;

    }

    public static enum Settings {
        PLACE,
        MISC,
        RENDER;

    }
}

