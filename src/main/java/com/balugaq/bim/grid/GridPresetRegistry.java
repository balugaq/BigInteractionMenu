/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import com.balugaq.bim.BIMMain;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
public class GridPresetRegistry {
    private final Map<NamespacedKey, GridPreset> presetRegistry = new HashMap<>();
    public static void registerOption(GridPreset option) {
        BIMMain.instance().getOptionRegistry().presetRegistry.put(option.getIdentifier(), option);
    }

    public static GridPreset getOption(NamespacedKey id) {
        return BIMMain.instance().getOptionRegistry().presetRegistry.get(id);
    }
}
