package com.balugaq.bim.grid;

import com.balugaq.bim.general.BlockPos;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 缓存数据
 *
 * @author balugaq
 */
@NullMarked
public class GridDataCache {
    private static final Map<BlockPos, ActiveGrid> activeGrids = new HashMap<>(); // grid 缓存
    private static final Map<Entity, InteractUnit> index = new HashMap<>(); // entity -> unit 的 fast index
    private static final Map<UUID, InteractUnit> watching = new HashMap<>(); // 主要给 hover 用的

    public static Map<BlockPos, ActiveGrid> activeGrids() {
        return activeGrids;
    }

    public static Map<Entity, InteractUnit> index() {
        return index;
    }

    public static Map<UUID, InteractUnit> watching() {
        return watching;
    }
}
