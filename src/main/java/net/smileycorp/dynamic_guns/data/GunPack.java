package net.smileycorp.dynamic_guns.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;
import net.smileycorp.dynamic_guns.item.GunItem;
import net.smileycorp.dynamic_guns.item.JsonItem;
import net.smileycorp.dynamic_guns.item.MeleeItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

public class GunPack {

    private final PackInfo info;
    private final DeferredRegister<Item> register;

    private GunPack(PackInfo info, Path resources) throws Exception {
        this.info = info;
        register = DeferredRegister.create(ForgeRegistries.ITEMS, info.getName());
        register.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addCreative);
        readItems(resources.resolve("items"), JsonItem::deserialize);
        readItems(resources.resolve("guns"), GunItem::deserialize);
        readItems(resources.resolve("melee"), MeleeItem::deserialize);
    }

    public PackInfo getPackInfo() {
        return info;
    }

    private void readItems(Path path, Function<JsonObject, Item> factory) throws Exception {
        Files.find(path, Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile()).forEach(p -> {
            try {
                InputStream stream = Files.newInputStream(p, StandardOpenOption.READ);
                JsonObject obj = JsonParser.parseReader(new BufferedReader(new InputStreamReader(stream))).getAsJsonObject();
                String name = obj.get("name").getAsString();
                register.register(name, () -> factory.apply(obj));
                DynamicGunsLogger.logInfo("Loaded item " + name);
            } catch (Exception e) {
                DynamicGunsLogger.logError("Failed to load item " + p.getFileName(), e);
            }
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        for (RegistryObject<Item> item : register.getEntries()) {
            if (!item.isPresent() || !(item.get() instanceof JsonItem)) continue;
            if (((JsonItem) item.get()).canAddToTab(event.getTabKey())) {
                if (item.get() instanceof GunItem) {
                    ItemStack stack = new ItemStack(item.get());
                    CompoundTag nbt = new CompoundTag();
                    nbt.putInt("ammo", ((GunItem) item.get()).getProperties().getMagSize());
                    stack.setTag(nbt);
                    event.accept(stack);
                }
                else event.accept(item.get());
            }
        }
    }

    public static GunPack fromPath(Path resources, Path location, boolean is_archive) throws Exception {
        InputStream stream = Files.newInputStream(resources.resolve("pack-info.json"), StandardOpenOption.READ);
        return new GunPack(PackInfo.deserialize(JsonParser.parseReader(new BufferedReader(new InputStreamReader(stream))).getAsJsonObject(),
                location, is_archive), resources);
    }

    public static GunPack fromMod(Path resources, IModFile file) throws Exception {
        return new GunPack(PackInfo.forMod(file), resources);
    }

}
