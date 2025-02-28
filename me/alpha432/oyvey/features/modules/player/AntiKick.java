/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.NoSuchElementException;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.NettyChannelEvent;
import me.alpha432.oyvey.event.events.PlayerUpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiKick
extends Module {
    private final Setting<Integer> timeout = this.register(new Setting<Integer>("Timeout", 240, 30, 600));
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Change));
    private boolean handlerRemoved = false;
    private Channel nettyChannel = null;
    private Integer timeoutLast = 240;
    private Timer changeThrottle = new Timer();

    public AntiKick() {
        super("AntiKick", "ak", Module.Category.PLAYER, true, false, true);
        this.timeoutLast = this.timeout.getValue();
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name() + (this.mode.getValue() == Mode.Change ? " " + String.valueOf(this.timeoutLast) : "") + " | " + (this.nettyChannel == null ? "NC" : "OK");
    }

    @SubscribeEvent
    public void onNettyChannelSet(NettyChannelEvent event) {
        this.nettyChannel = event.getChannel();
        this.handlerRemoved = false;
        if (this.isEnabled()) {
            this.updateTimeout(this.timeoutLast, this.mode.getValue() == Mode.Change);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (this.isEnabled() && this.changeThrottle.passedMs(1000L) && this.timeout.getValue() != this.timeoutLast && this.nettyChannel != null) {
            this.timeoutLast = this.timeout.getValue();
            this.changeThrottle.reset();
            this.updateTimeout(this.timeoutLast, this.mode.getValue() == Mode.Change);
        }
    }

    @SubscribeEvent
    public void onSettingsUpdate(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this) && event.getSetting().equals(this.mode)) {
            this.timeoutLast = this.timeout.getValue();
            this.updateTimeout(this.timeoutLast, this.mode.getPlannedValue() == Mode.Change);
        }
    }

    private void updateTimeout(int seconds, boolean addBack) {
        if (this.nettyChannel != null) {
            try {
                if (!this.handlerRemoved) {
                    this.nettyChannel.pipeline().remove("timeout");
                }
            }
            catch (NoSuchElementException e) {
                OyVey.LOGGER.info("AntiLagKick: catched NSEE trying to remove timeout");
            }
            if (addBack) {
                this.nettyChannel.pipeline().addFirst("timeout", (ChannelHandler)new ReadTimeoutHandler(seconds));
            }
            this.handlerRemoved = !addBack;
        }
    }

    public static enum Mode {
        Change,
        Remove;

    }
}

