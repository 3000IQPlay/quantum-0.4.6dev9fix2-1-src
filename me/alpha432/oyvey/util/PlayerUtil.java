/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.util.Timer;
import me.alpha432.oyvey.util.Util;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.IOUtils;

public class PlayerUtil
implements Util {
    public static Timer timer;
    private static final JsonParser PARSER;

    public static String getNameFromUUID(UUID uuid) {
        try {
            lookUpName process = new lookUpName(uuid);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getNameFromUUID(String uuid) {
        try {
            lookUpName process = new lookUpName(uuid);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static UUID getUUIDFromName(String name) {
        try {
            lookUpUUID process = new lookUpUUID(name);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String requestIDs(String data) {
        try {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            String res = PlayerUtil.convertStreamToString(in);
            ((InputStream)in).close();
            conn.disconnect();
            return res;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

    public static List<String> getHistoryOfNames(UUID id) {
        try {
            JsonArray array = PlayerUtil.getResources(new URL("https://api.mojang.com/user/profiles/" + PlayerUtil.getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            ArrayList temp = Lists.newArrayList();
            for (JsonElement e : array) {
                JsonObject node = e.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                temp.add(name + "\u00c2\u00a78" + new Date(changedAt).toString());
            }
            Collections.sort(temp);
            return temp;
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static String getIdNoHyphens(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(URL url, String request) throws Exception {
        return PlayerUtil.getResources(url, request, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static JsonElement getResources(URL url, String request, JsonElement element) throws Exception {
        HttpsURLConnection connection = null;
        try {
            JsonElement data;
            connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.writeBytes(AdvancementManager.GSON.toJson(element));
                output.close();
            }
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }
            scanner.close();
            String json = builder.toString();
            JsonElement jsonElement = data = PARSER.parse(json);
            return jsonElement;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static boolean isInHole() {
        BlockPos player_block = PlayerUtil.getPlayerPos();
        return PlayerUtil.mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR && PlayerUtil.mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR && PlayerUtil.mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR && PlayerUtil.mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(PlayerUtil.mc.player.posX), Math.floor(PlayerUtil.mc.player.posY), Math.floor(PlayerUtil.mc.player.posZ));
    }

    public static double getHealth() {
        return PlayerUtil.mc.player.getHealth() + PlayerUtil.mc.player.getAbsorptionAmount();
    }

    public static Vec3d getCenter(double posX, double posY, double posZ) {
        return new Vec3d(Math.floor(posX) + 0.5, Math.floor(posY), Math.floor(posZ) + 0.5);
    }

    static {
        PARSER = new JsonParser();
    }

    public static class lookUpName
    implements Runnable {
        private final String uuid;
        private final UUID uuidID;
        private volatile String name;

        public lookUpName(String input) {
            this.uuid = input;
            this.uuidID = UUID.fromString(input);
        }

        public lookUpName(UUID input) {
            this.uuidID = input;
            this.uuid = input.toString();
        }

        @Override
        public void run() {
            this.name = this.lookUpName();
        }

        public String lookUpName() {
            EntityPlayer player = null;
            if (Util.mc.world != null) {
                player = Util.mc.world.getPlayerEntityByUUID(this.uuidID);
            }
            if (player == null) {
                String url = "https://api.mojang.com/user/profiles/" + this.uuid.replace("-", "") + "/names";
                try {
                    String nameJson = IOUtils.toString((URL)new URL(url));
                    if (nameJson.contains(",")) {
                        List<String> names = Arrays.asList(nameJson.split(","));
                        Collections.reverse(names);
                        return names.get(1).replace("{\"name\":\"", "").replace("\"", "");
                    }
                    return nameJson.replace("[{\"name\":\"", "").replace("\"}]", "");
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                    return null;
                }
            }
            return player.getName();
        }

        public String getName() {
            return this.name;
        }
    }

    public static class lookUpUUID
    implements Runnable {
        private final String name;
        private volatile UUID uuid;

        public lookUpUUID(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            NetworkPlayerInfo profile;
            try {
                ArrayList infoMap = new ArrayList(Objects.requireNonNull(Util.mc.getConnection()).getPlayerInfoMap());
                profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
                assert (profile != null);
                this.uuid = profile.getGameProfile().getId();
            }
            catch (Exception e) {
                profile = null;
            }
            if (profile == null) {
                Command.sendMessage("Player isn't online. Looking up UUID..");
                String s = PlayerUtil.requestIDs("[\"" + this.name + "\"]");
                if (s == null || s.isEmpty()) {
                    Command.sendMessage("Couldn't find player ID. Are you connected to the internet? (0)");
                } else {
                    JsonElement element = new JsonParser().parse(s);
                    if (element.getAsJsonArray().size() == 0) {
                        Command.sendMessage("Couldn't find player ID. (1)");
                    } else {
                        try {
                            String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            this.uuid = UUIDTypeAdapter.fromString((String)id);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Command.sendMessage("Couldn't find player ID. (2)");
                        }
                    }
                }
            }
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }
}

