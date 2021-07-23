/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager.Identify;

import com.google.gson.annotations.SerializedName;
import me.alpha432.oyvey.manager.Identify.UtilThree;

public class UtilFour
extends UtilThree {
    String username;
    String content;
    @SerializedName(value="avatar_url")
    String avatarUrl;
    @SerializedName(value="tts")
    boolean textToSpeech;

    public UtilFour() {
        this(null, "", null, false);
    }

    public UtilFour(String content) {
        this(null, content, null, false);
    }

    public UtilFour(String username, String content, String avatar_url) {
        this(username, content, avatar_url, false);
    }

    public UtilFour(String username, String content, String avatar_url, boolean tts) {
        this.capeUsername(username);
        this.setCape(content);
        this.checkCapeUrl(avatar_url);
        this.isDev(tts);
    }

    public void capeUsername(String username) {
        this.username = username != null ? username.substring(0, Math.min(31, username.length())) : null;
    }

    public void setCape(String content) {
        this.content = content;
    }

    public void checkCapeUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void isDev(boolean textToSpeech) {
        this.textToSpeech = textToSpeech;
    }

    public static class Builder {
        private final UtilFour message;

        public Builder() {
            this.message = new UtilFour();
        }

        public Builder(String content) {
            this.message = new UtilFour(content);
        }

        public Builder withUsername(String username) {
            this.message.capeUsername(username);
            return this;
        }

        public Builder withContent(String content) {
            this.message.setCape(content);
            return this;
        }

        public Builder withAvatarURL(String avatarURL) {
            this.message.checkCapeUrl(avatarURL);
            return this;
        }

        public Builder withDev(boolean tts) {
            this.message.isDev(tts);
            return this;
        }

        public UtilFour build() {
            return this.message;
        }
    }
}

