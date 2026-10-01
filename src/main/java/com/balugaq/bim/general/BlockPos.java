package com.balugaq.bim.general;

import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.lang.ref.WeakReference;

/**
 * 存储纯方块的位置
 *
 * @author balugaq
 */
@NullMarked
public record BlockPos(WeakReference<World> world, int blockX, int blockY, int blockZ) implements BlockPosition {
    public static BlockPos from(Location location) {
        return new BlockPos(new WeakReference<>(location.getWorld()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation() {
        return toLocation(world.get());
    }
}
