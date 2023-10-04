package net.smileycorp.dynamic_guns.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public interface CreativeTabsProvider {

    boolean canAddToTab(ResourceKey<CreativeModeTab> tab);

}
