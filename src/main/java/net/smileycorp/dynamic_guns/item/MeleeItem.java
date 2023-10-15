package net.smileycorp.dynamic_guns.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Locale;


public class MeleeItem extends JsonItem {

    private final Multimap<Attribute, AttributeModifier> modifiers;
    private final boolean disables_shields;

    private MeleeItem(Properties props, boolean enchanted, ResourceLocation creative_tab, float damage, float speed, boolean disables_shields) {
        super(props, enchanted, creative_tab);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
        this.disables_shields = disables_shields;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? modifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
        if (stack.isDamageableItem()) stack.hurt(1, user.getRandom(), user instanceof ServerPlayer ? (ServerPlayer) user : null);
        return super.hurtEnemy(stack, target, user);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return disables_shields;
    }

    public static MeleeItem deserialize(JsonObject obj) {
        Properties props = new Properties();
        if (obj.has("rarity")) {
            JsonPrimitive value = obj.get("rarity").getAsJsonPrimitive();
            Rarity rarity = null;
            if (value.isNumber()) rarity = Rarity.create(String.valueOf(value.getAsInt()), text -> text.withColor(value.getAsInt()));
            else if (value.isString()) {
                String str = value.getAsString();
                rarity = Rarity.valueOf(str.toUpperCase(Locale.US));
                if (rarity == null) Rarity.create(str, ChatFormatting.valueOf(str.toUpperCase(Locale.US)));
                if (rarity == null) Rarity.create(str, text -> text.withColor(Integer.decode(str)));
            }
            if (rarity != null) props.rarity(rarity);
        }
        boolean enchanted = obj.has("enchanted") ? obj.get("enchanted").getAsBoolean() : false;
        if (obj.has("fire_resistant") && obj.get("fire_resistant").getAsBoolean()) props.fireResistant();
        ResourceLocation creative_tab = obj.has("creative_tab") ? ResourceLocation.tryParse(obj.get("creative_tab").getAsString()) : null;
        float damage = obj.get("attack_damage").getAsFloat();
        float speed = obj.get("attack_speed").getAsFloat();
        if (obj.has("max_durability")) {
            int max_durability = obj.get("max_durability").getAsInt();
            if (max_durability >= 0) props.durability(max_durability);
        }
        if (obj.has("can_repair") &! obj.get("can_repair").getAsBoolean()) props.setNoRepair();
        boolean disables_shields = (obj.has("disables_shields") && obj.get("disables_shields").getAsBoolean());
        return new MeleeItem(props, enchanted, creative_tab, damage, speed, disables_shields);
    }

}
