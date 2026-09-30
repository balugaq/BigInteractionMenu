package com.balugaq.bim;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class ExampleGridOption extends GridOption {
    static ExampleGridOption instance = new ExampleGridOption();

    public ExampleGridOption() {
        super("example", 9, 9, 0.12f);
    }

    public void load() {
        GridOptionRegistry.registerOption(this);
    }

    @Override
    public void initialize(int idx, InteractUnit unit) {
        var location = unit.location;
        var gap = this.gap;
        unit.itemDisplay = location.getWorld().spawn(location.clone().add(gap * 0.5f, -gap * 0.5f, 0), ItemDisplay.class);
        unit.titleDisplay = location.getWorld().spawn(location.clone().add(0 + gap, 0.03 - gap, 0.05), TextDisplay.class);
        unit.amountDisplay = location.getWorld().spawn(location.clone().add(-0.02 + gap, 0.01 - gap, 0.004), TextDisplay.class);

        var stack = new ItemStack(Material.IRON_INGOT, idx + 1);
        unit.itemDisplay.setItemStack(stack);
        unit.itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        unit.itemDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).leftRotation(0f, 1f, 0f, 0f).build());
        unit.itemDisplay.setBrightness(new Display.Brightness(12, 12));
        unit.titleDisplay.text(stack.effectiveName());
        unit.titleDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        unit.titleDisplay.setDefaultBackground(false);
        unit.titleDisplay.setBackgroundColor(Color.fromARGB(0));
        unit.titleDisplay.setBillboard(Display.Billboard.VERTICAL);
        unit.titleDisplay.setBrightness(new Display.Brightness(15, 15));
        unit.titleDisplay.setTextOpacity((byte) 0);
        unit.titleDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).build());
        var s = Util.formatAmount(stack.getAmount());
        unit.amountDisplay.text(Component.text().color(NamedTextColor.WHITE).append(Component.text(s)).build());
        unit.amountDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).build());
        unit.amountDisplay.setDefaultBackground(false);
        unit.amountDisplay.setBackgroundColor(Color.fromARGB(0));
        unit.amountDisplay.setBillboard(Display.Billboard.VERTICAL);
        unit.amountDisplay.setBrightness(new Display.Brightness(15, 15));
    }

    @Override
    public void interact(InteractUnit unit, PlayerInteractEvent event) {
        event.getPlayer().sendMessage("Performed " + event.getAction() + " at idx:" + unit.getIdx());
        event.setCancelled(true);
    }

    @Override
    public void hover(InteractUnit unit, Player player) {
        unit.titleDisplay.setTextOpacity((byte) 1);
        unit.itemDisplay.setBrightness(new Display.Brightness(15, 15));
        var old = GridDataCache.watching.put(player, Pair.of(unit.titleDisplay, unit.itemDisplay));
        if (old != null && unit.titleDisplay != old.left()) {
            old.left().setTextOpacity((byte) 0);
            old.right().setBrightness(new Display.Brightness(12, 12));
        }
    }

    @Override
    public void scroll(ActiveGrid grid, ScrollResult result, PlayerItemHeldEvent event) {
        if (event.getPlayer().isSneaking()) {
            return;
        }
        event.getPlayer().sendMessage("Performed scroll " + result);
        event.setCancelled(true);
        switch (result) {
            case UP -> {
                grid.scrollOffset = Math.min(grid.scrollOffset + 1, (Math.max(0, entriesSize() - width * height) + width - 1) / width);
            }
            case DOWN -> {
                grid.scrollOffset = Math.max(grid.scrollOffset - 1, 0);
            }
        }
        grid.units.forEach((i, u) -> {
            var stack = u.itemDisplay.getItemStack();
            int amt = i + grid.scrollOffset * width;
            stack.setAmount(amt);
            u.itemDisplay.setItemStack(stack);
            u.amountDisplay.text(Component.text().color(NamedTextColor.WHITE).append(Component.text(Util.formatAmount(amt))).build());
        });
    }

    @Override
    public void tick() {

    }

    @Override
    public int tickInterval() {
        return 1;
    }

    @Override
    public int entriesSize() {
        return 92;
    }
}
