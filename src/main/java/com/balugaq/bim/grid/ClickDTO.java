package com.balugaq.bim.grid;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * 玩家交互 InteractUnit 的交互数据
 *
 * @author balugaq
 */
@NullMarked
public record ClickDTO(PlayerInteractEvent event, InteractUnit unit) {
    public Player player() {
        return event.getPlayer();
    }

    public int slot() {
        return unit.idx;
    }

    public ItemStack clicked() {
        return unit.itemDisplay.getItemStack();
    }

    public boolean isShiftClick() {
        return player().isSneaking();
    }

    public boolean isRightClick() {
        return event.getAction().isRightClick();
    }

    public boolean isLeftClick() {
        return event.getAction().isLeftClick();
    }
}
