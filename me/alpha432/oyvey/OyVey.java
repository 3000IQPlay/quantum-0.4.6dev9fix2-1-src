/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey;

import me.alpha432.oyvey.DiscordPresence;
import me.alpha432.oyvey.features.modules.misc.RPC;
import me.alpha432.oyvey.manager.ColorManager;
import me.alpha432.oyvey.manager.CommandManager;
import me.alpha432.oyvey.manager.ConfigManager;
import me.alpha432.oyvey.manager.EventManager;
import me.alpha432.oyvey.manager.FileManager;
import me.alpha432.oyvey.manager.FriendManager;
import me.alpha432.oyvey.manager.HoleManager;
import me.alpha432.oyvey.manager.Identify.IdentityManager;
import me.alpha432.oyvey.manager.InventoryManager;
import me.alpha432.oyvey.manager.ModuleManager;
import me.alpha432.oyvey.manager.PacketManager;
import me.alpha432.oyvey.manager.PositionManager;
import me.alpha432.oyvey.manager.PotionManager;
import me.alpha432.oyvey.manager.ReloadManager;
import me.alpha432.oyvey.manager.RotationManager;
import me.alpha432.oyvey.manager.SafetyManager;
import me.alpha432.oyvey.manager.ServerManager;
import me.alpha432.oyvey.manager.SpeedManager;
import me.alpha432.oyvey.manager.TextManager;
import me.alpha432.oyvey.manager.TimerManager;
import me.alpha432.oyvey.manager.TotemPopManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid="quantum", name="Quantum", version="0.4.6")
public class OyVey {
    public static final String MODID = "quantum";
    public static final String MODNAME = "Quantum";
    public static final String MODVER = "0.4.6";
    public static final Logger LOGGER = LogManager.getLogger((String)"Quantum");
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static SafetyManager safetyManager;
    public static IdentityManager identityManager;
    @Mod.Instance
    public static OyVey INSTANCE;
    private static boolean unloaded;

    public static void load() {
        LOGGER.info("\n\nLoading Quantum");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        totemPopManager = new TotemPopManager();
        timerManager = new TimerManager();
        packetManager = new PacketManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        textManager = new TextManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        commandManager = new CommandManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        friendManager = new FriendManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        safetyManager = new SafetyManager();
        identityManager = new IdentityManager();
        LOGGER.info("Initialized Managers");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        totemPopManager.init();
        timerManager.init();
        if (moduleManager.getModuleByClass(RPC.class).isEnabled()) {
            DiscordPresence.start();
        }
        LOGGER.info("\"Quantum initialized!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading \"Quantum");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        OyVey.onUnload();
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        configManager = null;
        positionManager = null;
        commandManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        safetyManager = null;
        identityManager = null;
        LOGGER.info("\"Quantum unloaded!\n");
    }

    public static void reload() {
        OyVey.unload(false);
        OyVey.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(OyVey.configManager.config.replaceFirst("Quantum/", ""));
            moduleManager.onUnloadPost();
            timerManager.unload();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Holy fuck!");
        LOGGER.info("Big nigga");
        LOGGER.info("i eat balls nigga i love kids i kidnap autistic kids");
        LOGGER.info("my name is");
    }

    public final ColorManager getColorManager() {
        return colorManager;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle((String)"Quantum v0.4.6");
        OyVey.load();
    }

    static {
        unloaded = false;
    }
}

