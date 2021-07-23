/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class NickHider
extends Module {
    public final Setting<String> NameString = this.register(new Setting<String>("Name", "New Name Here..."));
    private static NickHider instance;

    public NickHider() {
        super("NickHider", "Changes name", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + this.NameString.getValue());
    }

    public static NickHider getInstance() {
        if (instance == null) {
            instance = new NickHider();
        }
        return instance;
    }
}

