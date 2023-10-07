package net.smileycorp.dynamic_guns.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.smileycorp.dynamic_guns.entity.DynamicGunsProjectile;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Projectile.class)
public class MixinProjectile implements DynamicGunsProjectile {

    private float damage = 1;

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
    }

}
