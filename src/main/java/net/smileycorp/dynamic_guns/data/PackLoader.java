package net.smileycorp.dynamic_guns.data;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.locating.IModFile;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;

import java.io.File;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class PackLoader {

    public static final File GUNS_FOLDER = FMLPaths.GAMEDIR.get().resolve("guns").toFile();
    private static final List<GunPack> loaded_packs = Lists.newArrayList();

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
            if (!path.resolve("pack-info.json").toFile().isFile()) return;
            loaded_packs.add(GunPack.fromPath(path));
            DynamicGunsLogger.logInfo("Loaded pack " + path.getFileName());
        } catch (Exception e) {
            DynamicGunsLogger.logError("Failed to load pack " + path.getFileName(), e);
        }
    }

    private static void tryLoadPack(IModFile mod) {
        try {
            PushbackInputStream stream = new PushbackInputStream(Files.newInputStream(mod.findResource("gunpack/pack-info.json"), StandardOpenOption.READ));
            stream.unread(stream.read());
        } catch (Exception e) { return; }
        try {
            loaded_packs.add(GunPack.fromMod(mod));
            DynamicGunsLogger.logInfo("Loaded pack " + mod.getFileName());
        } catch (Exception e) {
            DynamicGunsLogger.logError("Failed to load pack " + mod.getFileName(), e);
        }
    }

}
