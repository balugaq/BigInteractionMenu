/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.events;

import com.balugaq.bim.grid.ScrollResult;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * 玩家使用滚轮操作 Grid 翻动时触发
 *
 * @author balugaq
 */
@NullMarked
@Getter
public class PlayerScrollGridEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ScrollResult result;
    private final int delta;

    @Setter
    private boolean cancelled;

    public PlayerScrollGridEvent(Player p, ScrollResult result, int delta) {
        super(p);
        this.result = result;
        this.delta = delta;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
