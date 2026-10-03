/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import com.balugaq.bim.BIMLoader;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@RequiredArgsConstructor
@NullMarked
public class GridTickTask implements Runnable {
    private final Plugin plugin;

    @Override
    public void run() {
        hoverCheck();
        gridCheck();
    }

    public BIMLoader getLoader() {
        return BIMLoader.get(plugin);
    }

    private void hoverCheck() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            var cache = getLoader().getCache();
            var unit = getLoader().getGridUtil().rayTraceUnit(p);
            var old = cache.watching().get(p.getUniqueId());
            if (unit == null) {
                if (old != null) {
                    getLoader().getGridUtil().offGrid(p);
                    GridUtil.offHover(old, p);
                }
                continue;
            }
            if (unit != old) getLoader().getGridUtil().offGrid(p);
            GridUtil.offHover(old, p);
            var grid = unit.grid;
            grid.option.onHover(unit, p);
            grid.waitTicks += 1;
            if (grid.waitTicks % grid.option.tickInterval(grid) == 0) {
                grid.option.tick(grid);
            }
        }
    }

    private void gridCheck() {
        for (ActiveGrid active : getLoader().getCache().activeGrids().values()) {
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
