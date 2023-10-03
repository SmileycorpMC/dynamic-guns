package net.smileycorp.dynamic_guns.data;

import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.Path;

public class GunPack {

    public static GunPack fromPath(Path path) throws Exception {
        return new GunPack();
    }


    public static GunPack fromMod(IModFile mod) throws Exception  {
        return new GunPack();
    }
}
