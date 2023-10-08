package net.smileycorp.dynamic_guns.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.smileycorp.atlas.api.network.AbstractMessage;
import net.smileycorp.atlas.api.util.Func;
import net.smileycorp.dynamic_guns.item.GunItem;

public class InputMessage extends AbstractMessage {

    private Action action;

    public InputMessage() {}

    public InputMessage(Action action) {
        this.action = action;
    }

    @Override
    public void read(FriendlyByteBuf buf) {
       action = Action.values()[buf.readByte()];
    }

    @Override
    public void write(FriendlyByteBuf buf) {
       if (action != null) buf.writeByte(action.ordinal());
    }

    @Override
    public void handle(PacketListener handler) {}

    @Override
    public void process(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (stack == null || stack.isEmpty() |! (stack.getItem() instanceof GunItem)) return;
            GunItem item = (GunItem) stack.getItem();
            switch (action) {
                case PRIMARY -> item.fire(stack, player.level(), player);
                case SECONDARY -> Func.Void();
                case RELOAD -> item.reload(stack, player.level(), player);
            }
        });
        ctx.setPacketHandled(true);
    }

    public enum Action {
        PRIMARY,
        SECONDARY,
        RELOAD;
    }
}
