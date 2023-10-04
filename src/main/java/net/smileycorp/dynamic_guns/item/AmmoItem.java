package net.smileycorp.dynamic_guns.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.ForgeRegistries;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;

import java.util.Locale;


public class AmmoItem extends Item implements CreativeTabsProvider {

    private final boolean enchanted;
    private final ResourceLocation creative_tab;

    private AmmoItem(Properties props, boolean enchanted, ResourceLocation creative_tab) {
        super(props);
        this.enchanted = enchanted;
        this.creative_tab = creative_tab;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return enchanted || stack.isEnchanted();
    }

    @Override
    public boolean canAddToTab(ResourceKey<CreativeModeTab> tab) {
        DynamicGunsLogger.logInfo(tab.location());
        DynamicGunsLogger.logInfo(creative_tab);
        return tab.location().equals(creative_tab);
    }

    public static AmmoItem deserialize(JsonObject obj) {
        Properties props = new Properties();
        if (obj.has("stacks_to")) props.stacksTo(obj.get("stack_size").getAsInt());
        if (obj.has("rarity")) {
            JsonPrimitive value = obj.get("rarity").getAsJsonPrimitive();
            Rarity rarity = null;
            if (value.isNumber()) rarity = Rarity.create(String.valueOf(value.getAsInt()), text -> text.withColor(value.getAsInt()));
            else if (value.isString()) {
                String str = value.getAsString();
                rarity = Rarity.valueOf(str.toUpperCase(Locale.US));
                if (rarity == null) Rarity.create(str, ChatFormatting.valueOf(str.toUpperCase(Locale.US)));
                if (rarity == null) Rarity.create(str, text -> text.withColor(Integer.decode(str)));
                if (rarity != null) props.rarity(rarity);
            }
        }
        boolean enchanted = obj.has("enchanted") ? obj.get("enchanted").getAsBoolean() : false;
        if (obj.has("fire_resistant") && obj.get("fire_resistant").getAsBoolean()) props.fireResistant();
        ResourceLocation creative_tab = obj.has("creative_tab") ? ResourceLocation.tryParse(obj.get("creative_tab").getAsString()) : null;
        return new AmmoItem(props, enchanted, creative_tab);
    }

}
