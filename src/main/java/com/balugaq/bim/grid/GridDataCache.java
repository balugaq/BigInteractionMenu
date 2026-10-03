/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import com.balugaq.bim.general.BlockPos;
import lombok.Data;
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
public @Data class GridDataCache {
    private final Map<BlockPos, ActiveGrid> activeGrids = new HashMap<>(); // grid 缓存
    private final Map<Entity, InteractUnit> index = new HashMap<>(); // entity -> unit 的 fast index
    private final Map<UUID, InteractUnit> watching = new HashMap<>(); // 主要给 hover 用的

    public Map<BlockPos, ActiveGrid> activeGrids() {
        return activeGrids;
    }

    public Map<Entity, InteractUnit> index() {
        return index;
    }

    public Map<UUID, InteractUnit> watching() {
        return watching;
    }
}
