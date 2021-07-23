/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;

public class FastPlace
extends Module {
    private final Setting<Boolean> Block = this.register(new Setting<Boolean>("Blocks", false));
    private final Setting<Boolean> Crystal = this.register(new Setting<Boolean>("Crystals", false));
    private final Setting<Boolean> Firework = this.register(new Setting<Boolean>("Fireworks", false));
    private final Setting<Boolean> SpawnEgg = this.register(new Setting<Boolean>("SpawnEgg", false));
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 3));

    public FastPlace() {
        super("FastUse", "fu", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (FastPlace.fullNullCheck()) {
            return;
        }
        if (FastPlace.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && !this.Block.getValue().booleanValue()) {
            return;
        }
        if (InventoryUtil.holdingItem(ItemEndCrystal.class) && !this.Crystal.getValue().booleanValue()) {
            return;
        }
        if (InventoryUtil.getHeldItem(Items.FIREWORKS) && !this.Firework.getValue().booleanValue()) {
            return;
        }
        if (InventoryUtil.getHeldItem(Items.SPAWN_EGG) && !this.SpawnEgg.getValue().booleanValue()) {
            return;
        }
        FastPlace.mc.rightClickDelayTimer = this.delay.getValue();
    }
}

