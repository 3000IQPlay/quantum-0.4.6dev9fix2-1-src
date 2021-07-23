/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.DeathEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.AutoCrystal;
import me.alpha432.oyvey.features.modules.combat.CrystalAura;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.FileManager;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.TextUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Chat
extends Module {
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.Chat));
    private final Setting<String> suffix = this.register(new Setting<Object>("Mode", "Quantum", v -> this.setting.getValue() == Settings.Chat));
    public Setting<Boolean> killmsg = this.register(new Setting<Object>("AutoGG", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Chat));
    private final Setting<Integer> targetResetTimer = this.register(new Setting<Integer>("Reset", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(90), v -> this.killmsg.getValue()));
    private final Setting<Integer> delay = this.register(new Setting<Integer>("KillDelay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(30), v -> this.killmsg.getValue()));
    public Map<EntityPlayer, Integer> targets = new ConcurrentHashMap<EntityPlayer, Integer>();
    private static final String path = "Quantum/killsmg.txt";
    public List<String> messages = new ArrayList<String>();
    private final Timer cooldownTimer = new Timer();
    public EntityPlayer cauraTarget;
    private boolean cooldown;
    private final Timer delayTimer = new Timer();
    public Setting<TextUtil.Color> timeStamps = this.register(new Setting<Object>("Time", (Object)TextUtil.Color.NONE, v -> this.setting.getValue() == Settings.Visual));
    public Setting<TextUtil.Color> bracket = this.register(new Setting<Object>("Bracket", (Object)TextUtil.Color.WHITE, v -> this.setting.getValue() == Settings.Visual && this.timeStamps.getValue() != TextUtil.Color.NONE && this.setting.getValue() == Settings.Visual));
    public Setting<Boolean> space = this.register(new Setting<Object>("Space", Boolean.valueOf(true), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> all = this.register(new Setting<Object>("All", Boolean.valueOf(false), v -> this.timeStamps.getValue() != TextUtil.Color.NONE && this.setting.getValue() == Settings.Visual));
    public Setting<Boolean> clean = this.register(new Setting<Object>("CleanChat", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Visual));
    public Setting<Boolean> infinite = this.register(new Setting<Object>("Infinite", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Visual));
    private final Timer timer = new Timer();
    public boolean check;
    private static Chat INSTANCE = new Chat();

    public Chat() {
        super("Chat", "c", Module.Category.CLIENT, true, false, false);
        this.setInstance();
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Chat getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Chat();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (Chat.mc.player == null) {
            return;
        }
        if (this.delayTimer.passedMs(this.delay.getValue().intValue())) {
            Chat.mc.player.sendChatMessage("I just walked " + ThreadLocalRandom.current().nextInt(1, 31) + "!");
            this.delayTimer.reset();
        }
    }

    @Override
    public void onEnable() {
        this.loadMessages();
        this.timer.reset();
        this.cooldownTimer.reset();
    }

    @Override
    public void onTick() {
        if (AutoCrystal.target != null && this.cauraTarget != AutoCrystal.target) {
            this.cauraTarget = AutoCrystal.target;
        }
        if (CrystalAura.currentTarget != null && this.cauraTarget != CrystalAura.currentTarget) {
            this.cauraTarget = CrystalAura.currentTarget;
        }
        if (!this.cooldown) {
            this.cooldownTimer.reset();
        }
        if (this.cooldownTimer.passedS(this.delay.getValue().intValue()) && this.cooldown) {
            this.cooldown = false;
            this.cooldownTimer.reset();
        }
        if (AutoCrystal.target != null) {
            this.targets.put(AutoCrystal.target, (int)(this.timer.getPassedTimeMs() / 1000L));
        }
        this.targets.replaceAll((p, v) -> (int)(this.timer.getPassedTimeMs() / 1000L));
        for (EntityPlayer player : this.targets.keySet()) {
            if (this.targets.get(player) <= this.targetResetTimer.getValue()) continue;
            this.targets.remove(player);
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onEntityDeath(DeathEvent event) {
        if (this.killmsg.getValue().booleanValue()) {
            if (this.targets.containsKey(event.player) && !this.cooldown) {
                this.announceDeath(event.player);
                this.cooldown = true;
                this.targets.remove(event.player);
            }
            if (event.player == this.cauraTarget && !this.cooldown) {
                this.announceDeath(event.player);
                this.cooldown = true;
            }
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer && !OyVey.friendManager.isFriend(event.getEntityPlayer())) {
            this.targets.put((EntityPlayer)event.getTarget(), 0);
        }
    }

    @SubscribeEvent
    public void onSendAttackPacket(PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)Chat.mc.world) instanceof EntityPlayer && !OyVey.friendManager.isFriend((EntityPlayer)packet.getEntityFromWorld((World)Chat.mc.world))) {
            this.targets.put((EntityPlayer)packet.getEntityFromWorld((World)Chat.mc.world), 0);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if ((this.clean.getValue().booleanValue() || this.infinite.getValue().booleanValue()) && event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage)event.getPacket()).getMessage();
            boolean bl = this.check = !s.startsWith(OyVey.commandManager.getPrefix());
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/") || s.startsWith(".") || s.startsWith("#") || s.startsWith(",") || s.startsWith("-") || s.startsWith("+") || s.startsWith("$") || s.startsWith(";")) {
                return;
            }
            String string = this.suffix.getValue();
            ((CPacketChatMessage)event.getPacket()).message = ((CPacketChatMessage)event.getPacket()).getMessage() + " \u23d0 " + TextUtil.toUnicode(string);
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    @SubscribeEvent
    public void onChatPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat) {
            // empty if block
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat)event.getPacket()).isSystem()) {
                return;
            }
            String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
            String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat)event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    public String getTimeString(String message) {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        return (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString("<", this.bracket.getValue())) + TextUtil.coloredString(date, this.timeStamps.getValue()) + (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString(">", this.bracket.getValue())) + (this.space.getValue() != false ? " " : "") + "\u00a7r";
    }

    private boolean shouldSendMessage(EntityPlayer player) {
        if (player.dimension != 1) {
            return false;
        }
        return player.getPosition().equals((Object)new Vec3i(0, 240, 0));
    }

    public void loadMessages() {
        this.messages = FileManager.readTextFileAllLines(path);
    }

    public String getRandomMessage() {
        this.loadMessages();
        Random rand = new Random();
        if (this.messages.size() == 0) {
            return "<player> just fucking died!";
        }
        if (this.messages.size() == 1) {
            return this.messages.get(0);
        }
        return this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
    }

    public void announceDeath(EntityPlayer target) {
        Chat.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(this.getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
    }

    public static enum AnnouncerMode {
        DotGod,
        Simple;

    }

    public static enum SuffixMode {
        None,
        Custom;

    }

    public static enum Settings {
        Chat,
        Visual;

    }
}

