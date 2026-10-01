/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.events;

import com.balugaq.bim.grid.InteractUnit;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * 玩家视线离开一个 InteractUnit 后触发
 *
 * @author balugaq
 */
@NullMarked
@Getter
public class PlayerOffHoverUnitEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final InteractUnit unit;

    public PlayerOffHoverUnitEvent(Player p, InteractUnit unit) {
        super(p);
        this.unit = unit;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
