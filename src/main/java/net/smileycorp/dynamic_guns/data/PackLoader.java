package net.smileycorp.dynamic_guns.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.locating.IModFile;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;

import java.io.File;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class PackLoader {

    public static final File GUNS_FOLDER = FMLPaths.GAMEDIR.get().resolve("guns").toFile();
    private static final Map<String, GunPack> loaded_packs = Maps.newHashMap();

    public static void reload() {
        if (!GUNS_FOLDER.isDirectory()) {
            GUNS_FOLDER.mkdirs();
        }
        loadPacks();
    }

    public static void loadPacks() {
        loaded_packs.clear();
        for(String dir : GUNS_FOLDER.list()) tryLoadPack(GUNS_FOLDER.toPath().resolve(dir));
        ModList.get().forEachModFile(file -> tryLoadPack(file));
    }

    private static void tryLoadPack(Path path) {
        try {
            DynamicGunsLogger.logInfo("Trying to load pack " + path.getFileName());
            if (!path.resolve("pack-info.json").toFile().isFile()) return;
            GunPack pack = GunPack.fromPath(path, false);
            loaded_packs.put(pack.getPackInfo().getName(), pack);
            DynamicGunsLogger.logInfo("Loaded pack " + pack.getPackInfo().toString());
        } catch (Exception e) {
            DynamicGunsLogger.logError("Failed to load pack " + path.getFileName(), e);
        }
    }

    private static void tryLoadPack(IModFile mod) {
        try {
            //check to see if file exists in jar
            PushbackInputStream stream = new PushbackInputStream(Files.newInputStream(mod.findResource("gunpack/pack-info.json"), StandardOpenOption.READ));
            stream.unread(stream.read());
        } catch (Exception e) { return; }
        try {
            DynamicGunsLogger.logInfo("Trying to load pack " + mod.getFileName());
            GunPack pack = GunPack.fromPath(mod.findResource("gunpack"), true);
            loaded_packs.put(pack.getPackInfo().getName(), pack);
            DynamicGunsLogger.logInfo("Loaded pack " + pack.getPackInfo().toString());
        } catch (Exception e) {
            DynamicGunsLogger.logError("Failed to load pack " + mod.getFileName(), e);
        }
    }

}
