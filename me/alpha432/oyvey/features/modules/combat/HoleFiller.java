/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.TestUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleFiller
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.Place));
    public Setting<PlaceMode> placeMode = this.register(new Setting<Object>("PlaceMode", (Object)PlaceMode.All, v -> this.setting.getValue() == Settings.Place));
    private final Setting<Double> smartRange = this.register(new Setting<Object>("SmartRange", Double.valueOf(6.0), Double.valueOf(0.0), Double.valueOf(10.0), v -> this.placeMode.getValue() == PlaceMode.Smart && this.setting.getValue() == Settings.Place));
    private final Setting<Float> distance = this.register(new Setting<Object>("SuperSmartRange", Float.valueOf(2.0f), Float.valueOf(1.0f), Float.valueOf(7.0f), v -> this.setting.getValue() == Settings.Place && this.placeMode.getValue() == PlaceMode.SuperSmart));
    private final Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> this.setting.getValue() == Settings.Place));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Object>("BlocksPerTick", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.Place));
    private final Setting<Boolean> packet = this.register(new Setting<Object>("PacketPlace", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Place));
    private final Setting<Boolean> autoDisable = this.register(new Setting<Object>("AutoDisable", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Boolean> onlySafe = this.register(new Setting<Object>("OnlySafe", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Double> range = this.register(new Setting<Object>("PlaceRange", Double.valueOf(6.0), Double.valueOf(0.0), Double.valueOf(6.0), v -> this.setting.getValue() == Settings.Misc));
    private final Setting<Boolean> rotate = this.register(new Setting<Object>("Rotate", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.Misc));
    private final Vec3d[] offsetsDefault = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0)};
    private EntityPlayer target;
    private int offsetStep = 0;
    private int oldSlot = -1;
    private boolean placing = false;
    private static final BlockPos[] surroundOffset;
    private static HoleFiller INSTANCE;
    private final Timer offTimer = new Timer();
    private final Timer timer = new Timer();
    private boolean isSneaking;
    private boolean hasOffhand = false;
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private final Timer retryTimer = new Timer();
    private int blocksThisTick = 0;
    private ArrayList<BlockPos> holes = new ArrayList();
    private int trie;

    public HoleFiller() {
        super("HoleFill", "hf", Module.Category.COMBAT, true, false, true);
        this.setInstance();
    }

    public static HoleFiller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleFiller();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (HoleFiller.fullNullCheck()) {
            this.disable();
        }
        this.oldSlot = HoleFiller.mc.player.inventory.currentItem;
        this.offTimer.reset();
        this.trie = 0;
    }

    @Override
    public void onTick() {
        if (this.isOn() && (this.blocksPerTick.getValue() != 1 || !this.rotate.getValue().booleanValue())) {
            this.doHoleFill();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.isOn() && event.getStage() == 0 && this.blocksPerTick.getValue() == 1 && this.rotate.getValue().booleanValue()) {
            this.doHoleFill();
        }
    }

    @Override
    public void onDisable() {
        this.retries.clear();
    }

    private void place(BlockPos pos, int slot, int oldSlot) {
        HoleFiller.mc.player.inventory.currentItem = slot;
        HoleFiller.mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), HoleFiller.mc.player.isSneaking());
        HoleFiller.mc.player.inventory.currentItem = oldSlot;
        HoleFiller.mc.playerController.updateController();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doHoleFill() {
        if (this.check()) {
            return;
        }
        this.holes = new ArrayList();
        Iterable blocks = BlockPos.getAllInBox((BlockPos)HoleFiller.mc.player.getPosition().add(-this.range.getValue().doubleValue(), -this.range.getValue().doubleValue(), -this.range.getValue().doubleValue()), (BlockPos)HoleFiller.mc.player.getPosition().add(this.range.getValue().doubleValue(), this.range.getValue().doubleValue(), this.range.getValue().doubleValue()));
        for (BlockPos pos : blocks) {
            boolean solidNeighbours;
            if (HoleFiller.mc.player.getDistanceSq(pos) > MathUtil.square(this.range.getValue()) || this.placeMode.getValue() == PlaceMode.Smart && !this.isPlayerInRange(pos) || HoleFiller.mc.world.getBlockState(pos).getMaterial().blocksMovement() || HoleFiller.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) continue;
            boolean bl = HoleFiller.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR && HoleFiller.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && HoleFiller.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR ? true : (solidNeighbours = false);
            if (!solidNeighbours) continue;
            this.holes.add(pos);
        }
        List<BlockPos> object = OyVey.holeManager.getHoles();
        List<BlockPos> list = object;
        synchronized (list) {
            ArrayList<BlockPos> targets = new ArrayList<BlockPos>(OyVey.holeManager.getHoles());
        }
        this.holes.forEach(this::placeBlock);
        this.toggle();
    }

    private void placeBlock(BlockPos pos) {
        for (Entity entity : HoleFiller.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityLivingBase)) continue;
            return;
        }
        if (this.blocksThisTick < this.blocksPerTick.getValue()) {
            boolean smartRotate;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }
            boolean bl = smartRotate = this.blocksPerTick.getValue() == 1 && this.rotate.getValue() != false;
            this.isSneaking = smartRotate ? BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking) : BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            int originalSlot = HoleFiller.mc.player.inventory.currentItem;
            HoleFiller.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            HoleFiller.mc.playerController.updateController();
            TestUtil.placeBlock(pos);
            if (HoleFiller.mc.player.inventory.currentItem != originalSlot) {
                HoleFiller.mc.player.inventory.currentItem = originalSlot;
                HoleFiller.mc.playerController.updateController();
            }
            this.timer.reset();
            ++this.blocksThisTick;
        }
    }

    private boolean isPlayerInRange(BlockPos pos) {
        for (EntityPlayer player : HoleFiller.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, this.smartRange.getValue())) continue;
            return true;
        }
        return false;
    }

    private boolean check() {
        this.blocksThisTick = 0;
        if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.onlySafe.getValue().booleanValue() && !EntityUtil.isSafe((Entity)HoleFiller.mc.player)) {
            this.disable();
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }

    static {
        INSTANCE = new HoleFiller();
        surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets2(0, true));
    }

    public static enum Settings {
        Place,
        Misc;

    }

    public static enum PlaceMode {
        Smart,
        SuperSmart,
        All;

    }
}

