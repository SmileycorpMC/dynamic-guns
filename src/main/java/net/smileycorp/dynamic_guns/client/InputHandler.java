package net.smileycorp.dynamic_guns.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.smileycorp.dynamic_guns.gun.FireMode;
import net.smileycorp.dynamic_guns.item.GunItem;
import net.smileycorp.dynamic_guns.network.InputMessage;
import net.smileycorp.dynamic_guns.network.PacketHandler;

import java.awt.event.KeyEvent;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class InputHandler {

    public static KeyMapping RELOAD = new KeyMapping("key.dynamic_guns.reload", KeyEvent.VK_R, "key.category.dynamic_guns");

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(RELOAD);
    }

    @SubscribeEvent
    public void keyPressed(InputEvent.Key event) {
        if (event.getKey() == RELOAD.getKey().getValue() && event.getAction() == 1) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (stack == null || stack.isEmpty() |! (stack.getItem() instanceof GunItem)) return;
            if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                PacketHandler.NETWORK_INSTANCE.sendTo(new InputMessage(InputMessage.Action.RELOAD), player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
                RELOAD.consumeClick();
            }
        }
    }

    @SubscribeEvent
    public void mouseClicked(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (stack == null || stack.isEmpty() |! (stack.getItem() instanceof GunItem)) return;
            event.setSwingHand(false);
            event.setCanceled(true);
            if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                PacketHandler.NETWORK_INSTANCE.sendTo(new InputMessage(InputMessage.Action.PRIMARY), player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
            }
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (Minecraft.getInstance().options.keyAttack.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (stack == null || stack.isEmpty() |! (stack.getItem() instanceof GunItem)) return;
            if (!player.getCooldowns().isOnCooldown(stack.getItem()) && ((GunItem) stack.getItem()).getProperties().getFireMode() == FireMode.FULL_AUTO) {
                PacketHandler.NETWORK_INSTANCE.sendTo(new InputMessage(InputMessage.Action.PRIMARY), player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
            }
        }
    }

}
