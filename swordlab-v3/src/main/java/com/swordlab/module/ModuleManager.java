package com.swordlab.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public final class ModuleManager {
    public static final List<Module> ALL = new ArrayList<>();
    public static Indicators INDICATORS;
    public static SwingAnimation SWING;
    public static Viewmodel VIEWMODEL;
    public static CustomSky SKY;
    public static MotionBlur MOTION_BLUR;
    public static HitParticles HIT_PARTICLES;

    private ModuleManager() {}

    private static <T extends Module> T add(T m) { ALL.add(m); return m; }

    public static void init() {
        // Sword
        add(new Reach());
        add(new TriggerBot());
        add(new SilentAim());
        add(new JumpReset());
        // Crystal
        add(new AnchorMacro());
        add(new AutoCrystal());
        add(new AutoTotem());
        add(new AutoInvTotem());
        // Visuals
        add(new Aura());
        INDICATORS = add(new Indicators());
        SWING = add(new SwingAnimation());
        VIEWMODEL = add(new Viewmodel());
        SKY = add(new CustomSky());
        add(new Fullbright());
        add(new CustomFov());
        MOTION_BLUR = add(new MotionBlur());
        HIT_PARTICLES = add(new HitParticles());
        add(new Trajectories());
        add(new CsKnives());
    }

    public static List<Module> in(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) out.add(m);
        return out;
    }

    public static void tick(Minecraft mc) {
        for (Module m : ALL) if (m.enabled) m.onTick(mc);
    }
}
