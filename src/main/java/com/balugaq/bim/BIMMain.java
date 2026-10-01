package com.balugaq.bim;

import com.balugaq.bim.grid.GridDataCache;
import com.balugaq.bim.grid.GridPresetRegistry;
import com.balugaq.bim.grid.GridTickTask;
import com.balugaq.bim.grid.MenuListener;
import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
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
 * 祈祷别出 bug 喵~
 *
 * @author balugaq
 */
@NullMarked
@Getter
public final class BIMMain extends JavaPlugin {
    private static BIMMain INSTANCE;
    private final GridPresetRegistry optionRegistry = new GridPresetRegistry();

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
            g.getOption().onDestroy(g);
        });
        GridDataCache.activeGrids().clear();
    }

    private void setupLibraries() {
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);

        libraryManager.addMavenCentral();

// 保留在这里防止我忘了怎么加依赖
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
