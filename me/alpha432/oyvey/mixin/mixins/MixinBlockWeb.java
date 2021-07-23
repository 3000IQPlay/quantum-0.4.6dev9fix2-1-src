/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.event.events.BlockCollisionBoundingBoxEvent;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockWeb.class})
public class MixinBlockWeb {
    @Inject(method={"getCollisionBoundingBox"}, at={@At(value="HEAD")}, cancellable=true)
    public void getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> callbackInfoReturnable) {
        BlockCollisionBoundingBoxEvent bb = new BlockCollisionBoundingBoxEvent(pos);
        MinecraftForge.EVENT_BUS.post((Event)bb);
        if (bb.isCanceledE()) {
            callbackInfoReturnable.setReturnValue(bb.getBoundingBox());
            callbackInfoReturnable.cancel();
        }
    }
}

