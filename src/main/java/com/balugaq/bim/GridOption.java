package com.balugaq.bim;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.Range;

@Data
@RequiredArgsConstructor
public abstract class GridOption {
    final String identifier;
    final @Range(from = 1, to = Integer.MAX_VALUE) int height;
    final @Range(from = 1, to = Integer.MAX_VALUE) int width;
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
