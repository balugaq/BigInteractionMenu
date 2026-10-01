package com.balugaq.bim.grid;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Data
@RequiredArgsConstructor
public class InteractUnit {
    final int idx;
    final Location location;
    final ActiveGrid grid;
    ItemDisplay itemDisplay;
    TextDisplay titleDisplay;
    TextDisplay amountDisplay;

    @Override
    public String toString() {
        return "InteractUnit{idx=" + idx + ", location=" + location + ", grid.option=" + grid.option + ", itemDisplay=" + itemDisplay + ", titleDisplay=" + titleDisplay + ", amountDisplay=" + amountDisplay + "}";
    }
}
