package com.balugaq.bim;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public abstract class ActiveGridOption extends GridOption {
    public ActiveGridOption(NamespacedKey identifier, @Range(from = 1, to = Integer.MAX_VALUE) int height, @Range(from = 1, to = Integer.MAX_VALUE) int width, float gap) {
        super(identifier, height, width, gap);
    }

    public ActiveGridOption(NamespacedKey identifier, @Range(from = 1, to = Integer.MAX_VALUE) int height, @Range(from = 1, to = Integer.MAX_VALUE) int width) {
        super(identifier, height, width);
    }

    @Override
    public void initialize(int idx, InteractUnit unit) {
        var location = unit.location;
        var o = GridOrientation.fromYawPitch(location.getYaw(), location.getPitch());
        var gap = getGap();
        unit.itemDisplay = location.getWorld().spawn(o.apply(location, gap * 0.5f, -gap * 0.5f, 0), ItemDisplay.class);
        unit.titleDisplay = location.getWorld().spawn(o.apply(location, gap * 0.5f, -gap * 0.5f + 0.03, 0.005), TextDisplay.class);
        unit.amountDisplay = location.getWorld().spawn(o.apply(location, gap - 0.02, 0.01 - gap, 0.004), TextDisplay.class);

        var stack = getItemInSlot(idx);
        unit.itemDisplay.setItemStack(stack);
        unit.itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        unit.itemDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).leftRotation(o.getItemRotation()).build());
        unit.itemDisplay.setBrightness(GridUtil.KDB);

        if (stack != null) unit.titleDisplay.text(stack.effectiveName());
        unit.titleDisplay.setDefaultBackground(false);
        unit.titleDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).build());
        unit.titleDisplay.setBackgroundColor(Color.fromARGB(0));
        unit.titleDisplay.setBillboard(o.isHorizontal() ? Display.Billboard.CENTER : Display.Billboard.FIXED);
        unit.titleDisplay.setBrightness(GridUtil.MDB);
        unit.titleDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_HIDDEN);
        unit.titleDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        if (stack != null) unit.amountDisplay.text(Component.text().color(NamedTextColor.WHITE).append(
            Component.text(GridUtil.formatAmount(stack.getAmount()))).build());
        unit.amountDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).build());
        unit.amountDisplay.setDefaultBackground(false);
        unit.amountDisplay.setBackgroundColor(Color.fromARGB(0));
        unit.amountDisplay.setBillboard(o.isHorizontal() ? Display.Billboard.CENTER : Display.Billboard.FIXED);
        unit.amountDisplay.setBrightness(GridUtil.MDB);
        unit.amountDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_HIDDEN);
    }

    @Override
    public void onHover(InteractUnit unit, Player player) {
        unit.titleDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_SHOWN);
        unit.itemDisplay.setBrightness(GridUtil.MDB);
        unit.amountDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_SHOWN);
        var old = GridDataCache.watching.put(player, unit);
        if (old != null && unit != old) {
            old.titleDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_HIDDEN);
            old.itemDisplay.setBrightness(GridUtil.KDB);
            old.amountDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_HIDDEN);
        }
    }

    @Override
    public void onScroll(ActiveGrid active, ScrollResult result, int delta, PlayerItemHeldEvent event) {
        if (event.getPlayer().isSneaking()) return;

        switch (result) {
            case UP -> {
                active.scrollOffset = Math.min(active.scrollOffset + delta, (Math.max(0, entriesSize() - getWidth() * getHeight()) + getWidth() - 1) / getWidth());
            }
            case DOWN -> {
                active.scrollOffset = Math.max(active.scrollOffset - delta, 0);
            }
        }
        active.units.forEach((i, u) -> {
            updateDisplayItem(active, i, u);
        });
    }

    @Override
    public void updateDisplayItem(ActiveGrid active, int idx, InteractUnit u) {
        var stack = getItemInSlot(idx + u.grid.scrollOffset * getWidth());
        u.itemDisplay.setItemStack(stack);
        if (stack == null) {
            u.amountDisplay.text(Component.empty());
        } else {
            u.amountDisplay.text(Component.text().color(NamedTextColor.WHITE).append(Component.text(GridUtil.formatAmount(stack.getAmount()))).build());
        }
    }

    public abstract @Nullable ItemStack getItemInSlot(int idx);
}
