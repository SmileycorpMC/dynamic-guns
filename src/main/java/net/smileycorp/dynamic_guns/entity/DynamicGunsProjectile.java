package net.smileycorp.dynamic_guns.entity;

import net.minecraft.world.entity.Entity;
import net.smileycorp.dynamic_guns.gun.DamageFalloff;

public interface DynamicGunsProjectile {

    void setBaseDamage(float damage);

    float getBaseDamage();

    boolean isCrit(Entity target);

    void setCritMultiplier(float multiplier);

    float getCritMultiplier(Entity target);

    float getDistance();

    void setFalloff(DamageFalloff falloff);

    DamageFalloff getFalloff();

    default float getDamage(Entity target) {
        float damage = getBaseDamage();
        DamageFalloff falloff = getFalloff();
        float distance = getDistance();
        float start = falloff.getStart();
        float end = falloff.getEnd();
        if (distance > start && (end < 0 || distance < end)) {
            float amount = falloff.getAmount();
            switch (falloff.getType()) {
                case LINEAR -> damage *= (1 - (distance - start) / (end - start)) * amount;
                case EXPONENTIAL -> damage *= Math.pow(amount, distance - start);
            }
        }
        if (isCrit(target)) damage *= getCritMultiplier(target);
        return damage;
    }

}
