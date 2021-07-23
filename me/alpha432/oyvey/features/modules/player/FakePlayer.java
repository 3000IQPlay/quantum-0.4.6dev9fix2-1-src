/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.IOUtils;

public class FakePlayer
extends Module {
    public static final String[][] OyVeyInfo = new String[][]{{"8af022c8-b926-41a0-8b79-2b544ff00fcf", "3arthqu4ke", "3", "0"}, {"0aa3b04f-786a-49c8-bea9-025ee0dd1e85", "zb0b", "-3", "0"}, {"19bf3f1f-fe06-4c86-bea5-3dad5df89714", "3vt", "0", "-3"}, {"e47d6571-99c2-415b-955e-c4bc7b55941b", "Phobos_eu", "0", "3"}, {"b01f9bc1-cb7c-429a-b178-93d771f00926", "bakpotatisen", "6", "0"}, {"b232930c-c28a-4e10-8c90-f152235a65c5", "948", "-6", "0"}, {"ace08461-3db3-4579-98d3-390a67d5645b", "Browswer", "0", "-6"}, {"5bead5b0-3bab-460d-af1d-7929950f40c2", "fsck", "0", "6"}, {"78ee2bd6-64c4-45f0-96e5-0b6747ba7382", "Fit", "0", "9"}, {"78ee2bd6-64c4-45f0-96e5-0b6747ba7382", "deathcurz0", "0", "-9"}};
    private final String name = "BigNigger";
    public Setting<Boolean> multi = this.register(new Setting<Boolean>("Multi", false));
    private final Setting<Integer> players = this.register(new Setting<Object>("Players", 1, 1, 9, v -> this.multi.getValue(), "Amount of other players."));
    private EntityOtherPlayerMP _fakePlayer;
    private final List<EntityOtherPlayerMP> fakeEntities = new ArrayList<EntityOtherPlayerMP>();
    public List<Integer> fakePlayerIdList = new ArrayList<Integer>();

    public FakePlayer() {
        super("FakePlayer", "fp", Module.Category.PLAYER, false, false, false);
    }

    public static String getUuid(String name) {
        JsonParser parser = new JsonParser();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String UUIDJson = IOUtils.toString((URL)new URL(url), (Charset)StandardCharsets.UTF_8);
            if (UUIDJson.isEmpty()) {
                return "invalid name";
            }
            JsonObject UUIDObject = (JsonObject)parser.parse(UUIDJson);
            return FakePlayer.reformatUuid(UUIDObject.get("id").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private static String reformatUuid(String uuid) {
        String longUuid = "";
        longUuid = longUuid + uuid.substring(1, 9) + "-";
        longUuid = longUuid + uuid.substring(9, 13) + "-";
        longUuid = longUuid + uuid.substring(13, 17) + "-";
        longUuid = longUuid + uuid.substring(17, 21) + "-";
        longUuid = longUuid + uuid.substring(21, 33);
        return longUuid;
    }

    @Override
    public void onEnable() {
        if (FakePlayer.fullNullCheck()) {
            this.disable();
            return;
        }
        if (this.multi.getValue().booleanValue()) {
            int amount = 0;
            int entityId = -101;
            for (String[] data : OyVeyInfo) {
                this.addFakePlayer(data[0], data[1], entityId, Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                if (++amount >= this.players.getValue()) {
                    return;
                }
                entityId -= amount;
            }
        } else {
            this._fakePlayer = null;
            if (FakePlayer.mc.player != null) {
                try {
                    this.getClass();
                    UUID uUID = UUID.fromString(FakePlayer.getUuid("BigNigger"));
                    this.getClass();
                    this._fakePlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(uUID, "BigNigger"));
                }
                catch (Exception e) {
                    UUID uUID = UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03");
                    this.getClass();
                    this._fakePlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(uUID, "BigNigger"));
                    Command.sendMessage("Failed to load uuid, setting another one.");
                }
                Object[] objectArray = new Object[1];
                this.getClass();
                objectArray[0] = "BigNigger";
                Command.sendMessage(String.format("%s has been spawned.", objectArray));
                this._fakePlayer.copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
                this._fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
                FakePlayer.mc.world.addEntityToWorld(-100, (Entity)this._fakePlayer);
            }
        }
    }

    private void addFakePlayer(String uuid, String name, int entityId, int offsetX, int offsetZ) {
        GameProfile profile = new GameProfile(UUID.fromString(uuid), name);
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, profile);
        fakePlayer.copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
        fakePlayer.posX += (double)offsetX;
        fakePlayer.posZ += (double)offsetZ;
        fakePlayer.setHealth(FakePlayer.mc.player.getHealth() + FakePlayer.mc.player.getAbsorptionAmount());
        this.fakeEntities.add(fakePlayer);
        FakePlayer.mc.world.addEntityToWorld(entityId, (Entity)fakePlayer);
        this.fakePlayerIdList.add(entityId);
    }

    @Override
    public void onDisable() {
        if (FakePlayer.mc.world != null && FakePlayer.mc.player != null) {
            super.onDisable();
            FakePlayer.mc.world.removeEntity((Entity)this._fakePlayer);
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (this.isEnabled()) {
            this.disable();
        }
    }
}

