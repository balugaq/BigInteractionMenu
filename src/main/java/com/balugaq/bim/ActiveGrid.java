package com.balugaq.bim;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

/**
 * @author balugaq
 */
@NullMarked
@Data
@RequiredArgsConstructor
public class ActiveGrid {
    final GridOption option;
    Int2ObjectOpenHashMap<InteractUnit> units = new Int2ObjectOpenHashMap<>();
    Set<TextDisplay> background = new HashSet<>();
    int scrollOffset;
    int waitTicks;

    final Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
    final Int2ObjectOpenHashMap<ClickHandler> clickHandlers = new Int2ObjectOpenHashMap<>();

    public void setItem(int slot, ItemStack item) {
        items.put(slot, item);
        option.updateDisplayItem(this, slot, getUnit(slot));
    }

    public @Nullable InteractUnit getUnit(int slot) {
        return units.get(slot);
    }

    public @Nullable ItemStack getItemInSlot(int slot) {
        return items.get(slot);
    }
}
