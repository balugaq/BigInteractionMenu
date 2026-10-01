package com.balugaq.bim.grid;

import com.balugaq.bim.general.BlockPos;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GridDataCache {
    private static final Map<BlockPos, ActiveGrid> activeGrids = new HashMap<>();
    private static final Map<Entity, InteractUnit> index = new HashMap<>();
    private static final Map<UUID, InteractUnit> watching = new HashMap<>();

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
