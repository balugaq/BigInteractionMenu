package com.balugaq.bim;

import com.balugaq.bim.events.PlayerInteractUnitEvent;
import com.balugaq.bim.events.PlayerScrollGridEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        var dto = new ClickDTO(event, unit);
        if (!new PlayerInteractUnitEvent(dto).callEvent()) return;
        unit.grid.clickHandlers.getOrDefault(unit.idx, ClickHandler.cancel).onClick(dto);
    }

    @EventHandler(ignoreCancelled = true)
    public void onScroll(PlayerItemHeldEvent event) {
        var unit = GridUtil.rayTraceUnit(event.getPlayer());
        if (unit == null) return;

        int p = event.getPreviousSlot();
        int n = event.getNewSlot();
        ScrollResult r;
        int delta;
        if (p == 0 && n == 8) { r = ScrollResult.DOWN; delta = 1; }
        else if (p == 8 && n == 0) { r = ScrollResult.UP; delta = 1; }
        else if (p > n) { r = ScrollResult.DOWN; delta = p - n; }
        else if (p < n) { r = ScrollResult.UP; delta = n - p; }
        else return;

        if (!new PlayerScrollGridEvent(event.getPlayer(), r, delta).callEvent()) return;
        unit.grid.option.onScroll(unit.grid, r, delta, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void offGrid(PlayerDeathEvent event) {
        GridUtil.offGrid(event.getPlayer());
    }

    @EventHandler
    public void offGrid(PlayerQuitEvent event) {
        GridUtil.offGrid(event.getPlayer());
    }
}
