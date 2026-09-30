package com.balugaq.bim;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class Util {
    public static final NamespacedKey IDX_KEY = new NamespacedKey(MyPluginMain.instance(), "idx");
    public static final NamespacedKey OPTION_ID_KEY = new NamespacedKey(MyPluginMain.instance(), "option_identifier");

    @Nullable
    public static <T extends Entity> T rayEntity(Vector origin, Vector direction, Collection<T> candidates) {
        Vector dir = direction.clone().normalize();
        T closest = null;
        double minDistance = Double.MAX_VALUE;

        for (var candidate : candidates) {
            Vector toCandidate = candidate.getLocation().toVector().subtract(origin);

            // 投影长度 t：t < 0 表示点在射线反方向
            double t = toCandidate.dot(dir);
            if (t < 0) continue;

            // 点到射线的垂直距离
            double distance = toCandidate.crossProduct(dir).length();

            if (distance < 0.2 && distance < minDistance) {
                minDistance = distance;
                closest = candidate;
            }
        }

        return closest;
    }

    public static LookResult lookResult(Player p, Location interactLocation) {
        var result = rayEntity(interactLocation.toVector(), getDirection(p.getYaw(), p.getPitch()), p.getLocation().getWorld().getNearbyEntitiesByType(ItemDisplay.class, p.getLocation(), 5));
        if (result == null) return LookResult.fail();
        var unit = asInteractUnit(result);
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

    public static int pixelToLineWidth(float pixel) {
        return Math.round((pixel - 0.07173f) / 0.109705f);
    }

    public static void placeGrid(Location location, GridOption option) {
        var active = new ActiveGrid(option);
        Int2ObjectOpenHashMap<InteractUnit> units = new Int2ObjectOpenHashMap<>();
        var gap = option.gap;
        for (int h = 0; h < option.height; h++) {
            for (int w = 0; w < option.width; w++) {
                Location loc = location.clone().add(gap * (w + 0.5), gap * (option.height - h - 0.5), 0);
                int i = h * option.width + w;
                var unit = new InteractUnit(i, loc, active);
                option.initialize(i, unit);
                units.put(i, unit);
            }
        }
        units.values().forEach(u -> {
            GridDataCache.index.put(u.itemDisplay, u);
            GridDataCache.index.put(u.titleDisplay, u);
            GridDataCache.index.put(u.amountDisplay, u);
        });
        active.units = units;
        GridDataCache.activeGrids.put(BlockPos.from(location), active);
        if (option.defaultBackground()) {
            var bigBackground = location.getWorld().spawn(location.clone().add(gap * (option.width / 2f + 0.5), -gap * 0.5, 0.001), TextDisplay.class);
            var scale = 0.2f;
            var blockWidth = gap * (option.width + 1);
            var blockPerText = 1f / 16f * 4f * scale;
            var blockHeight = gap * option.height;
            var c = Component.text("你".repeat(Math.round((blockWidth / blockPerText) * (blockHeight / blockPerText) * 16f)));
            bigBackground.text(c);
            bigBackground.setTextOpacity((byte) 0);
            bigBackground.setTransformation(TransformationBuilder.create().scale(scale / 4).build());
            bigBackground.setBackgroundColor(Color.fromRGB(0x8B8B8B));
            bigBackground.setBrightness(new Display.Brightness(15, 15));
            bigBackground.setLineWidth(pixelToLineWidth(Math.round(blockWidth / blockPerText) * 4) - 2);
            active.background.add(bigBackground);

            for (int w = 0; w <= option.width; w++) {
                var divider = location.getWorld().spawn(location.clone().add(gap * (w + 0.5), -gap * 0.5, 0.002), TextDisplay.class);
                divider.text(Component.text("你".repeat(Math.round(blockHeight / blockPerText * 8))));
                divider.setTextOpacity((byte) 0);
                divider.setTransformation(TransformationBuilder.create().scale(scale / 8).build());
                divider.setBackgroundColor(Color.fromRGB(0xC3C3C3));
                divider.setBrightness(new Display.Brightness(15, 15));
                divider.setLineWidth(1);
                active.background.add(divider);
            }

            for (int h = 0; h <= option.height; h++) {
                var divider = location.getWorld().spawn(location.clone().add(gap * (option.width / 2f + 0.5), gap * (option.height - h - 0.5), 0.003), TextDisplay.class);
                divider.text(Component.text("你".repeat(Math.round(blockWidth / blockPerText * 8))));
                divider.setTextOpacity((byte) 0);
                divider.setTransformation(TransformationBuilder.create().scale(scale / 8).build());
                divider.setBackgroundColor(Color.fromRGB(0xC3C3C3));
                divider.setBrightness(new Display.Brightness(15, 15));
                divider.setLineWidth(999999);
                active.background.add(divider);
            }
        }
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
    public static InteractUnit getUnit(Player player, Location interactLocation) {
        var result = Util.lookResult(player, interactLocation);
        if (!result.success()) return null;
        return result.unit();
    }

    public static <T> T either(@Nullable T t1, T t2) {
        if (t1 == null) return t2;
        return t1;
    }
}
