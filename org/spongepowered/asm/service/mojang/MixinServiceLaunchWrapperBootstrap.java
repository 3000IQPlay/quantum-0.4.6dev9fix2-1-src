/*
 * Decompiled with CFR 0.151.
 */
package org.spongepowered.asm.service.mojang;

import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class MixinServiceLaunchWrapperBootstrap
implements IMixinServiceBootstrap {
    private static final String SERVICE_PACKAGE = "org.spongepowered.asm.service.";
    private static final String MIXIN_UTIL_PACKAGE = "org.spongepowered.asm.util.";
    private static final String ASM_PACKAGE = "org.spongepowered.asm.lib.";
    private static final String MIXIN_PACKAGE = "org.spongepowered.asm.mixin.";

    @Override
    public String getName() {
        return "LaunchWrapper";
    }

    @Override
    public String getServiceClassName() {
        return "org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper";
    }

    @Override
    public void bootstrap() {
        Launch.classLoader.addClassLoaderExclusion(SERVICE_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(ASM_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(MIXIN_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(MIXIN_UTIL_PACKAGE);
    }
}

