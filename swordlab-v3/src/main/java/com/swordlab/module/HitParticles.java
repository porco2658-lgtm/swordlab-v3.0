package com.swordlab.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Spawns particles on the entity you hit (client-side only). */
public class HitParticles extends Module {
    private final Setting style = add(Setting.mode("Style", 0, "Crit", "Purple", "Totem", "Hearts", "Sparkle"));
    private final Setting amount = add(Setting.number("Amount", 12, 1, 40, 1));
    private final Setting spread = add(Setting.number("Spread", 0.35, 0.1, 1.0, 0.05));

    public HitParticles() {
        super("Hit Particles", "Particles when you hit something", Category.VISUALS);
    }

    @Override
    public void onTick(Minecraft mc) {}

    private SimpleParticleType type() {
        return switch (style.mode) {
            case 1 -> ParticleTypes.WITCH;
            case 2 -> ParticleTypes.TOTEM_OF_UNDYING;
            case 3 -> ParticleTypes.HEART;
            case 4 -> ParticleTypes.END_ROD;
            default -> ParticleTypes.CRIT;
        };
    }

    public void onHit(Entity e) {
        Minecraft mc = Minecraft.getInstance();
        if (!enabled || mc.level == null || !(e instanceof LivingEntity)) return;
        RandomSource r = e.getRandom();
        SimpleParticleType t = type();
        for (int i = 0; i < amount.asInt(); i++) {
            double x = e.getX() + (r.nextDouble() - 0.5) * e.getBbWidth();
            double y = e.getY() + r.nextDouble() * e.getBbHeight();
            double z = e.getZ() + (r.nextDouble() - 0.5) * e.getBbWidth();
            double s = spread.value;
            Particle part = mc.particleEngine.createParticle(t, x, y, z,
                    (r.nextDouble() - 0.5) * s, (r.nextDouble() - 0.2) * s, (r.nextDouble() - 0.5) * s);
            if (part != null) mc.particleEngine.add(part);
        }
    }
}
