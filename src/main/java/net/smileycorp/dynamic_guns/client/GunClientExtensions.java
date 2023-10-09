package net.smileycorp.dynamic_guns.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class GunClientExtensions implements IClientItemExtensions {

    private GunItemRenderer renderer = null;
    // Don't instantiate until ready. This prevents race conditions breaking things
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) renderer = new GunItemRenderer();
        return renderer;
    }

    @Nullable
    public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return hand == InteractionHand.MAIN_HAND ? HumanoidModel.ArmPose.BOW_AND_ARROW : null;
    }

}
