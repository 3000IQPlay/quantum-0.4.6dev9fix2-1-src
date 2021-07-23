/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.misc;

import java.util.ArrayList;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.RusherHackUtil;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NewChunks
extends Module {
    public static ArrayList<Chunk> coords = new ArrayList();

    public NewChunks() {
        super("NewChunks", "nw", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (Chunk chunk : coords) {
            int x = chunk.x * 16;
            boolean y = false;
            int z = chunk.z * 16;
            NewChunks.chunkESP(x, (double)y, z);
        }
    }

    @SubscribeEvent
    public void eventSPacketChunk(PacketEvent e) {
        if (e.getPacket() instanceof SPacketChunkData && !((SPacketChunkData)e.getPacket()).isFullChunk()) {
            coords.add(NewChunks.mc.world.getChunk(((SPacketChunkData)e.getPacket()).getChunkX(), ((SPacketChunkData)e.getPacket()).getChunkZ()));
        }
    }

    public static void chunkESP(double x, double y, double z) {
        double posX = x - RusherHackUtil.getRenderPosX();
        double posY = y - RusherHackUtil.getRenderPosY();
        double posZ = z - RusherHackUtil.getRenderPosZ();
        GL11.glPushMatrix();
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDisable((int)3553);
        GL11.glDepthMask((boolean)false);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth((float)1.0f);
        GL11.glColor3f((float)189.0f, (float)0.0f, (float)0.0f);
        GL11.glBegin((int)2);
        GL11.glVertex3d((double)posX, (double)posY, (double)posZ);
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)posZ);
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)posZ);
        GL11.glVertex3d((double)posX, (double)posY, (double)posZ);
        GL11.glEnd();
        GL11.glBegin((int)2);
        GL11.glVertex3d((double)posX, (double)posY, (double)posZ);
        GL11.glVertex3d((double)posX, (double)posY, (double)(posZ + 16.0));
        GL11.glEnd();
        GL11.glBegin((int)2);
        GL11.glVertex3d((double)posX, (double)posY, (double)(posZ + 16.0));
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)(posZ + 16.0));
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)(posZ + 16.0));
        GL11.glVertex3d((double)posX, (double)posY, (double)(posZ + 16.0));
        GL11.glEnd();
        GL11.glBegin((int)2);
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)(posZ + 16.0));
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)posZ);
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)posZ);
        GL11.glVertex3d((double)(posX + 16.0), (double)posY, (double)(posZ + 16.0));
        GL11.glColor3f((float)189.0f, (float)0.0f, (float)0.0f);
        GL11.glEnd();
        GL11.glDisable((int)3042);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

