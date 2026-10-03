package com.balugaq.bim;

import com.balugaq.bim.grid.GridDataCache;
import com.balugaq.bim.grid.GridTickTask;
import com.balugaq.bim.grid.GridUtil;
import com.balugaq.bim.grid.MenuListener;
import lombok.Data;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有其他插件使用此依赖时，需要先 {@code new BIMLoader(Plugin)} 来启动
 *
 * @author balugaq
 */
@Data
public class BIMLoader {
    public static final Map<Plugin, BIMLoader> loaders = new HashMap<>();
    private final Plugin instance;
    private final GridUtil gridUtil;
    private final GridDataCache cache;
    private final MenuListener listener;

    public BIMLoader(Plugin instance) {
        this.instance = instance;
        loaders.put(instance, this);
        this.gridUtil = new GridUtil(instance);
        this.cache = new GridDataCache();
        this.listener = new MenuListener(instance);
    }

    @SneakyThrows
    public static BIMLoader get(Plugin instance) {
        var loader = loaders.get(instance);
        if (loader == null) throw new IllegalAccessException("You should call BIMLoader#new(Plugin) first!");
        return loader;
    }

    public void load() {
        Bukkit.getPluginManager().registerEvents(listener, instance);
        Bukkit.getScheduler().runTaskTimer(instance, new GridTickTask(instance), 0, 1);
    }

    public void shutdown() {
        HandlerList.unregisterAll(listener);
        cache.activeGrids().values().forEach(g -> {
            g.getOption().onDestroy(g);
        });
        cache.activeGrids().clear();
        loaders.remove(instance);
    }
}
