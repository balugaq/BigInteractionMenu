package com.balugaq.bim.events;

import com.balugaq.bim.ActiveGrid;
import com.balugaq.bim.ScrollResult;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
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
