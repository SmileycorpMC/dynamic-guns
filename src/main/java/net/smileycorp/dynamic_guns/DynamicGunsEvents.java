package net.smileycorp.dynamic_guns;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.smileycorp.dynamic_guns.entity.DynamicGunsProjectile;

public class DynamicGunsEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDamage(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;
        DamageSource source = event.getSource();
        Entity attacker = source.getDirectEntity();
        if (attacker instanceof DynamicGunsProjectile) event.setAmount(((DynamicGunsProjectile) attacker).getDamage(entity));
    }

}
