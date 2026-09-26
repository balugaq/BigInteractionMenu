package com.balugaq.bim;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

@Data
public class InteractUnit {
    final int idx;
    final Location location;
    ItemDisplay itemDisplay;
    TextDisplay titleDisplay;
    TextDisplay amountDisplay;
    ActiveGrid grid;
    public InteractUnit(int idx, Location location) {
        this.idx = idx;
        this.location = location;
        this.itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);
        this.titleDisplay = location.getWorld().spawn(location.clone().add(0, 0.25, 0), TextDisplay.class);
        this.amountDisplay = location.getWorld().spawn(location.clone().add(0.25, 0, 0), TextDisplay.class);
    }
}
