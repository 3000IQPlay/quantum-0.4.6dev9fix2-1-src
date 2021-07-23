/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChorusESP
extends Module {
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private final Timer timer = new Timer();
    private BlockPos chorusPos;

    public ChorusESP() {
        super("ChorusESP", "cesp", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSoundEffect packet;
        if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
            this.chorusPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            this.timer.reset();
        }
    }

    public void onRender3D() {
        if (this.chorusPos != null) {
            if (this.timer.passedMs(2000L)) {
                this.chorusPos = null;
                return;
            }
            RenderUtil.drawBoxRealth(this.chorusPos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), this.alpha.getValue());
        }
    }
}

