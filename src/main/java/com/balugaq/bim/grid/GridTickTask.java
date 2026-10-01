package com.balugaq.bim.grid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class GridTickTask implements Runnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            var unit = GridUtil.rayTraceUnit(p);
            var old = GridDataCache.watching().get(p.getUniqueId());
            if (unit == null) {
                if (old != null) {
                    GridUtil.offGrid(p);
                    GridUtil.offHover(old, p);
                }
                continue;
            }
            if (unit != old) GridUtil.offGrid(p);
            GridUtil.offHover(old, p);
            var grid = unit.grid;
            grid.option.onHover(unit, p);
            grid.waitTicks += 1;
            if (grid.waitTicks % grid.option.tickInterval() == 0) {
                grid.option.tick();
            }
        }
    }
}
