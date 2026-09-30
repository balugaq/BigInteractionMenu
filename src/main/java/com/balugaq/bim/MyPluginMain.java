package com.balugaq.bim;

import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

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
 */
@Getter
public final class MyPluginMain extends JavaPlugin {
    private static MyPluginMain INSTANCE;
    private final GridOptionRegistry optionRegistry = new GridOptionRegistry();

    @Override
    public void onLoad() {
        INSTANCE = this;
        setupLibraries();
    }

    public static MyPluginMain instance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        // Plugin start logic
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getScheduler().runTaskTimer(instance(), this::tickGrid, 0, 1);
        ExampleGridOption.instance.load();
        getCommand("bim").setExecutor(new BIMCommandExecutor());
    }

    public void tickGrid() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            var unit = Util.getUnit(p, p.getEyeLocation());
            if (unit == null) {
                var old = GridDataCache.watching.get(p);
                if (old != null) {
                    old.titleDisplay.setTextOpacity((byte) 0);
                    old.itemDisplay.setBrightness(Util.KDB);
                }
                return;
            }
            var grid = unit.grid;
            grid.option.hover(unit, p);
            grid.waitTicks += 1;
            if (grid.waitTicks % grid.option.tickInterval() == 0) {
                grid.option.tick();
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll(instance());
        GridDataCache.activeGrids.values().forEach(g -> {
            g.units.values().forEach(u -> {
                u.itemDisplay.remove();
                u.titleDisplay.remove();
                u.amountDisplay.remove();
            });
            g.background.forEach(Entity::remove);
        });
        GridDataCache.activeGrids.clear();
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
