/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.ProcessRightClickBlockEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class OffhandPlus
extends Module {
    private static OffhandPlus instance;
    private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
    private final Timer timer = new Timer();
    private final Timer secondTimer = new Timer();
    public Setting<Boolean> crystal = this.register(new Setting<Boolean>("Crystal", true));
    public Setting<Float> crystalHealth = this.register(new Setting<Float>("CrystalHP", Float.valueOf(13.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    public Setting<Float> crystalHoleHealth = this.register(new Setting<Float>("CrystalHoleHP", Float.valueOf(3.5f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    public Setting<Boolean> gapple = this.register(new Setting<Boolean>("SwordGapple", false));
    public Setting<Boolean> armorCheck = this.register(new Setting<Boolean>("ArmorCheck", true));
    public Setting<Integer> actions = this.register(new Setting<Integer>("Packets", 4, 1, 4));
    public Mode2 currentMode = Mode2.TOTEMS;
    public int totems = 0;
    public int crystals = 0;
    public int gapples = 0;
    public int lastTotemSlot = -1;
    public int lastGappleSlot = -1;
    public int lastCrystalSlot = -1;
    public int lastObbySlot = -1;
    public int lastWebSlot = -1;
    public boolean holdingCrystal = false;
    public boolean holdingTotem = false;
    public boolean holdingGapple = false;
    public boolean didSwitchThisTick = false;
    private boolean second = false;
    private boolean switchedForHealthReason = false;

    public OffhandPlus() {
        super("OffhandCC", "o", Module.Category.COMBAT, true, false, false);
        instance = this;
    }

    public static OffhandPlus getInstance() {
        if (instance == null) {
            instance = new OffhandPlus();
        }
        return instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandPlus.mc.objectMouseOver != null && event.pos == OffhandPlus.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            OffhandPlus.mc.player.setActiveHand(EnumHand.OFF_HAND);
            OffhandPlus.mc.playerController.processRightClick((EntityPlayer)OffhandPlus.mc.player, (World)OffhandPlus.mc.world, EnumHand.OFF_HAND);
        }
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (OffhandPlus.mc.player != null && OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandPlus.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown((int)1)) {
                OffhandPlus.mc.player.setActiveHand(EnumHand.OFF_HAND);
                OffhandPlus.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown((int)1);
            }
        } else if (OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandPlus.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            OffhandPlus.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (OffhandPlus.nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!OffhandPlus.fullNullCheck() && OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandPlus.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && OffhandPlus.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            CPacketPlayerTryUseItem packet;
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        OffhandPlus.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        OffhandPlus.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet = (CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystals";
        }
        if (OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totems";
        }
        if (OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapples";
        }
        return null;
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.holdingTotem = OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
        this.holdingGapple = OffhandPlus.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
        this.totems = OffhandPlus.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::func_190916_E).sum();
        if (this.holdingTotem) {
            this.totems += OffhandPlus.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::func_190916_E).sum();
        }
        this.crystals = OffhandPlus.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::func_190916_E).sum();
        if (this.holdingCrystal) {
            this.crystals += OffhandPlus.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::func_190916_E).sum();
        }
        this.gapples = OffhandPlus.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::func_190916_E).sum();
        if (this.holdingGapple) {
            this.gapples += OffhandPlus.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::func_190916_E).sum();
        }
        this.doSwitch();
    }

    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.gapple.getValue().booleanValue() && OffhandPlus.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && OffhandPlus.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Mode2.GAPPLES;
        } else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue().booleanValue() && (EntityUtil.isSafe((Entity)OffhandPlus.mc.player) && EntityUtil.getHealth((Entity)OffhandPlus.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandPlus.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && (!EntityUtil.isSafe((Entity)OffhandPlus.mc.player) && EntityUtil.getHealth((Entity)OffhandPlus.mc.player, true) <= this.crystalHealth.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandPlus.mc.player, true) <= this.crystalHoleHealth.getValue().floatValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && (EntityUtil.isSafe((Entity)OffhandPlus.mc.player) && EntityUtil.getHealth((Entity)OffhandPlus.mc.player, true) > this.crystalHoleHealth.getValue().floatValue() || EntityUtil.getHealth((Entity)OffhandPlus.mc.player, true) > this.crystalHealth.getValue().floatValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue().booleanValue() && (OffhandPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || OffhandPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || OffhandPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || OffhandPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Mode2.TOTEMS);
        }
        if (OffhandPlus.mc.currentScreen instanceof GuiContainer && !(OffhandPlus.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        Item currentOffhandItem = OffhandPlus.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems <= 0 || this.holdingTotem) break;
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                break;
            }
            case GAPPLES: {
                if (this.gapples <= 0 || this.holdingGapple) break;
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                break;
            }
            default: {
                if (this.crystals <= 0 || this.holdingCrystal) break;
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            InventoryUtil.Task task = this.taskList.poll();
            if (task == null) continue;
            task.run();
            if (!task.isSwitching()) continue;
            this.didSwitchThisTick = true;
        }
    }

    private int getLastSlot(Item item, int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }

    private void putItemInOffhand(int slotIn, int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    public void setMode(Mode2 mode) {
        this.currentMode = this.currentMode == mode ? Mode2.TOTEMS : mode;
    }

    public static enum Mode2 {
        TOTEMS,
        GAPPLES,
        CRYSTALS;

    }
}

