/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Normal));
    private final Setting<Boolean> guiMove = this.register(new Setting<Boolean>("GuiMove", true));
    boolean sneaking;
    private static final KeyBinding[] keys = new KeyBinding[]{NoSlow.mc.gameSettings.keyBindForward, NoSlow.mc.gameSettings.keyBindBack, NoSlow.mc.gameSettings.keyBindLeft, NoSlow.mc.gameSettings.keyBindRight, NoSlow.mc.gameSettings.keyBindJump, NoSlow.mc.gameSettings.keyBindSprint};

    public NoSlow() {
        super("NoSlow", "ns", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (NoSlow.nullCheck()) {
            return;
        }
        if (this.guiMove.getValue().booleanValue()) {
            for (KeyBinding bind : keys) {
                KeyBinding.setKeyBindState((int)bind.getKeyCode(), (boolean)Keyboard.isKeyDown((int)bind.getKeyCode()));
            }
            if (NoSlow.mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown((int)bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState((int)bind.getKeyCode(), (boolean)false);
                }
            }
        }
        if (this.mode.getValue() == Mode.Strict) {
            Item item = NoSlow.mc.player.getActiveItemStack().getItem();
            if (this.sneaking && (!NoSlow.mc.player.isHandActive() && item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion || !(item instanceof ItemFood) || !(item instanceof ItemBow) || !(item instanceof ItemPotion))) {
                NoSlow.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoSlow.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneaking = false;
            }
        }
        if (NoSlow.mc.currentScreen != null && !(NoSlow.mc.currentScreen instanceof GuiChat) && this.guiMove.getValue().booleanValue()) {
            if (NoSlow.mc.currentScreen instanceof OyVeyGui && !this.guiMove.getValue().booleanValue()) {
                return;
            }
            if (Keyboard.isKeyDown((int)200)) {
                NoSlow.mc.player.rotationPitch -= 5.0f;
            }
            if (Keyboard.isKeyDown((int)208)) {
                NoSlow.mc.player.rotationPitch += 5.0f;
            }
            if (Keyboard.isKeyDown((int)205)) {
                NoSlow.mc.player.rotationYaw += 5.0f;
            }
            if (Keyboard.isKeyDown((int)203)) {
                NoSlow.mc.player.rotationYaw -= 5.0f;
            }
            if (NoSlow.mc.player.rotationPitch > 90.0f) {
                NoSlow.mc.player.rotationPitch = 90.0f;
            }
            if (NoSlow.mc.player.rotationPitch < -90.0f) {
                NoSlow.mc.player.rotationPitch = -90.0f;
            }
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent event) {
        if (this.mode.getValue() == Mode.Strict && !this.sneaking) {
            NoSlow.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoSlow.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (this.mode.getValue() == Mode.Normal && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }

    public static enum Mode {
        Normal,
        Strict;

    }
}

