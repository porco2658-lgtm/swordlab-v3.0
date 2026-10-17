package com.swordlab.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

/**
 * "Lite" motion blur: soft purple streaks fade in from the screen edges while you turn quickly.
 * (A real full-screen blur needs a post-processing pass, which can't be done safely on 1.21.11 without testing.)
 */
public class MotionBlur extends Module {
    private final Setting strength = add(Setting.number("Strength", 0.6, 0.1, 1.0, 0.05));
    private final Setting width = add(Setting.number("Width", 22, 6, 40, 1));
    private float lastYaw, lastPitch, speed;
    private boolean has;

    public MotionBlur() {
        super("Motion Blur", "Edge streaks while turning (lite blur)", Category.VISUALS);
    }

    @Override
    public void onDisable() { has = false; speed = 0; }

    @Override
    public void onTick(Minecraft mc) {}

    public void render(GuiGraphics g, Minecraft mc) {
        LocalPlayer p = mc.player;
        if (!enabled || p == null || mc.options.hideGui) { has = false; return; }
        float yaw = p.getYRot(), pitch = p.getXRot();
        if (has) {
            float d = Math.abs(net.minecraft.util.Mth.wrapDegrees(yaw - lastYaw)) + Math.abs(pitch - lastPitch);
            speed += (d - speed) * 0.35f;
        }
        lastYaw = yaw; lastPitch = pitch; has = true;

        float k = Math.min(1f, speed / 14f) * (float) strength.value;
        if (k < 0.02f) return;
        int w = g.guiWidth(), h = g.guiHeight();
        int strips = width.asInt();
        float strip = (w * 0.22f) / strips;
        for (int i = 0; i < strips; i++) {
            float t = 1f - (float) i / strips;
            int a = (int) (k * t * t * 150f);
            if (a <= 0) continue;
            int col = (a << 24) | 0x9B4DFF;
            int off = Math.round(i * strip), off2 = Math.round((i + 1) * strip);
            g.fill(off, 0, off2, h, col);
            g.fill(w - off2, 0, w - off, h, col);
        }
    }
}
