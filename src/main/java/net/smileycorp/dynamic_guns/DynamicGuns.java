package net.smileycorp.dynamic_guns;

import net.minecraftforge.fml.common.Mod;
import net.smileycorp.dynamic_guns.data.PackLoader;

@Mod(value = Constants.MODID)
public class DynamicGuns {


    public DynamicGuns() {
        DynamicGunsLogger.clearLog();
        PackLoader.reload();
    }


}
