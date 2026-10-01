package com.balugaq.bim;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GridDataCache {
    public static final Map<BlockPos, ActiveGrid> activeGrids = new HashMap<>();
    public static final Map<Entity, InteractUnit> index = new HashMap<>();
    public static final Map<Player, InteractUnit> watching = new HashMap<>();
}
