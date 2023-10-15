package net.smileycorp.dynamic_guns.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.smileycorp.dynamic_guns.entity.DynamicGunsProjectile;
import net.smileycorp.dynamic_guns.gun.DamageFalloff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class MixinProjectile extends Entity implements DynamicGunsProjectile {


    public MixinProjectile(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    private float damage = 1;
    private float crit_multiplier = 1;
    private DamageFalloff falloff = DamageFalloff.DEFAULT;

    @Shadow @Nullable public abstract Entity getOwner();

    @Override
    public void setBaseDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public float getBaseDamage() {
        return damage;
    }

    @Override
    public boolean isCrit(Entity target) {
        return Math.abs(getY() - target.getY()) < target.getBbHeight() / 10;
    }

    @Override
    public void setCritMultiplier(float multiplier) {
        this.crit_multiplier = multiplier;
    }

    @Override
    public float getCritMultiplier(Entity target) {
        return crit_multiplier;
    }

    @Override
    public float getDistance() {
        return distanceTo(getOwner());
    }

    @Override
    public void setFalloff(DamageFalloff falloff) {
        this.falloff = falloff;
    }

   public DamageFalloff getFalloff() {
        return falloff;
   }

}
