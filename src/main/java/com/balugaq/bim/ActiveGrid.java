package com.balugaq.bim;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ActiveGrid {
    final GridOption option;
    final Int2ObjectOpenHashMap<InteractUnit> units;
    int scrollOffset;
    int waitTicks;
}
