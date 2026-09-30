package com.balugaq.bim;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.Range;

@Data
public abstract class GridOption {
    final String identifier;
    @Range(from = 1, to = Integer.MAX_VALUE) int height;
    @Range(from = 1, to = Integer.MAX_VALUE) int width;
    float gap;

    public GridOption(String identifier,
                      @Range(from = 1, to = Integer.MAX_VALUE) int height,
                      @Range(from = 1, to = Integer.MAX_VALUE) int width,
                      float gap) {
        this.identifier = identifier;
        this.height = height;
        this.width = width;
        this.gap = gap;
    }
    public abstract void initialize(int idx, InteractUnit unit);
    public abstract void interact(InteractUnit unit, PlayerInteractEvent event);
    public abstract void hover(InteractUnit unit, Player player);
    public abstract void scroll(ActiveGrid grid, ScrollResult result, PlayerItemHeldEvent event);
    public abstract void tick();
    public abstract int tickInterval(); // ticks
    public abstract int entriesSize();
    public boolean defaultBackground() {
        return true;
    }
}
