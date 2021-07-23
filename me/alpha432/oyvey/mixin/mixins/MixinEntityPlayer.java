/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PlayerJumpEvent;
import me.alpha432.oyvey.features.modules.misc.BetterPortals;
import me.alpha432.oyvey.features.modules.movement.Phase;
import me.alpha432.oyvey.features.modules.player.TpsSync;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer
extends EntityLivingBase {
    EntityPlayer player;

    public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn);
    }

    @Inject(method={"getCooldownPeriod"}, at={@At(value="HEAD")}, cancellable=true)
    private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (TpsSync.getInstance().isOn() && TpsSync.getInstance().attack.getValue().booleanValue()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf((float)(1.0 / ((EntityPlayer)EntityPlayer.class.cast((Object)this)).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue() * 20.0 * (double)OyVey.serverManager.getTpsFactor())));
        }
    }

    @Inject(method={"jump"}, at={@At(value="HEAD")}, cancellable=true)
    public void onJump(CallbackInfo ci) {
        if (Minecraft.getMinecraft().player.getName() == this.getName()) {
            MinecraftForge.EVENT_BUS.post((Event)new PlayerJumpEvent(this.motionX, this.motionY));
        }
    }

    @ModifyConstant(method={"getPortalCooldown"}, constant={@Constant(intValue=10)})
    private int getPortalCooldownHook(int cooldown) {
        int time = cooldown;
        if (BetterPortals.getInstance().isOn() && BetterPortals.getInstance().fastPortal.getValue().booleanValue()) {
            time = BetterPortals.getInstance().cooldown.getValue();
        }
        return time;
    }

    @Inject(method={"isEntityInsideOpaqueBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable<Boolean> info) {
        if (Phase.getInstance().isOn()) {
            info.setReturnValue(false);
        } else if (Phase.getInstance().mode.getValue() == Phase.Mode.Packetfly && Phase.getInstance().isOn()) {
            info.setReturnValue(false);
        }
    }
}

