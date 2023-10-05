package net.smileycorp.dynamic_guns.gun;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GunProperties {

    private final Item ammo;
    private final EntityType<?> projectile;
    private FireMode fire_mode = FireMode.SINGLE_SHOT;
    private double projectile_speed = 5f;
    private int ammo_per_item = 1;
    private int mag_size = 1;
    private int fire_rate = 20;
    private float damage = 6;
    private int reload_speed = 30;
    private SoundEvent fire_sound = null;
    private SoundEvent reload_sound = null;
    private SoundEvent empty_sound = null;
    private List<GunAttribute> attributes = Lists.newArrayList();
    private Map<String, Object> additional_data = Maps.newHashMap();

    public GunProperties(Item ammo, EntityType<?> projectile) {
        this.ammo = ammo;
        this.projectile = projectile;
    }

    public boolean isAmmo(ItemStack stack) {
        return stack.is(ammo);
    }

    public FireMode getFireMode() {
        return fire_mode;
    }


    public double getProjectileSpeed() {
        return projectile_speed;
    }

    public int getAmmoPerItem() {
        return ammo_per_item;
    }

    public int getMagSize() {
        return mag_size;
    }

    public int getFireRate() {
        return fire_rate;
    }

    public float getDamage() {
        return damage;
    }

    public int getReloadSpeed() {
        return reload_speed;
    }

    public SoundEvent getFireSound() {
        return fire_sound;
    }

    public SoundEvent getReloadSound() {
        return reload_sound;
    }

    public SoundEvent getEmptySound() {
        return empty_sound;
    }

    public List<GunAttribute> getAttributes() {
        return attributes;
    }

    public Object getAdditionalData(String name) {
        return additional_data.get(name);
    }

    public static GunProperties deserialize(JsonObject obj) {
        Item ammo = ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("ammo").getAsString()));
        EntityType<?> projectile = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(obj.get("projectile").getAsString()));
        GunProperties props = new GunProperties(ammo, projectile);
        if (obj.has("fire_mode")) try {
            props.fire_mode = FireMode.valueOf(obj.get("fire_mode").getAsString().toUpperCase(Locale.US));
        } catch (Exception e) {}
        if (obj.has("projectile_speed")) props.projectile_speed = obj.get("projectile_speed").getAsDouble();
        if (obj.has("ammo_per_item")) props.ammo_per_item = obj.get("ammo_per_item").getAsInt();
        if (obj.has("mag_size")) props.mag_size = obj.get("mag_size").getAsInt();
        if (obj.has("fire_rate")) props.fire_rate = obj.get("fire_rate").getAsInt();
        if (obj.has("damage")) props.damage = obj.get("damage").getAsFloat();
        if (obj.has("reload_speed")) props.reload_speed = obj.get("reload_speed").getAsInt();
        if (obj.has("fire_sound")) try {
            props.fire_sound = SoundEvent.createFixedRangeEvent(new ResourceLocation(obj.get("fire_sound").getAsString()), 0);
        } catch (Exception e) {}
        if (obj.has("reload_sound")) try {
            props.reload_sound = SoundEvent.createFixedRangeEvent(new ResourceLocation(obj.get("reload_sound").getAsString()), 0);
        } catch (Exception e) {}
        if (obj.has("empty_sound")) try {
            props.empty_sound = SoundEvent.createFixedRangeEvent(new ResourceLocation(obj.get("empty_sound").getAsString()), 0);
        } catch (Exception e) {}
        for (JsonElement element : obj.get("attributes").getAsJsonArray()) {
            GunAttribute attribute = GunAttribute.deserialize(element.getAsJsonObject());
            if (attribute != null) props.attributes.add(attribute);
        }
        JsonObject additional_data = obj.get("additional_data").getAsJsonObject();
        if (props.fire_mode == FireMode.VARIABLE) {
            List<FireMode> fireModes = Lists.newArrayList();
            for (JsonElement element : additional_data.get("fire_modes").getAsJsonArray()) {
                try {
                    fireModes.add(FireMode.valueOf(element.getAsString().toUpperCase(Locale.US)));
                } catch (Exception e) {}
            }
            props.additional_data.put("fire_modes", fireModes);
        }
        if (props.fire_mode == FireMode.BURST || (props.additional_data.containsKey("fire_modes")
                && ((List)props.additional_data.get("fire_modes")).contains(FireMode.BURST))) {
           props.additional_data.put("burst_shots", additional_data.get("burst_shots").getAsInt());
           props.additional_data.put("burst_rate", additional_data.get("burst_rate").getAsInt());
        }
        return props;
    }

}