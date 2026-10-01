package com.balugaq.bim;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class MenuListener implements Listener {
    @EventHandler(ignoreCancelled = false) // Allow listen to click air
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        var unit = GridUtil.rayTraceUnit(event.getPlayer());
        if (unit == null) return;
        unit.grid.clickHandlers.getOrDefault(unit.idx, ClickHandler.cancel).onClick(new ClickDTO(event, unit));
    }

    @EventHandler(ignoreCancelled = true)
    public void onScroll(PlayerItemHeldEvent event) {
        var unit = GridUtil.rayTraceUnit(event.getPlayer());
        if (unit == null) return;

        int p = event.getPreviousSlot();
        int n = event.getNewSlot();
        if (p == 0 && n == 8) {
            unit.grid.option.onScroll(unit.grid, ScrollResult.DOWN, 1, event);
            return;
        }

        if (p == 8 && n == 0) {
            unit.grid.option.onScroll(unit.grid, ScrollResult.UP, 1, event);
            return;
        }

        if (p > n) {
            unit.grid.option.onScroll(unit.grid, ScrollResult.DOWN, p - n, event);
            return;
        }

        if (p < n) {
            unit.grid.option.onScroll(unit.grid, ScrollResult.UP, n - p, event);
        }
    }
}
