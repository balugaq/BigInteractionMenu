package com.balugaq.bim;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class InteractListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        var unit = Util.getUnit(event.getPlayer());
        if (unit == null) return;
        unit.grid.option.interact(unit, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onScroll(PlayerItemHeldEvent event) {
        var unit = Util.getUnit(event.getPlayer());
        if (unit == null) return;

        int p = event.getPreviousSlot();
        int n = event.getNewSlot();
        if (p == 0 && n == 8 || p > n) {
            unit.grid.option.scroll(ScrollResult.DOWN, event);
        } else {
            unit.grid.option.scroll(ScrollResult.UP, event);
        }
    }
}
