/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.alpha432.oyvey.features.Feature;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionManager
extends Feature {
    private final Map<EntityPlayer, PotionList> potions = new ConcurrentHashMap<EntityPlayer, PotionList>();

    public List<PotionEffect> getOwnPotions() {
        return this.getPlayerPotions((EntityPlayer)PotionManager.mc.player);
    }

    public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
        PotionList list = this.potions.get(player);
        List<PotionEffect> potions = new ArrayList<PotionEffect>();
        if (list != null) {
            potions = list.getEffects();
        }
        return potions;
    }

    public PotionEffect[] getImportantPotions(EntityPlayer player) {
        PotionEffect[] array = new PotionEffect[3];
        for (PotionEffect effect : this.getPlayerPotions(player)) {
            Potion potion = effect.getPotion();
            switch (I18n.format((String)potion.getName(), (Object[])new Object[0]).toLowerCase()) {
                case "strength": {
                    array[0] = effect;
                }
                case "weakness": {
                    array[1] = effect;
                }
                case "speed": {
                    array[2] = effect;
                }
            }
        }
        return array;
    }

    public String getPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();
        return I18n.format((String)potion.getName(), (Object[])new Object[0]) + " " + (effect.getAmplifier() + 1) + " " + ChatFormatting.WHITE + Potion.getPotionDurationString((PotionEffect)effect, (float)1.0f);
    }

    public String getColoredPotionString(PotionEffect effect) {
        return this.getPotionString(effect);
    }

    public static class PotionList {
        private final List<PotionEffect> effects = new ArrayList<PotionEffect>();

        public void addEffect(PotionEffect effect) {
            if (effect != null) {
                this.effects.add(effect);
            }
        }

        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }
}

