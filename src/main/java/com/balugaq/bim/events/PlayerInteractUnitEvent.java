package com.balugaq.bim.events;

import com.balugaq.bim.grid.ClickDTO;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * 玩家和一个 InteractUnit 交互时触发
 *
 * @author balugaq
 */
@NullMarked
@Getter
public class PlayerInteractUnitEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ClickDTO dto;

    @Setter
    private boolean cancelled;

    public PlayerInteractUnitEvent(ClickDTO dto) {
        super(dto.player());
        this.dto = dto;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
