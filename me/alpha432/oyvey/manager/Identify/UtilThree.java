/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager.Identify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UtilThree {
    private static final Gson gson = new Gson();
    private static final Gson PRETTY_PRINTING = new GsonBuilder().setPrettyPrinting().create();

    public String toJson() {
        return gson.toJson((Object)this);
    }

    public String toJson(boolean prettyPrinting) {
        return prettyPrinting ? PRETTY_PRINTING.toJson((Object)this) : gson.toJson((Object)this);
    }
}

