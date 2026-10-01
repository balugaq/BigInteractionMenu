/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * 类似 Slimefun 的 BlockMenuPreset
 *
 * @author balugaq
 */
@NullMarked
@Data
public abstract class GridPreset {
    public static final float DEFAULT_GAP = 0.12f;
    final NamespacedKey identifier;
    final @Range(from = 1, to = Integer.MAX_VALUE) int height;
    final @Range(from = 1, to = Integer.MAX_VALUE) int width;
    float gap;

    public GridPreset(NamespacedKey identifier,
                      @Range(from = 1, to = Integer.MAX_VALUE) int height,
                      @Range(from = 1, to = Integer.MAX_VALUE) int width) {
        this(identifier, height, width, DEFAULT_GAP);
    }

    public GridPreset(NamespacedKey identifier,
                      @Range(from = 1, to = Integer.MAX_VALUE) int height,
                      @Range(from = 1, to = Integer.MAX_VALUE) int width,
                      float gap) {
        this.identifier = identifier;
        this.height = height;
        this.width = width;
        this.gap = gap;
        GridPresetRegistry.registerOption(this);
    }

    public void place(Location location) {
        GridUtil.placeGrid(location, this);
    }

    /**
     * 初始化 Grid 时触发
     */
    public abstract void init(ActiveGrid active, int idx, InteractUnit unit);

    /**
     * 当 Grid 隐藏时，玩家靠近后触发
     */
    @OverridingMethodsMustInvokeSuper
    public void onShow(ActiveGrid grid) {
        grid.isDisplaying = true;
    }

    /**
     * 当 Grid 显示时，玩家远离后触发
     */
    @OverridingMethodsMustInvokeSuper
    public void onHide(ActiveGrid grid) {
        grid.isDisplaying = false;
    }

    /**
     * 当玩家在一个 InteractUnit 上悬停鼠标后触发
     */
    public abstract void onHover(InteractUnit unit, Player player);

    /**
     * 当玩家在鼠标悬停离开一个 InteractUnit 后触发
     */
    public abstract void offHover(InteractUnit unit, Player player);

    /**
     * 当玩家使用滚轮滑动物品栏时触发，即视作 Grid 滚动
     */
    @OverridingMethodsMustInvokeSuper
    public void onScroll(ActiveGrid active, ScrollResult result, int delta, PlayerItemHeldEvent event) {
        switch (result) {
            case UP -> {
                active.scrollOffset = Math.min(active.scrollOffset + delta, (Math.max(0, entriesSize() - getWidth() * getHeight()) + getWidth() - 1) / getWidth());
            }
            case DOWN -> {
                active.scrollOffset = Math.max(active.scrollOffset - delta, 0);
            }
        }
    }

    public abstract void updateDisplayItem(ActiveGrid active, int idx, InteractUnit u);

    /**
     * Grid 可以有自己的 Ticker
     */
    public abstract void tick();

    /**
     * Tick 间隔， Ticks 为单位
     */
    public abstract int tickInterval();

    /**
     * 用于限定 Scroll Offset 范围
     */
    public abstract int entriesSize();

    /**
     * 在关服时触发，用于删除实体
     */
    public void onDestroy(ActiveGrid g) {
        g.units.values().forEach(u -> {
            u.itemDisplay.remove();
            u.titleDisplay.remove();
            u.amountDisplay.remove();
        });
        g.backgrounds.forEach(Entity::remove);
    }

    /**
     * 开启时会在使用 TextDisplay 绘制一个仿真箱子界面作背景
     */
    public boolean defaultBackground() {
        return true;
    }
}
