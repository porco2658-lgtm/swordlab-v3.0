package com.swordlab.module;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Shows the sword in your hand as a CS-style knife with a skin. Purely visual: it only sets the item-model
 * component on your local copy of the stack, and puts it back whenever you open a screen or switch away.
 */
public class CsKnives extends Module {
    public static final String[] KNIVES = {"Bayonet", "Karambit", "Huntsman"};
    public static final String[] SKINS = {"Fade", "Doppler", "Slaughter", "Case Hardened", "Night", "Steel"};
    private static final String NS = "swordlab";

    private final Setting knife = add(Setting.mode("Knife", 1, KNIVES));
    private final Setting skin = add(Setting.mode("Skin", 0, SKINS));

    public CsKnives() {
        super("CS Knives", "Held sword looks like a CS knife skin", Category.VISUALS);
    }

    private Identifier current() {
        String k = KNIVES[knife.mode].toLowerCase();
        String s = SKINS[skin.mode].toLowerCase().replace(' ', '_');
        return Identifier.fromNamespaceAndPath(NS, "knife_" + k + "_" + s);
    }

    @Override
    public void onTick(Minecraft mc) { sync(mc, true); }

    @Override
    public void onDisable() { sync(Minecraft.getInstance(), false); }

    private void sync(Minecraft mc, boolean active) {
        if (mc.player == null) return;
        Inventory inv = mc.player.getInventory();
        Identifier want = current();
        boolean show = active && mc.screen == null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            Identifier model = s.get(DataComponents.ITEM_MODEL);
            boolean ours = model != null && NS.equals(model.getNamespace());
            boolean held = i == inv.getSelectedSlot() || i == 40;
            if (show && held && s.is(ItemTags.SWORDS)) {
                if (!want.equals(model)) s.set(DataComponents.ITEM_MODEL, want);
            } else if (ours) {
                Identifier def = s.getItem().components().get(DataComponents.ITEM_MODEL);
                if (def != null) s.set(DataComponents.ITEM_MODEL, def);
            }
        }
    }
}
