package com.balugaq.bim.grid;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Data
public abstract class GridOption {
    public static final float DEFAULT_GAP = 0.12f;
    final NamespacedKey identifier;
    @Range(from = 1, to = Integer.MAX_VALUE) int height;
    @Range(from = 1, to = Integer.MAX_VALUE) int width;
    float gap;

    public GridOption(NamespacedKey identifier,
                      @Range(from = 1, to = Integer.MAX_VALUE) int height,
                      @Range(from = 1, to = Integer.MAX_VALUE) int width) {
        this(identifier, height, width, DEFAULT_GAP);
    }

    public GridOption(NamespacedKey identifier,
                      @Range(from = 1, to = Integer.MAX_VALUE) int height,
                      @Range(from = 1, to = Integer.MAX_VALUE) int width,
                      float gap) {
        this.identifier = identifier;
        this.height = height;
        this.width = width;
        this.gap = gap;
        GridOptionRegistry.registerOption(this);
    }

    public void place(Location location) {
        GridUtil.placeGrid(location, this);
    }

    public abstract void init(int idx, InteractUnit unit);

    public abstract void onHover(InteractUnit unit, Player player);

    public abstract void offHover(InteractUnit unit, Player player);

    public abstract void onScroll(ActiveGrid active, ScrollResult result, int delta, PlayerItemHeldEvent event);

    public abstract void updateDisplayItem(ActiveGrid active, int idx, InteractUnit u);
    public abstract void tick();
    public abstract int tickInterval(); // ticks
    public abstract int entriesSize();
    public boolean defaultBackground() {
        return true;
    }
}
