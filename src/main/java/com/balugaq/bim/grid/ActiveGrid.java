/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import com.balugaq.bim.BIMLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

/**
 * 类似 Slimefun 的 ChestMenu
 *
 * @author balugaq
 */
@NullMarked
@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ActiveGrid {
    final Location location;
    final GridPreset option;
    final GridOrientation orientation;
    Int2ObjectOpenHashMap<InteractUnit> units = new Int2ObjectOpenHashMap<>();
    Set<TextDisplay> backgrounds = new HashSet<>();
    int scrollOffset;
    int waitTicks;
    boolean isDisplaying;

    final Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
    final Int2ObjectOpenHashMap<ClickHandler> clickHandlers = new Int2ObjectOpenHashMap<>();

    private void checkBound(int slot) {
        if (slot < 0 || slot >= option.width * option.height) {
            throw new IndexOutOfBoundsException(slot);
        }
    }

    /**
     * 设置并更新物品显示
     */
    public void setItem(@Range(from = 0, to = Integer.MAX_VALUE) int slot, ItemStack item) {
        checkBound(slot);
        items.put(slot, item);
        option.updateDisplayItem(this, slot, getUnit(slot));
    }

    /**
     * 设置 slot 所在的物品
     */
    public void setItemUnsafe(@Range(from = 0, to = Integer.MAX_VALUE) int slot, ItemStack item) {
        checkBound(slot);
        items.put(slot, item);
    }

    public void setClickHandler(@Range(from = 0, to = Integer.MAX_VALUE) int slot, ClickHandler clickHandler) {
        checkBound(slot);
        clickHandlers.put(slot, clickHandler);
    }

    /**
     * 获取 InteractUnit
     */
    public @Nullable InteractUnit getUnit(@Range(from = 0, to = Integer.MAX_VALUE) int slot) {
        return units.get(slot);
    }

    /**
     * 获取 slot 所在的物品
     */
    public @Nullable ItemStack getItemInSlot(@Range(from = 0, to = Integer.MAX_VALUE) int slot) {
        return items.get(slot);
    }

    /**
     * 占据的 BoundingBox
     */
    public BoundingBox getOccupiedBoundingBox() {
        return option.getOccupiedBoundingBox(location, orientation);
    }

    /**
     * 有玩家进入这个 Showing Box 内时就会触发 {@link GridPreset#onShow(ActiveGrid)}
     * 多个玩家进入只会触发 1 次
     */
    public BoundingBox getShowingBoundingBox() {
        return getOccupiedBoundingBox().expand(GridUtil.SHOW_DISTANCE);
    }

    /**
     * 没有玩家在这个 Hiding Box 范围内时就会触发 {@link GridPreset#onHide(ActiveGrid)}
     */
    public BoundingBox getHidingBoundingBox() {
        return getOccupiedBoundingBox().expand(GridUtil.HIDE_DISTANCE);
    }

    public String getTag() {
        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public void tag(Entity e) {
        e.getPersistentDataContainer().set(BIMLoader.get(option.plugin).getGridUtil().TAG, PersistentDataType.STRING, getTag());
    }
}
