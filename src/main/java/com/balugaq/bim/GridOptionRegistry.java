package com.balugaq.bim;

import java.util.HashMap;
import java.util.Map;

public class GridOptionRegistry {
    private final Map<String, GridOption> optionRegistry = new HashMap<>();
    public static void registerOption(GridOption option) {
        MyPluginMain.instance().getOptionRegistry().optionRegistry.put(option.getIdentifier(), option);
    }

    public static GridOption getOption(String id) {
        return MyPluginMain.instance().getOptionRegistry().optionRegistry.get(id);
    }
}
