package com.balugaq.bim;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
public class GridOptionRegistry {
    private final Map<NamespacedKey, GridOption> optionRegistry = new HashMap<>();
    public static void registerOption(GridOption option) {
        BIMMain.instance().getOptionRegistry().optionRegistry.put(option.getIdentifier(), option);
    }

    public static GridOption getOption(NamespacedKey id) {
        return BIMMain.instance().getOptionRegistry().optionRegistry.get(id);
    }
}
