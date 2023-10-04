package net.smileycorp.dynamic_guns.gun;

import com.google.gson.JsonObject;

public class GunAttribute {

    public static GunAttribute deserialize(JsonObject obj) {
        return new GunAttribute();
    }

}
