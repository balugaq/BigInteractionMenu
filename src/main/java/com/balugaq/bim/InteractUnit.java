package com.balugaq.bim;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

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
