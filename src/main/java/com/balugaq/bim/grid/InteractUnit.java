/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

/**
 * 一个可交互单位
 *
 * @author balugaq
 */
@NullMarked
@Data
@RequiredArgsConstructor
public class InteractUnit {
    final int idx;
    final Location location;
    final ActiveGrid grid;
    ItemDisplay itemDisplay; // 物品
    TextDisplay titleDisplay; // 物品名称
    TextDisplay amountDisplay; // 物品数字角标

    /**
     * 这里手写 {@link InteractUnit#toString()}， 因为 {@link ActiveGrid} 里也有 {@link InteractUnit} 的引用
     * 会造成循环引用 {@link StackOverflowError}
     */
    @Override
    public String toString() {
        return "InteractUnit{idx=" + idx + ", location=" + location + ", grid.option=" + grid.option + ", itemDisplay=" + itemDisplay + ", titleDisplay=" + titleDisplay + ", amountDisplay=" + amountDisplay + "}";
    }
}
