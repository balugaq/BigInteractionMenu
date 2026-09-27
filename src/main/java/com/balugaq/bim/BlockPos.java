package com.balugaq.bim;

import io.papermc.paper.math.BlockPosition;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.ref.WeakReference;

@Data
public class BlockPos implements BlockPosition {
    private final WeakReference<World> world;
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(World world, int x, int y, int z) {
        this.world = new WeakReference<>(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPos from(Location location) {
        return new BlockPos(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation() {
        return toLocation(world.get());
    }

    @Override
    public int blockX() {
        return x;
    }

    @Override
    public int blockY() {
        return y;
    }

    @Override
    public int blockZ() {
        return z;
    }
}
