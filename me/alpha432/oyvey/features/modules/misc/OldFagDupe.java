/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.misc;

import java.util.Comparator;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class OldFagDupe
extends Module {
    Entity donkey;

    public OldFagDupe() {
        super("OldFagDupe", "", Module.Category.MISC, true, false, false);
    }

    public boolean setup() {
        return false;
    }

    @Override
    public void onEnable() {
        if (this.findAirInHotbar() == -1) {
            this.disable();
            return;
        }
        if (this.findChestInHotbar() == -1) {
            this.disable();
            return;
        }
        this.donkey = OldFagDupe.mc.world.loadedEntityList.stream().filter(this::isValidEntity).min(Comparator.comparing(p_Entity -> Float.valueOf(OldFagDupe.mc.player.getDistance(p_Entity)))).orElse(null);
        if (this.donkey == null) {
            this.disable();
            return;
        }
    }

    @Override
    public void onUpdate() {
        if (this.findAirInHotbar() == -1) {
            this.disable();
            return;
        }
        if (this.findChestInHotbar() == -1) {
            this.disable();
            return;
        }
        this.donkey = OldFagDupe.mc.world.loadedEntityList.stream().filter(this::isValidEntity).min(Comparator.comparing(p_Entity -> Float.valueOf(OldFagDupe.mc.player.getDistance(p_Entity)))).orElse(null);
        if (this.donkey == null) {
            this.disable();
            return;
        }
        this.putChestOn();
        Command.sendMessage("put chest on the donkey");
        this.toggle();
    }

    public void putChestOn() {
        OldFagDupe.mc.player.inventory.currentItem = this.findAirInHotbar();
        OldFagDupe.mc.player.inventory.currentItem = this.findChestInHotbar();
        OldFagDupe.mc.playerController.interactWithEntity((EntityPlayer)OldFagDupe.mc.player, this.donkey, EnumHand.MAIN_HAND);
    }

    private int findChestInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = OldFagDupe.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || !((block = ((ItemBlock)stack.getItem()).getBlock()) instanceof BlockChest)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    private int findAirInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = OldFagDupe.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != Items.AIR) continue;
            slot = i;
        }
        return slot;
    }

    private boolean isValidEntity(Entity entity) {
        if (entity instanceof AbstractChestHorse) {
            AbstractChestHorse donkey = (AbstractChestHorse)entity;
            return !donkey.isChild() && donkey.isTame();
        }
        return false;
    }
}

