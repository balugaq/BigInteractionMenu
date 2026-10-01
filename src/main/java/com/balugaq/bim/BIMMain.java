package com.balugaq.bim;

import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

/**
 * 　 　 　 　 　 　 　 　 ／＞　　フ
 * 　 　 　 　 　 　 　 　| 　_　 _|
 * 　 　 　 　 　 　 　 ／` ミ＿xノ
 * 　 　 　 　 　 　 /　　　 　 |
 * 　 　 　 　 　  /　 ヽ　　 ﾉ
 * 　 　 　 　 　 │　　|　|　|
 * 　／￣|　　 |　|　|　|　|
 *  | (￣ヽ＿_ヽ_)__)
 *  ＼二つ
 *
 * @author balugaq
 */
@NullMarked
@Getter
public final class BIMMain extends JavaPlugin {
    private static BIMMain INSTANCE;
    private final GridOptionRegistry optionRegistry = new GridOptionRegistry();

    @Override
    public void onLoad() {
        INSTANCE = this;
        setupLibraries();
    }

    public static BIMMain instance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getScheduler().runTaskTimer(instance(), this::tickGridAndHoverCheck, 0, 1);
    }

    public void tickGridAndHoverCheck() {
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

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(instance());
        GridDataCache.activeGrids().values().forEach(g -> {
            g.units.values().forEach(u -> {
                u.itemDisplay.remove();
                u.titleDisplay.remove();
                u.amountDisplay.remove();
            });
            g.background.forEach(Entity::remove);
        });
        GridDataCache.activeGrids().clear();
    }

    private void setupLibraries() {
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);

        libraryManager.addMavenCentral();

//        libraryManager.addRepository(repo);
//        Library byteBuddy = Library.builder()
//                .groupId("net{}bytebuddy")
//                .artifactId("byte-buddy")
//                .version("1.18.11")
//                .build();
//
//        libraryManager.loadLibrary(byteBuddy);
    }
}
