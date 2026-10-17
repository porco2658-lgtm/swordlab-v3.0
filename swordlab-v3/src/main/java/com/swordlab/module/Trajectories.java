package com.swordlab.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Shows the predicted flight path of what you're holding (bow, crossbow, pearl, snowball, trident...) as particles. */
public class Trajectories extends Module {
    private final Setting steps = add(Setting.number("Max steps", 80, 20, 200, 5));
    private final Setting spacing = add(Setting.number("Dot spacing", 2, 1, 5, 1));
    private final Setting landing = add(Setting.bool("Landing marker", true));

    public Trajectories() {
        super("Trajectories", "Predicts the path of bows, pearls, throwables", Category.VISUALS);
    }

    /** speed, gravity, drag; null if the item isn't a projectile. */
    private double[] params(LocalPlayer p, ItemStack s) {
        if (s.is(Items.BOW)) {
            float f = p.getTicksUsingItem() / 20f;
            f = (f * f + f * 2f) / 3f;
            if (p.getUseItem() != s || f < 0.1f) return null;
            return new double[] {Math.min(f, 1f) * 3.0, 0.05, 0.99};
        }
        if (s.is(Items.CROSSBOW)) return new double[] {3.15, 0.05, 0.99};
        if (s.is(Items.TRIDENT)) return new double[] {2.5, 0.05, 0.99};
        if (s.is(Items.ENDER_PEARL) || s.is(Items.SNOWBALL) || s.is(Items.EGG)) return new double[] {1.5, 0.03, 0.99};
        if (s.is(Items.SPLASH_POTION) || s.is(Items.LINGERING_POTION)) return new double[] {0.5, 0.05, 0.99};
        if (s.is(Items.EXPERIENCE_BOTTLE)) return new double[] {0.7, 0.07, 0.99};
        return null;
    }

    @Override
    public void onTick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null || mc.screen != null) return;
        double[] pr = params(p, p.getMainHandItem());
        if (pr == null) pr = params(p, p.getOffhandItem());
        if (pr == null) return;

        Vec3 look = p.getLookAngle();
        Vec3 pos = p.getEyePosition().add(look.scale(0.3)).add(0, -0.1, 0);
        Vec3 vel = look.scale(pr[0]);
        int sp = spacing.asInt();
        for (int i = 0; i < steps.asInt(); i++) {
            Vec3 next = pos.add(vel);
            var hit = mc.level.clip(new ClipContext(pos, next, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
            boolean end = hit.getType() != HitResult.Type.MISS;
            if (end) next = hit.getLocation();
            if (i % sp == 0) dot(mc, next);
            if (end) {
                if (landing.bool) ring(mc, next);
                return;
            }
            pos = next;
            vel = vel.scale(pr[2]).add(0, -pr[1], 0);
            if (pos.y < -128) return;
        }
    }

    private void dot(Minecraft mc, Vec3 v) {
        Particle part = mc.particleEngine.createParticle(ParticleTypes.WITCH, v.x, v.y, v.z, 0, 0, 0);
        if (part != null) mc.particleEngine.add(part);
    }

    private void ring(Minecraft mc, Vec3 c) {
        for (int i = 0; i < 12; i++) {
            double a = i * Math.PI * 2 / 12;
            Particle part = mc.particleEngine.createParticle(ParticleTypes.END_ROD,
                    c.x + Math.cos(a) * 0.35, c.y + 0.05, c.z + Math.sin(a) * 0.35, 0, 0, 0);
            if (part != null) mc.particleEngine.add(part);
        }
    }
}
