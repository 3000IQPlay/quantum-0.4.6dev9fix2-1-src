package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiRegear extends Module {
  private final Setting<Float> reach;
  
  private final Setting<Integer> retry;
  
  private final List<BlockPos> retries;
  
  private final List<BlockPos> selfPlaced;
  
  private int ticks;
  
  public AntiRegear() {
    super("AntiRegear", "ar", Module.Category.COMBAT, true, false, false);
    this.reach = register(new Setting("Reach", Float.valueOf(5.0F), Float.valueOf(1.0F), Float.valueOf(6.0F)));
    this.retry = register(new Setting("Retry Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(20)));
    this.retries = new ArrayList<>();
    this.selfPlaced = new ArrayList<>();
  }
  
  @SubscribeEvent
  public void onUpdate(UpdateEvent event) {
    if (this.ticks++ < ((Integer)this.retry.getValue()).intValue()) {
      this.ticks = 0;
      this.retries.clear();
    } 
    List<BlockPos> sphere = BlockUtil.getSphereRealth(((Float)this.reach.getValue()).floatValue(), true);
    int size = sphere.size();
    for (int i = 0; i < size; i++) {
      BlockPos pos = sphere.get(i);
      if (!this.retries.contains(pos) && !this.selfPlaced.contains(pos))
        if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockShulkerBox) {
          mc.player.swingArm(EnumHand.MAIN_HAND);
          mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
          mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
          this.retries.add(pos);
        }  
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
      CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
      if (mc.player.getHeldItem(packet.getHand()).getItem() instanceof net.minecraft.item.ItemShulkerBox)
        this.selfPlaced.add(packet.getPos().offset(packet.getDirection())); 
    } 
  }
}
