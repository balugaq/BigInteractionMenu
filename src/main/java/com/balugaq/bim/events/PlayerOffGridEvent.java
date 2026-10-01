/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.events;

import com.balugaq.bim.grid.ActiveGrid;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * 玩家视线离开一个 Grid 后触发
 *
 * @author balugaq
 */
@NullMarked
@Getter
public class PlayerOffGridEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ActiveGrid grid;

    public PlayerOffGridEvent(Player p, ActiveGrid active) {
        super(p);
        this.grid = active;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
