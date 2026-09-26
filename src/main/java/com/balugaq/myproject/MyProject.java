package com.balugaq.myproject;

import net.byteflux.libby.BukkitLibraryManager;
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
public final class MyProject extends JavaPlugin {
    private static MyProject INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
        setupLibraries();
    }

    public static MyProject instance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        // Plugin start logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
