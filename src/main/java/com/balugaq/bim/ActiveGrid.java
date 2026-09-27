package com.balugaq.bim;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.TextDisplay;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class ActiveGrid {
    final GridOption option;
    Int2ObjectOpenHashMap<InteractUnit> units = new Int2ObjectOpenHashMap<>();
    Set<TextDisplay> background = new HashSet<>();
    int scrollOffset;
    int waitTicks;
}
