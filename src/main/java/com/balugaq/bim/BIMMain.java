package com.balugaq.bim;

import com.balugaq.bim.grid.GridDataCache;
import com.balugaq.bim.grid.GridOptionRegistry;
import com.balugaq.bim.grid.GridTickTask;
import com.balugaq.bim.grid.MenuListener;
import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
        Bukkit.getScheduler().runTaskTimer(instance(), new GridTickTask(), 0, 1);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(instance());
        GridDataCache.activeGrids().values().forEach(g -> {
            g.getUnits().values().forEach(u -> {
                u.getItemDisplay().remove();
                u.getTitleDisplay().remove();
                u.getAmountDisplay().remove();
            });
            g.getBackgrounds().forEach(Entity::remove);
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
