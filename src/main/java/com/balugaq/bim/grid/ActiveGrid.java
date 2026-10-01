package com.balugaq.bim.grid;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author balugaq
 */
@NullMarked
@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ActiveGrid {
    final GridOption option;
    Int2ObjectOpenHashMap<InteractUnit> units = new Int2ObjectOpenHashMap<>();
    Set<TextDisplay> backgrounds = new HashSet<>();
    int scrollOffset;
    int waitTicks;

    final Set<UUID> viewers = new HashSet<>();
    final Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
    final Int2ObjectOpenHashMap<ClickHandler> clickHandlers = new Int2ObjectOpenHashMap<>();

    private void checkBound(int slot) {
        if (slot < 0 || slot >= option.width * option.height) {
            throw new IndexOutOfBoundsException(slot);
        }
    }

    public void setItem(int slot, ItemStack item) {
        checkBound(slot);
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
