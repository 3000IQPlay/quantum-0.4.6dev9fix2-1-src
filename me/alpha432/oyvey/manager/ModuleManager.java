/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.Chat;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.client.FontMod;
import me.alpha432.oyvey.features.modules.client.HUD;
import me.alpha432.oyvey.features.modules.client.HudComponents;
import me.alpha432.oyvey.features.modules.client.NickHider;
import me.alpha432.oyvey.features.modules.client.Notifiers;
import me.alpha432.oyvey.features.modules.combat.Anchor;
import me.alpha432.oyvey.features.modules.combat.AntiRegear;
import me.alpha432.oyvey.features.modules.combat.AutoArmor;
import me.alpha432.oyvey.features.modules.combat.AutoCrystal;
import me.alpha432.oyvey.features.modules.combat.AutoTrap;
import me.alpha432.oyvey.features.modules.combat.AutoWeb;
import me.alpha432.oyvey.features.modules.combat.BedAura;
import me.alpha432.oyvey.features.modules.combat.BlockLag;
import me.alpha432.oyvey.features.modules.combat.BowSpam;
import me.alpha432.oyvey.features.modules.combat.Criticals;
import me.alpha432.oyvey.features.modules.combat.CrystalAura;
import me.alpha432.oyvey.features.modules.combat.FeetXP;
import me.alpha432.oyvey.features.modules.combat.Flatten;
import me.alpha432.oyvey.features.modules.combat.GodModule;
import me.alpha432.oyvey.features.modules.combat.HoleFiller;
import me.alpha432.oyvey.features.modules.combat.InstantSelfFill;
import me.alpha432.oyvey.features.modules.combat.Killaura;
import me.alpha432.oyvey.features.modules.combat.Offhand;
import me.alpha432.oyvey.features.modules.combat.OffhandPlus;
import me.alpha432.oyvey.features.modules.combat.SelfWeb;
import me.alpha432.oyvey.features.modules.combat.Selftrap;
import me.alpha432.oyvey.features.modules.combat.Surround;
import me.alpha432.oyvey.features.modules.misc.AntiDesync;
import me.alpha432.oyvey.features.modules.misc.AntiPackets;
import me.alpha432.oyvey.features.modules.misc.Auto;
import me.alpha432.oyvey.features.modules.misc.AutoFish;
import me.alpha432.oyvey.features.modules.misc.BetterPortals;
import me.alpha432.oyvey.features.modules.misc.BookCrash;
import me.alpha432.oyvey.features.modules.misc.BuildHeight;
import me.alpha432.oyvey.features.modules.misc.ChatLogger;
import me.alpha432.oyvey.features.modules.misc.DupeCanceller;
import me.alpha432.oyvey.features.modules.misc.ExtraTab;
import me.alpha432.oyvey.features.modules.misc.LogoutSpots;
import me.alpha432.oyvey.features.modules.misc.MCF;
import me.alpha432.oyvey.features.modules.misc.MobOwner;
import me.alpha432.oyvey.features.modules.misc.MountBypass;
import me.alpha432.oyvey.features.modules.misc.NewChunks;
import me.alpha432.oyvey.features.modules.misc.NoHandShake;
import me.alpha432.oyvey.features.modules.misc.NoHitBox;
import me.alpha432.oyvey.features.modules.misc.OldFagDupe;
import me.alpha432.oyvey.features.modules.misc.RPC;
import me.alpha432.oyvey.features.modules.misc.RenameBypass;
import me.alpha432.oyvey.features.modules.misc.StashLogger;
import me.alpha432.oyvey.features.modules.misc.ToolTips;
import me.alpha432.oyvey.features.modules.misc.Tracker;
import me.alpha432.oyvey.features.modules.movement.AntiLevitate;
import me.alpha432.oyvey.features.modules.movement.AntiWeb;
import me.alpha432.oyvey.features.modules.movement.AutoWalk;
import me.alpha432.oyvey.features.modules.movement.BackTP;
import me.alpha432.oyvey.features.modules.movement.BoatFly;
import me.alpha432.oyvey.features.modules.movement.ElytraFlight;
import me.alpha432.oyvey.features.modules.movement.EntityControl;
import me.alpha432.oyvey.features.modules.movement.IceSpeed;
import me.alpha432.oyvey.features.modules.movement.Jesus;
import me.alpha432.oyvey.features.modules.movement.LongJump;
import me.alpha432.oyvey.features.modules.movement.NoFallOldfag;
import me.alpha432.oyvey.features.modules.movement.NoSlow;
import me.alpha432.oyvey.features.modules.movement.Phase;
import me.alpha432.oyvey.features.modules.movement.ReverseStep;
import me.alpha432.oyvey.features.modules.movement.Scaffold;
import me.alpha432.oyvey.features.modules.movement.Speed;
import me.alpha432.oyvey.features.modules.movement.Sprint;
import me.alpha432.oyvey.features.modules.movement.Static;
import me.alpha432.oyvey.features.modules.movement.Step;
import me.alpha432.oyvey.features.modules.movement.StepTwo;
import me.alpha432.oyvey.features.modules.movement.Strafe;
import me.alpha432.oyvey.features.modules.movement.VanillaSpeed;
import me.alpha432.oyvey.features.modules.movement.Velocity;
import me.alpha432.oyvey.features.modules.movement.WebTP;
import me.alpha432.oyvey.features.modules.movement.YPort;
import me.alpha432.oyvey.features.modules.player.AntiKick;
import me.alpha432.oyvey.features.modules.player.EntityDesync;
import me.alpha432.oyvey.features.modules.player.FakePlayer;
import me.alpha432.oyvey.features.modules.player.FastPlace;
import me.alpha432.oyvey.features.modules.player.Freecam;
import me.alpha432.oyvey.features.modules.player.Godmode;
import me.alpha432.oyvey.features.modules.player.HasteHack;
import me.alpha432.oyvey.features.modules.player.LiquidInteract;
import me.alpha432.oyvey.features.modules.player.MCP;
import me.alpha432.oyvey.features.modules.player.MultiTask;
import me.alpha432.oyvey.features.modules.player.PacketEat;
import me.alpha432.oyvey.features.modules.player.PosDesync;
import me.alpha432.oyvey.features.modules.player.Replenish;
import me.alpha432.oyvey.features.modules.player.Speedmine;
import me.alpha432.oyvey.features.modules.player.TimerSpeed;
import me.alpha432.oyvey.features.modules.player.TpsSync;
import me.alpha432.oyvey.features.modules.player.XCarry;
import me.alpha432.oyvey.features.modules.player.Yaw;
import me.alpha432.oyvey.features.modules.render.Animations;
import me.alpha432.oyvey.features.modules.render.ArrowESP;
import me.alpha432.oyvey.features.modules.render.Aspect;
import me.alpha432.oyvey.features.modules.render.BlockHighlight;
import me.alpha432.oyvey.features.modules.render.CameraClip;
import me.alpha432.oyvey.features.modules.render.ChorusESP;
import me.alpha432.oyvey.features.modules.render.Crosshair;
import me.alpha432.oyvey.features.modules.render.CrystalScale;
import me.alpha432.oyvey.features.modules.render.ESP;
import me.alpha432.oyvey.features.modules.render.FullBright;
import me.alpha432.oyvey.features.modules.render.GlintModify;
import me.alpha432.oyvey.features.modules.render.HandChams;
import me.alpha432.oyvey.features.modules.render.HoleESP;
import me.alpha432.oyvey.features.modules.render.NameTags;
import me.alpha432.oyvey.features.modules.render.NoRender;
import me.alpha432.oyvey.features.modules.render.Shaders;
import me.alpha432.oyvey.features.modules.render.SkyColor;
import me.alpha432.oyvey.features.modules.render.StorageESP;
import me.alpha432.oyvey.features.modules.render.TexturedChams;
import me.alpha432.oyvey.features.modules.render.Trails;
import me.alpha432.oyvey.features.modules.render.Trajectories;
import me.alpha432.oyvey.features.modules.render.ViewModChanger;
import me.alpha432.oyvey.features.modules.render.Wireframe;
import me.alpha432.oyvey.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

public class ModuleManager
extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<String> sortedModulesABC = new ArrayList<String>();
    public Animation animationThread;

    public void init() {
        this.modules.add(new Chat());
        this.modules.add(new ClickGui());
        this.modules.add(new FontMod());
        this.modules.add(new HUD());
        this.modules.add(new HudComponents());
        this.modules.add(new NickHider());
        this.modules.add(new Notifiers());
        this.modules.add(new Anchor());
        this.modules.add(new AntiRegear());
        this.modules.add(new AutoArmor());
        this.modules.add(new AutoCrystal());
        this.modules.add(new AutoTrap());
        this.modules.add(new AutoWeb());
        this.modules.add(new BedAura());
        this.modules.add(new BlockLag());
        this.modules.add(new BowSpam());
        this.modules.add(new Criticals());
        this.modules.add(new CrystalAura());
        this.modules.add(new FeetXP());
        this.modules.add(new Flatten());
        this.modules.add(new GodModule());
        this.modules.add(new InstantSelfFill());
        this.modules.add(new HoleFiller());
        this.modules.add(new Killaura());
        this.modules.add(new Offhand());
        this.modules.add(new OffhandPlus());
        this.modules.add(new Selftrap());
        this.modules.add(new SelfWeb());
        this.modules.add(new Surround());
        this.modules.add(new AntiDesync());
        this.modules.add(new AntiPackets());
        this.modules.add(new Auto());
        this.modules.add(new DupeCanceller());
        this.modules.add(new AutoFish());
        this.modules.add(new BetterPortals());
        this.modules.add(new BookCrash());
        this.modules.add(new BuildHeight());
        this.modules.add(new ChatLogger());
        this.modules.add(new ExtraTab());
        this.modules.add(new LogoutSpots());
        this.modules.add(new MCF());
        this.modules.add(new MobOwner());
        this.modules.add(new MountBypass());
        this.modules.add(new NewChunks());
        this.modules.add(new NoHandShake());
        this.modules.add(new NoHitBox());
        this.modules.add(new OldFagDupe());
        this.modules.add(new RenameBypass());
        this.modules.add(new RPC());
        this.modules.add(new StashLogger());
        this.modules.add(new ToolTips());
        this.modules.add(new Tracker());
        this.modules.add(new AntiLevitate());
        this.modules.add(new AntiWeb());
        this.modules.add(new AutoWalk());
        this.modules.add(new BackTP());
        this.modules.add(new BoatFly());
        this.modules.add(new ElytraFlight());
        this.modules.add(new EntityControl());
        this.modules.add(new IceSpeed());
        this.modules.add(new Jesus());
        this.modules.add(new LongJump());
        this.modules.add(new NoFallOldfag());
        this.modules.add(new NoSlow());
        this.modules.add(new Phase());
        this.modules.add(new ReverseStep());
        this.modules.add(new Scaffold());
        this.modules.add(new Speed());
        this.modules.add(new Sprint());
        this.modules.add(new Static());
        this.modules.add(new Step());
        this.modules.add(new StepTwo());
        this.modules.add(new Strafe());
        this.modules.add(new VanillaSpeed());
        this.modules.add(new Velocity());
        this.modules.add(new WebTP());
        this.modules.add(new YPort());
        this.modules.add(new AntiKick());
        this.modules.add(new EntityDesync());
        this.modules.add(new FakePlayer());
        this.modules.add(new FastPlace());
        this.modules.add(new Freecam());
        this.modules.add(new Godmode());
        this.modules.add(new HasteHack());
        this.modules.add(new LiquidInteract());
        this.modules.add(new MCP());
        this.modules.add(new MultiTask());
        this.modules.add(new PacketEat());
        this.modules.add(new PosDesync());
        this.modules.add(new Replenish());
        this.modules.add(new Speedmine());
        this.modules.add(new TimerSpeed());
        this.modules.add(new TpsSync());
        this.modules.add(new XCarry());
        this.modules.add(new Yaw());
        this.modules.add(new Animations());
        this.modules.add(new ArrowESP());
        this.modules.add(new Aspect());
        this.modules.add(new BlockHighlight());
        this.modules.add(new CameraClip());
        this.modules.add(new ChorusESP());
        this.modules.add(new Crosshair());
        this.modules.add(new CrystalScale());
        this.modules.add(new ESP());
        this.modules.add(new FullBright());
        this.modules.add(new GlintModify());
        this.modules.add(new HandChams());
        this.modules.add(new HoleESP());
        this.modules.add(new NameTags());
        this.modules.add(new NoRender());
        this.modules.add(new Shaders());
        this.modules.add(new SkyColor());
        this.modules.add(new StorageESP());
        this.modules.add(new TexturedChams());
        this.modules.add(new Trails());
        this.modules.add(new Trajectories());
        this.modules.add(new Wireframe());
        this.modules.add(new ViewModChanger());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add((Module)module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(arg_0 -> ((EventBus)MinecraftForge.EVENT_BUS).register(arg_0));
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public <T extends Module> T getModuleT(Class<T> clazz) {
        return (T)((Module)this.modules.stream().filter(module -> module.getClass() == clazz).map(module -> module).findFirst().orElse(null));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList<String>(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(arg_0 -> ((EventBus)MinecraftForge.EVENT_BUS).unregister(arg_0));
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof OyVeyGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    private class Animation
    extends Thread {
        public Module module;
        public float offset;
        public float vOffset;
        ScheduledExecutorService service;

        public Animation() {
            super("Animation");
            this.service = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        public void run() {
            if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
                for (Module module : ModuleManager.this.sortedModules) {
                    String text = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    module.offset = (float)ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
                    module.vOffset = (float)ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
                    if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                        if (!(module.arrayListOffset > module.offset) || Util.mc.world == null) continue;
                        module.arrayListOffset -= module.offset;
                        module.sliding = true;
                        continue;
                    }
                    if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) continue;
                    if (module.arrayListOffset < (float)ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                        module.arrayListOffset += module.offset;
                        module.sliding = true;
                        continue;
                    }
                    module.sliding = false;
                }
            } else {
                for (String e : ModuleManager.this.sortedModulesABC) {
                    Module module = OyVey.moduleManager.getModuleByName(e);
                    String text = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    module.offset = (float)ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
                    module.vOffset = (float)ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
                    if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                        if (!(module.arrayListOffset > module.offset) || Util.mc.world == null) continue;
                        module.arrayListOffset -= module.offset;
                        module.sliding = true;
                        continue;
                    }
                    if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) continue;
                    if (module.arrayListOffset < (float)ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                        module.arrayListOffset += module.offset;
                        module.sliding = true;
                        continue;
                    }
                    module.sliding = false;
                }
            }
        }

        @Override
        public void start() {
            System.out.println("Starting animation thread.");
            this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
        }
    }
}

