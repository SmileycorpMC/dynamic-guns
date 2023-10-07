package net.smileycorp.dynamic_guns.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.smileycorp.dynamic_guns.gun.GunAttribute;
import net.smileycorp.dynamic_guns.gun.GunProperties;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Locale;


public class GunItem extends JsonItem implements GeoItem {

    private static final RawAnimation RELOAD_ANIM = RawAnimation.begin().thenPlay("reload");

    private final GunProperties properties;

    private GunItem(Properties props, boolean enchanted, ResourceLocation creative_tab, GunProperties gun_props) {
        super(props, enchanted, creative_tab);
        this.properties = gun_props;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public GunProperties getProperties() {
        return properties;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return properties.getMagSize() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(((float) getAmmoCount(stack) / (float) properties.getMagSize()) * 13.0F );
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack old, ItemStack stack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> text, TooltipFlag additional) {
        text.add(Component.translatable("tooltip.dynamic_guns.gun.ammo_count", getAmmoCount(stack), properties.getMagSize()).withStyle(ChatFormatting.YELLOW));
        text.add(Component.translatable("tooltip.dynamic_guns.gun.ammo_type", properties.getAmmoName()).withStyle(ChatFormatting.AQUA));
        for (GunAttribute attribute : properties.getAttributes()) text.add(attribute.getHoverText());
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player && ((Player)entity).getCooldowns().isOnCooldown(this)) return false;
        fire(stack, entity.level(), entity);
        return false;
    }

    public void reload(ItemStack stack, Level level, LivingEntity entity) {
        if (! (level instanceof ServerLevel)) return;
        if (!(entity instanceof Player) || ((Player) entity).isCreative()) setAmmoCount(stack, properties.getMagSize());
        Inventory inventory = ((Player)entity).getInventory();
        int space = properties.getMagSize() - getAmmoCount(stack);
        int to_load = 0;
        for(int i = 0; i < inventory.getContainerSize(); ++i) {
            if (to_load >= space) break;
            ItemStack item = inventory.getItem(i);
            if (properties.isAmmo(item)) {
                if (item.isDamageableItem()) {
                    int count = Math.min(item.getMaxDamage() - item.getDamageValue(), space - to_load);
                    item.hurt(count, entity.getRandom(), entity instanceof ServerPlayer ? (ServerPlayer) entity : null);
                    to_load += count;
                } else {
                    int count = Math.min(item.getCount(), space - to_load);
                    item.shrink(count);
                    to_load += count;
                }
            }
        }
        if (to_load > 0) {
            entity.playSound(properties.getReloadSound());
            setAmmoCount(stack, getAmmoCount(stack) + to_load);
            if (entity instanceof Player) ((Player) entity).getCooldowns().addCooldown(this, properties.getReloadSpeed());
            triggerAnim(entity, GeoItem.getOrAssignId(stack, (ServerLevel) level), "Gun", "reload");
        }
    }

    public void fire(ItemStack stack, Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        int ammo = getAmmoCount(stack);
        if (entity instanceof Player) ((Player) entity).getCooldowns().addCooldown(this, properties.getFireRate());
        if (ammo <= 0) {
            entity.playSound(properties.getEmptySound());
            return;
        }
        if (properties.getMagSize() > 0 && (!(entity instanceof Player) || !((Player) entity).isCreative())) setAmmoCount(stack, ammo - 1);
        properties.createProjectile(level, entity);
        entity.playSound(properties.getFireSound());
    }

    public static int getAmmoCount(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getTag() == null |! (stack.getItem() instanceof GunItem)) return 0;
        GunProperties props = ((GunItem) stack.getItem()).getProperties();
        CompoundTag tag = stack.getTag();
        if (!tag.contains("ammo")) tag.putInt("ammo", 0);
        if (tag.getInt("ammo") > props.getMagSize()) tag.putInt("ammo", props.getMagSize());
        return tag.getInt("ammo");
    }

    public static void setAmmoCount(ItemStack stack, int count) {
        if (stack == null || stack.isEmpty() |! (stack.getItem() instanceof GunItem)) return;
        GunProperties props = ((GunItem) stack.getItem()).getProperties();
        stack.getOrCreateTag().putInt("ammo", count);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registry) {
        registry.add(new AnimationController<>(this, "Gun", 0, state -> PlayState.STOP)
                .triggerableAnim("reload", RELOAD_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return GeckoLibUtil.createInstanceCache(this);
    }

    public static GunItem deserialize(JsonObject obj) {
        Properties props = new Properties().setNoRepair().stacksTo(1);
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
        return new GunItem(props, enchanted, creative_tab, GunProperties.deserialize(obj));
    }

}
