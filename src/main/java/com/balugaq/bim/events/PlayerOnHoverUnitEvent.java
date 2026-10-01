package com.balugaq.bim.events;

import com.balugaq.bim.InteractUnit;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Getter
public class PlayerOnHoverUnitEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final InteractUnit unit;

    public PlayerOnHoverUnitEvent(Player p, InteractUnit unit) {
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
