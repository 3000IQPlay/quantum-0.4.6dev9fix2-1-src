/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiRegear
extends Module {
    private final Setting<Float> reach = this.register(new Setting<Float>("Reach", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(6.0f)));
    private final Setting<Integer> retry = this.register(new Setting<Integer>("Retry Delay", 10, 0, 20));
    private final List<BlockPos> retries = new ArrayList<BlockPos>();
    private final List<BlockPos> selfPlaced = new ArrayList<BlockPos>();
    private int ticks;

    public AntiRegear() {
        super("AntiRegear", "ar", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.ticks++ < this.retry.getValue()) {
            this.ticks = 0;
            this.retries.clear();
        }
        List<BlockPos> sphere = BlockUtil.getSphereRealth(this.reach.getValue().floatValue(), true);
        int size = sphere.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);
            if (this.retries.contains(pos) || this.selfPlaced.contains(pos) || !(AntiRegear.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox)) continue;
            AntiRegear.mc.player.swingArm(EnumHand.MAIN_HAND);
            AntiRegear.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            AntiRegear.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
            this.retries.add(pos);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayerTryUseItemOnBlock packet;
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && AntiRegear.mc.player.getHeldItem((packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand()).getItem() instanceof ItemShulkerBox) {
            this.selfPlaced.add(packet.getPos().offset(packet.getDirection()));
        }
    }
}

