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
        hoverCheck();
        gridCheck();
    }

    private void hoverCheck() {
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

    private void gridCheck() {
        for (ActiveGrid active : GridDataCache.activeGrids().values()) {
            if (active.isDisplaying) {
                // 附近没有玩家，就不显示，缓解 fps 压力
                if (active.location.getWorld().getNearbyEntities(active.getHidingBoundingBox(), e -> e instanceof Player).isEmpty()) {
                    active.option.onHide(active);
                }
            }
            else {
                // 玩家在附近时显示
                if (!active.location.getWorld().getNearbyEntities(active.getShowingBoundingBox(), e -> e instanceof Player).isEmpty()) {
                    active.option.onShow(active);
                }
            }
        }
    }
}
