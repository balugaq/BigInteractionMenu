/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
@Getter
public class GridPresetRegistry {
    @Getter
    private static final GridPresetRegistry optionRegistry = new GridPresetRegistry();
    private final Map<NamespacedKey, GridPreset> presetRegistry = new HashMap<>();
    public static void registerOption(GridPreset option) {
        optionRegistry.presetRegistry.put(option.getIdentifier(), option);
    }

    public static GridPreset getOption(NamespacedKey id) {
        return optionRegistry.presetRegistry.get(id);
    }
}
