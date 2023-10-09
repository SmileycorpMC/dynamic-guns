package net.smileycorp.dynamic_guns.client;

import net.smileycorp.dynamic_guns.item.GunItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GunItemRenderer extends GeoItemRenderer<GunItem> {

    public GunItemRenderer() {
        super(new GunModel());
    }

}
