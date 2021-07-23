/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.FileManager;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifiers
extends Module {
    public Setting<Boolean> totemPops = this.register(new Setting<Boolean>("PopNotify", false));
    public Setting<Integer> PopDelay = this.register(new Setting<Object>("NotifyDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(5000), v -> this.totemPops.getValue()));
    public Setting<Boolean> visualRange = this.register(new Setting<Boolean>("VisualRange", false));
    public Setting<Boolean> VisualRangeSound = this.register(new Setting<Boolean>("VSound", Boolean.valueOf(false), v -> this.visualRange.getValue()));
    public Setting<Boolean> visualRangeCoords = this.register(new Setting<Object>("VCoords", Boolean.valueOf(true), v -> this.visualRange.getValue()));
    public Setting<Boolean> visualRangeLeaving = this.register(new Setting<Object>("LeavingRange", Boolean.valueOf(false), v -> this.visualRange.getValue()));
    public Setting<Boolean> pearlNotify = this.register(new Setting<Boolean>("PearlNotify", false));
    public Setting<Boolean> ghastNotify = this.register(new Setting<Boolean>("GhastNotify", false));
    public Setting<Boolean> ghastSound = this.register(new Setting<Object>("GSound", Boolean.valueOf(true), v -> this.ghastNotify.getValue()));
    public Setting<Boolean> ghastChat = this.register(new Setting<Object>("GCoords", Boolean.valueOf(true), v -> this.ghastNotify.getValue()));
    public Setting<Boolean> burrow = this.register(new Setting<Boolean>("Burrow", false));
    public Setting<Boolean> strength = this.register(new Setting<Boolean>("Strength", false));
    public Setting<Boolean> crash = this.register(new Setting<Boolean>("CrashInfo", false));
    private static final String fileName = "phobos/util/ModuleMessage_List.txt";
    private final List<EntityPlayer> burrowedPlayers = new ArrayList<EntityPlayer>();
    public static HashMap<String, Integer> TotemPopContainer = new HashMap();
    private List<EntityPlayer> knownPlayers = new ArrayList<EntityPlayer>();
    private static final List<String> modules = new ArrayList<String>();
    public static Map<EntityPlayer, Integer> strMap;
    private static Notifiers INSTANCE;
    public static Set<EntityPlayer> strengthPlayers;
    private Set<Entity> ghasts = new HashSet<Entity>();
    private final Timer timer = new Timer();
    private Entity enderPearl;
    private boolean check;
    private boolean flag;

    public Notifiers() {
        super("Notifications", "n", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static Notifiers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifiers();
        }
        return INSTANCE;
    }

    public static void displayCrash(Exception e) {
        Command.sendMessage("\u00a7cException caught: " + e.getMessage());
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        this.check = true;
        this.loadFile();
        this.check = false;
    }

    @Override
    public void onEnable() {
        this.ghasts.clear();
        this.flag = true;
        TotemPopContainer.clear();
        this.knownPlayers = new ArrayList<EntityPlayer>();
        if (!this.check) {
            this.loadFile();
        }
    }

    @Override
    public void onTick() {
        if (!this.burrow.getValue().booleanValue()) {
            return;
        }
        for (EntityPlayer entityPlayer2 : Notifiers.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != Notifiers.mc.player).collect(Collectors.toList())) {
            if (this.burrowedPlayers.contains(entityPlayer2) || !this.isInBurrow(entityPlayer2)) continue;
            Command.sendMessage(ChatFormatting.RED + entityPlayer2.getDisplayNameString() + ChatFormatting.GRAY + " has burrowed");
            this.burrowedPlayers.add(entityPlayer2);
        }
    }

    private boolean isInBurrow(EntityPlayer entityPlayer) {
        BlockPos playerPos = new BlockPos(this.getMiddlePosition(entityPlayer.posX), entityPlayer.posY, this.getMiddlePosition(entityPlayer.posZ));
        return Notifiers.mc.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN || Notifiers.mc.world.getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST || Notifiers.mc.world.getBlockState(playerPos).getBlock() == Blocks.ANVIL;
    }

    private double getMiddlePosition(double positionIn) {
        double positionFinal = Math.round(positionIn);
        if ((double)Math.round(positionIn) > positionIn) {
            positionFinal -= 0.5;
        } else if ((double)Math.round(positionIn) <= positionIn) {
            positionFinal += 0.5;
        }
        return positionFinal;
    }

    @SubscribeEvent
    public void onPotionColor(PotionColorCalculationEvent event) {
        if (!this.strength.getValue().booleanValue()) {
            return;
        }
        if (event.getEntityLiving() instanceof EntityPlayer) {
            boolean hasStrength = false;
            for (PotionEffect potionEffect : event.getEffects()) {
                if (potionEffect.getPotion() != MobEffects.STRENGTH) continue;
                strMap.put((EntityPlayer)event.getEntityLiving(), potionEffect.getAmplifier());
                Command.sendMessage(ChatFormatting.RED + event.getEntityLiving().getName() + ChatFormatting.GRAY + " has strength");
                hasStrength = true;
                break;
            }
            if (strMap.containsKey(event.getEntityLiving()) && !hasStrength) {
                strMap.remove(event.getEntityLiving());
                Command.sendMessage(ChatFormatting.RED + event.getEntityLiving().getName() + ChatFormatting.GRAY + " no longer has strength");
            }
        }
    }

    public void onDeath(EntityPlayer player) {
        if (this.totemPops.getValue().booleanValue() && TotemPopContainer.containsKey(player.getName())) {
            int l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.remove(player.getName());
            if (l_Count == 1) {
                Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totem");
            } else {
                Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totems");
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (this.totemPops.getValue().booleanValue()) {
            if (Notifiers.fullNullCheck()) {
                return;
            }
            if (Notifiers.mc.player.equals((Object)player)) {
                return;
            }
            int l_Count = 1;
            if (TotemPopContainer.containsKey(player.getName())) {
                l_Count = TotemPopContainer.get(player.getName());
                TotemPopContainer.put(player.getName(), ++l_Count);
            } else {
                TotemPopContainer.put(player.getName(), l_Count);
            }
            if (l_Count == 1) {
                Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " popped " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totem");
            } else {
                Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " popped " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totems");
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.check && this.timer.passedMs(750L)) {
            this.check = false;
        }
        if (this.visualRange.getValue().booleanValue()) {
            ArrayList tickPlayerList = new ArrayList(Notifiers.mc.world.playerEntities);
            if (tickPlayerList.size() > 0) {
                for (EntityPlayer player : tickPlayerList) {
                    if (player.getName().equals(Notifiers.mc.player.getName()) || this.knownPlayers.contains(player)) continue;
                    this.knownPlayers.add(player);
                    if (OyVey.friendManager.isFriend(player)) {
                        Command.sendMessage(ChatFormatting.GRAY + "Player " + ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " entered your visual range" + (this.visualRangeCoords.getValue() != false ? " at (" + ChatFormatting.RED + (int)player.posX + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posY + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posZ + ChatFormatting.GRAY + ")!" : "!"));
                    } else {
                        Command.sendMessage(ChatFormatting.GRAY + "Player " + ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " entered your visual range" + (this.visualRangeCoords.getValue() != false ? " at (" + ChatFormatting.RED + (int)player.posX + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posY + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posZ + ChatFormatting.GRAY + ")!" : "!"));
                    }
                    if (this.VisualRangeSound.getValue().booleanValue()) {
                        Notifiers.mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    }
                    return;
                }
            }
            if (this.knownPlayers.size() > 0) {
                for (EntityPlayer player : this.knownPlayers) {
                    if (tickPlayerList.contains(player)) continue;
                    this.knownPlayers.remove(player);
                    if (this.visualRangeLeaving.getValue().booleanValue()) {
                        if (OyVey.friendManager.isFriend(player)) {
                            Command.sendMessage("Player \u00a7a" + player.getName() + "\u00a7r left your visual range" + (this.visualRangeCoords.getValue() != false ? " at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!" : "!"));
                        } else {
                            Command.sendMessage("Player \u00a7c" + player.getName() + "\u00a7r left your visual range" + (this.visualRangeCoords.getValue() != false ? " at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!" : "!"));
                        }
                    }
                    return;
                }
            }
        }
        if (this.pearlNotify.getValue().booleanValue()) {
            if (Notifiers.mc.world == null || Notifiers.mc.player == null) {
                return;
            }
            this.enderPearl = null;
            for (Object e : Notifiers.mc.world.loadedEntityList) {
                if (!(e instanceof EntityEnderPearl)) continue;
                this.enderPearl = e;
                break;
            }
            if (this.enderPearl == null) {
                this.flag = true;
                return;
            }
            EntityPlayer closestPlayer = null;
            for (EntityPlayer entity : Notifiers.mc.world.playerEntities) {
                if (closestPlayer == null) {
                    closestPlayer = entity;
                    continue;
                }
                if (closestPlayer.getDistance(this.enderPearl) <= entity.getDistance(this.enderPearl)) continue;
                closestPlayer = entity;
            }
            if (closestPlayer == Notifiers.mc.player) {
                this.flag = false;
            }
            if (closestPlayer != null && this.flag) {
                String faceing = this.enderPearl.getHorizontalFacing().toString();
                if (faceing.equals("west")) {
                    faceing = "east";
                } else if (faceing.equals("east")) {
                    faceing = "west";
                }
                Command.sendSilentMessage(OyVey.friendManager.isFriend(closestPlayer.getName()) ? ChatFormatting.AQUA + closestPlayer.getName() + ChatFormatting.GRAY + " has just thrown a pearl heading " + ChatFormatting.RED + faceing + ChatFormatting.GRAY + "!" : ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.GRAY + " has just thrown a pearl heading " + ChatFormatting.RED + faceing + ChatFormatting.GRAY + "!");
                this.flag = false;
            }
        }
        if (this.ghastNotify.getValue().booleanValue()) {
            for (Entity entity : Notifiers.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityGhast) || this.ghasts.contains(entity)) continue;
                if (this.ghastChat.getValue().booleanValue()) {
                    Command.sendMessage(ChatFormatting.GRAY + "Ghast Detected at: " + ChatFormatting.RED + entity.getPosition().getX() + "x" + ChatFormatting.GRAY + ", " + ChatFormatting.RED + entity.getPosition().getY() + "y" + ChatFormatting.GRAY + ", " + ChatFormatting.RED + entity.getPosition().getZ() + "z" + ChatFormatting.GRAY + ".");
                }
                this.ghasts.add(entity);
                if (!this.ghastSound.getValue().booleanValue()) continue;
                Notifiers.mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    public void loadFile() {
        List<String> fileInput = FileManager.readTextFileAllLines(fileName);
        Iterator<String> i = fileInput.iterator();
        modules.clear();
        while (i.hasNext()) {
            String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) continue;
            modules.add(s);
        }
    }

    static {
        INSTANCE = new Notifiers();
        strengthPlayers = new HashSet<EntityPlayer>();
        strMap = new HashMap<EntityPlayer, Integer>();
    }
}

