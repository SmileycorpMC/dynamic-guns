package net.smileycorp.dynamic_guns;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.smileycorp.dynamic_guns.client.InputHandler;
import net.smileycorp.dynamic_guns.data.PackLoader;
import net.smileycorp.dynamic_guns.network.PacketHandler;

@Mod(value = Constants.MODID)
@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DynamicGuns {

    public DynamicGuns() {
        DynamicGunsLogger.clearLog();
        PackLoader.reload();
        PacketHandler.initPackets();
    }

    @SubscribeEvent
    public static void constructMod(FMLConstructModEvent event) {
        MinecraftForge.EVENT_BUS.register(new DynamicGunsEvents());
    }

    @SubscribeEvent
    public static void loadClient(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new InputHandler());
    }

}
