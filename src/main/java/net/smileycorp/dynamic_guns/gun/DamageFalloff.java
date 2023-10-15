package net.smileycorp.dynamic_guns.gun;

import com.google.gson.JsonObject;

import java.util.Locale;

public class DamageFalloff {

    public static final DamageFalloff DEFAULT = new DamageFalloff(0, 0, Type.LINEAR, 1);

    private final float start;
    private final float end;
    private final Type type;
    private final float amount;

    public DamageFalloff(float start, float end, DamageFalloff.Type type, float amount) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.amount = amount;
    }

    public float getStart() {
        return start;
    }

    public float getEnd() {
        return end;
    }

    public Type getType() {
        return type;
    }

    public float getAmount() {
        return amount;
    }

    public enum Type {

        LINEAR,
        EXPONENTIAL

    }

    public static DamageFalloff deserialize(JsonObject obj) {
        float start = obj.get("start").getAsFloat();
        float end = obj.get("end").getAsFloat();
        Type type = Type.valueOf(obj.get("type").getAsString().toUpperCase(Locale.US));
        float amount = obj.get("amount").getAsFloat();
        return new DamageFalloff(start, end, type, amount);
    }

}
