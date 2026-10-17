package com.swordlab.module;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

/** Key helpers. If a Minecraft update changes the InputConstants call, fix it here only. */
public final class Keys {
    private Keys() {}

    public static boolean down(int key) {
        if (key < 0) return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
    }

    /**
     * Hotbar index 0-8 whose in-game keybind (Options > Controls > Hotbar) is this key, or -1.
     * Works with rebound hotbar keys, e.g. slot 3 bound to R.
     */
    public static int slot(int key) {
        if (key < 0) return -1;
        Minecraft mc = Minecraft.getInstance();
        String name = InputConstants.Type.KEYSYM.getOrCreate(key).getName();
        for (int i = 0; i < 9; i++) {
            if (mc.options.keyHotbarSlots[i].saveString().equals(name)) return i;
        }
        return -1;
    }

    public static String name(int key) {
        return key < 0 ? "None" : InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
    }
}
