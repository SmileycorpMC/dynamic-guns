package net.smileycorp.dynamic_guns.data;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.locating.IModFile;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;

import java.io.File;
import java.io.PushbackInputStream;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

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
        ModList.get().forEachModFile(PackLoader::tryLoadPack);
    }

    private static void tryLoadPack(Path path) {
        DynamicGunsLogger.logInfo("Trying to load pack " + path.getFileName());
        if (path.toFile().isFile()) {
            try (FileSystem zip = FileSystems.newFileSystem(path,  Collections.emptyMap())){
                try {
                    //check to see if file exists in zip
                    PushbackInputStream stream = new PushbackInputStream(Files.newInputStream(zip.getPath("pack-info.json"), StandardOpenOption.READ));
                    stream.unread(stream.read());
                } catch (Exception e) {
                    DynamicGunsLogger.logError("Failed to load pack ", e);
                    return;
                }
                GunPack pack = GunPack.fromPath(zip.getPath("/"), path, true);
                loaded_packs.put(pack.getPackInfo().getName(), pack);
                DynamicGunsLogger.logInfo("Loaded pack " + pack.getPackInfo().toString());
            } catch (Exception e) {
                DynamicGunsLogger.logError("Failed to load pack " + path.getFileName(), e);
            }
        } else {
            try {
                if (!path.resolve("pack-info.json").toFile().isFile()) return;
                GunPack pack = GunPack.fromPath(path, path, false);
                loaded_packs.put(pack.getPackInfo().getName(), pack);
                DynamicGunsLogger.logInfo("Loaded pack " + pack.getPackInfo().toString());
            } catch (Exception e) {
                DynamicGunsLogger.logError("Failed to load pack " + path.getFileName(), e);
            }
        }
    }

    private static void tryLoadPack(IModFile mod) {
        if (!Files.isDirectory(mod.findResource("gunpack"))) return;
        try {
            DynamicGunsLogger.logInfo("Trying to load pack " + mod.getFileName());
            GunPack pack = GunPack.fromPath(mod.findResource("gunpack"), mod.getFilePath(), true);
            loaded_packs.put(pack.getPackInfo().getName(), pack);
            DynamicGunsLogger.logInfo("Loaded pack " + pack.getPackInfo().toString());
        } catch (Exception e) {
            DynamicGunsLogger.logError("Failed to load pack " + mod.getFileName(), e);
        }
    }

    public static Stream<GunPack> getLoadedPacks() {
        return loaded_packs.values().stream();
    }

}
