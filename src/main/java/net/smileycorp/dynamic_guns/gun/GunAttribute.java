package net.smileycorp.dynamic_guns.gun;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;

public class GunAttribute {

    public Component getHoverText() {
        return Component.literal("");
    }

    public static GunAttribute deserialize(JsonObject obj) {
        return new GunAttribute();
    }

}
