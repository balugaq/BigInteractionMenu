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

    // debug-adjustable via /bim w|h|gap|facing
    int debugWidth = 9;
    int debugHeight = 9;
    float debugGap = 0.12f;
    GridOrientation debugOrientation = GridOrientation.XY;

    public ExampleGridOption() {
        super("example", 9, 9, 0.12f, GridOrientation.XY);
    }

    public void load() {
        GridOptionRegistry.registerOption(this);
    }

    @Override
    public int getWidth() {
        return debugWidth;
    }

    @Override
    public int getHeight() {
        return debugHeight;
    }

    @Override
    public float getGap() {
        return debugGap;
    }

    @Override
    public GridOrientation getOrientation() {
        return debugOrientation;
    }

    public void setDebugWidth(int width) {
        this.debugWidth = Math.max(1, width);
    }

    public void setDebugHeight(int height) {
        this.debugHeight = Math.max(1, height);
    }

    public void setDebugGap(float gap) {
        this.debugGap = Math.max(0.01f, gap);
    }

    public void setDebugOrientation(GridOrientation orientation) {
        this.debugOrientation = orientation;
    }

    @Override
    public void initialize(int idx, InteractUnit unit) {
        var location = unit.location;
        var o = getOrientation();
        var gap = getGap();
        unit.itemDisplay = location.getWorld().spawn(o.apply(location, gap * 0.5f, -gap * 0.5f, 0), ItemDisplay.class);
        unit.titleDisplay = location.getWorld().spawn(o.apply(location, gap, 0.03 - gap, 0.1), TextDisplay.class);
        unit.amountDisplay = location.getWorld().spawn(o.apply(location, gap - 0.02, 0.01 - gap, 0.004), TextDisplay.class);

        var stack = new ItemStack(Material.IRON_INGOT, idx + 1);
        unit.itemDisplay.setItemStack(stack);
        unit.itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        unit.itemDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).leftRotation(o.getItemRotation()).build());
        unit.itemDisplay.setBrightness(Util.KDB);
        unit.titleDisplay.text(stack.effectiveName());
        unit.titleDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        unit.titleDisplay.setDefaultBackground(false);
        unit.titleDisplay.setBackgroundColor(Color.fromARGB(0));
        unit.titleDisplay.setBillboard(o.isHorizontal() ? Display.Billboard.CENTER : Display.Billboard.VERTICAL);
        unit.titleDisplay.setBrightness(Util.MDB);
//        unit.titleDisplay.setTextOpacity((byte) 0);
        unit.titleDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).build());
        var s = Util.formatAmount(stack.getAmount());
        unit.amountDisplay.text(Component.text().color(NamedTextColor.WHITE).append(Component.text(s)).build());
        unit.amountDisplay.setTransformation(TransformationBuilder.create().scale(0.1f).build());
        unit.amountDisplay.setDefaultBackground(false);
        unit.amountDisplay.setBackgroundColor(Color.fromARGB(0));
        unit.amountDisplay.setBillboard(o.isHorizontal() ? Display.Billboard.CENTER : Display.Billboard.VERTICAL);
        unit.amountDisplay.setBrightness(Util.MDB);
    }

    @Override
    public void interact(InteractUnit unit, PlayerInteractEvent event) {
        event.getPlayer().sendMessage("Performed " + event.getAction() + " at idx:" + unit.getIdx());
        event.setCancelled(true);
    }

    @Override
    public void hover(InteractUnit unit, Player player) {
        unit.titleDisplay.setTextOpacity((byte) 1);
        unit.itemDisplay.setBrightness(Util.MDB);
        var old = GridDataCache.watching.put(player, unit);
        if (old != null && unit != old) {
            old.titleDisplay.setTextOpacity((byte) 0);
            old.itemDisplay.setBrightness(Util.KDB);
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
                grid.scrollOffset = Math.min(grid.scrollOffset + 1, (Math.max(0, entriesSize() - getWidth() * getHeight()) + getWidth() - 1) / getWidth());
            }
            case DOWN -> {
                grid.scrollOffset = Math.max(grid.scrollOffset - 1, 0);
            }
        }
        grid.units.forEach((i, u) -> {
            var stack = u.itemDisplay.getItemStack();
            int amt = i + grid.scrollOffset * getWidth() + 1;
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
