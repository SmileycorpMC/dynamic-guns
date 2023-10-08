package net.smileycorp.dynamic_guns.network;

import net.minecraftforge.network.simple.SimpleChannel;
import net.smileycorp.atlas.api.network.NetworkUtils;
import net.smileycorp.dynamic_guns.Constants;

public class PacketHandler {

    public static SimpleChannel NETWORK_INSTANCE;

    public static void initPackets() {
        NETWORK_INSTANCE = NetworkUtils.createChannel(Constants.loc("main"));
        NetworkUtils.registerMessage(NETWORK_INSTANCE,0, InputMessage.class);
    }

}
