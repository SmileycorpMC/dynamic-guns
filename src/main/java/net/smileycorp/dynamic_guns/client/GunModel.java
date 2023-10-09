package net.smileycorp.dynamic_guns.client;

import net.minecraft.resources.ResourceLocation;
import net.smileycorp.dynamic_guns.item.GunItem;
import software.bernie.geckolib.model.GeoModel;

public class GunModel extends GeoModel<GunItem> {

    @Override
    public ResourceLocation getModelResource(GunItem item) {
        return format(item.getProperties().getLocation(), "geo/", ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GunItem item) {
        return format(item.getProperties().getLocation(), "textures/items/", ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GunItem item) {
        return format(item.getProperties().getLocation(), "animations/", ".json");
    }

    private static ResourceLocation format(ResourceLocation location, String prefix, String suffix) {
        return new ResourceLocation(location.getNamespace(), prefix + location.getPath() + suffix );
    }

}
