package net.smileycorp.dynamic_guns.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;
import net.smileycorp.dynamic_guns.item.AmmoItem;
import net.smileycorp.dynamic_guns.item.CreativeTabsProvider;
import net.smileycorp.dynamic_guns.item.GunItem;
import net.smileycorp.dynamic_guns.item.MeleeItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GunPack {

    private final PackInfo info;
    private final DeferredRegister<Item> register;

    private GunPack(PackInfo info) {
        this.info = info;
        register = DeferredRegister.create(ForgeRegistries.ITEMS, info.getName());
        register.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addCreative);
    }

    public PackInfo getPackInfo() {
        return info;
    }

    private void addAmmoItem(String name, JsonObject obj) {
        register.register(name, () -> AmmoItem.deserialize(obj));
    }

    private void addGunItem(String name, JsonObject obj) {
        register.register(name, () -> GunItem.deserialize(obj));
    }

    private void addMeleeItem(String name, JsonObject obj) {
        register.register(name, () -> MeleeItem.deserialize(obj));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        for (RegistryObject<Item> item : register.getEntries()) {
            if (!item.isPresent() || !(item.get() instanceof CreativeTabsProvider)) continue;
            if (((CreativeTabsProvider) item.get()).canAddToTab(event.getTabKey())) event.accept(item.get());
        }
    }

    public static GunPack fromPath(Path path, boolean from_mod) throws Exception {
        GunPack pack = new GunPack(PackInfo.deserialize(readJsonFromStream(path.resolve("pack-info.json")), path, from_mod));
        readAmmoItems(pack, path.resolve("ammo"));
        readGunItems(pack, path.resolve("guns"));
        readMeleeItems(pack, path.resolve("melee"));
        return pack;
    }

    private static JsonObject readJsonFromStream(Path path) throws Exception {
        InputStream stream = Files.newInputStream(path, StandardOpenOption.READ);
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(stream))).getAsJsonObject();
    }

    private static void readAmmoItems(GunPack pack, Path path) throws Exception {
        Files.find(path, Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile()).forEach(p -> {
            try {
                JsonObject obj = readJsonFromStream(p);
                String name = obj.get("name").getAsString();
                pack.addAmmoItem(name, obj);
                DynamicGunsLogger.logInfo("Loaded ammo item " + name);
            } catch (Exception e) {
                DynamicGunsLogger.logError("Failed to load ammo item " + p.getFileName(), e);
            }
        });
    }

    private static void readGunItems(GunPack pack, Path path) throws Exception {
        Files.find(path, Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile()).forEach(p -> {
            try {
                JsonObject obj = readJsonFromStream(p);
                String name = obj.get("name").getAsString();
                pack.addGunItem(name, obj);
                DynamicGunsLogger.logInfo("Loaded gun item " + name);
            } catch (Exception e) {
                DynamicGunsLogger.logError("Failed to load gun item " + p.getFileName(), e);
            }
        });
    }

    private static void readMeleeItems(GunPack pack, Path path) throws Exception {
        Files.find(path, Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile()).forEach(p -> {
            try {
                JsonObject obj = readJsonFromStream(p);
                String name = obj.get("name").getAsString();
                pack.addMeleeItem(name, obj);
                DynamicGunsLogger.logInfo("Loaded melee item " + name);
            } catch (Exception e) {
                DynamicGunsLogger.logError("Failed to load melee item " + p.getFileName(), e);
            }
        });
    }

}
