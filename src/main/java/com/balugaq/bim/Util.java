package com.balugaq.bim;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

public class Util {
    public static final NamespacedKey IDX_KEY = new NamespacedKey(MyPluginMain.instance(), "idx");
    public static final NamespacedKey OPTION_ID_KEY = new NamespacedKey(MyPluginMain.instance(), "option_identifier");

    public static LookResult lookResult(Player p) {
        var result = p.getWorld().rayTraceEntities(p.getLocation(), getDirection(p.getYaw(), p.getPitch()), 5);
        if (result == null) return LookResult.fail();
        var entity = result.getHitEntity();
        var unit = asInteractUnit(entity);
        if (unit != null) {
            return LookResult.success(unit);
        } else {
            return LookResult.fail();
        }
    }

    @Nullable
    public static InteractUnit asInteractUnit(@Nullable Entity entity) {
        if (entity == null) return null;
        return GridDataCache.index.get(entity);
    }

    public static String formatAmount(long amount) {
        if (amount < 0) {
            return "-" + formatAmount(-amount);
        }
        if (amount < 1000) {
            return String.valueOf(amount);
        }

        String[] units = {"", "K", "M", "B", "T", "P", "E"};
        int unitIndex = 0;
        double value = amount;

        while (value >= 1000 && unitIndex < units.length - 1) {
            value /= 1000;
            unitIndex++;
        }

        // 保留一位小数
        String formatted = String.format("%.1f", value);

        // 去掉末尾的 .0
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }

        return formatted + units[unitIndex];
    }

    public static void placeGrid(Location location, GridOption option) {
        Int2ObjectOpenHashMap<InteractUnit> map = new Int2ObjectOpenHashMap<>();
        for (int h = 0; h < option.height; h++) {
            for (int w = 0; w < option.width; w++) {
                Location loc = location.clone().add(0.25 * w, 0.25 * (option.height - h), 0);
                int i = h * 9 + w;
                var unit = new InteractUnit(i, loc);
                option.initialize(i, unit);
                map.put(i, unit);
            }
        }
        var active = new ActiveGrid(option, map);
        map.values().forEach(u -> {
            u.setGrid(active);
            GridDataCache.index.put(u.itemDisplay, u);
            GridDataCache.index.put(u.titleDisplay, u);
            GridDataCache.index.put(u.amountDisplay, u);
        });
        GridDataCache.activeGrids.put(BlockPos.from(location), active);
    }

    public static Vector getDirection(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        return new Vector(x, y, z);
    }

    @Nullable
    public static InteractUnit getUnit(Player player) {
        var result = Util.lookResult(player);
        if (!result.success()) return null;
        return result.unit();
    }
}
