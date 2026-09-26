package com.balugaq.bim;

import org.bukkit.entity.Display;

import java.util.HashMap;
import java.util.Map;

public class GridDataCache {
    public static final Map<BlockPos, ActiveGrid> activeGrids = new HashMap<>();
    public static final Map<Display, InteractUnit> index = new HashMap<>();
}
