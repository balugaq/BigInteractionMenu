package com.balugaq.bim.grid;

import com.balugaq.bim.general.BlockPos;
import com.balugaq.bim.general.TransformationBuilder;
import com.balugaq.bim.events.PlayerOffGridEvent;
import com.balugaq.bim.events.PlayerOffHoverUnitEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * @author balugaq
 */
@NullMarked
public class GridUtil {
    public static final Display.Brightness MDB = new Display.Brightness(15, 15);
    public static final Display.Brightness KDB = new Display.Brightness(12, 12);
    /**
     * text_opacity：完全透明。0 在所有版本都可靠。
     * 注意：不要用 1~26 当"显示"——4~26 会被客户端着色器按 alpha<0.1 丢弃，
     * 1~3 在部分版本按 1/255 渲染（同样不可见），部分版本才特判为不透明。
     */
    public static final byte TEXT_OPACITY_HIDDEN = (byte) 0;
    /**
     * text_opacity：完全不透明。255（默认值 -1）在所有版本都可靠。
     */
    public static final byte TEXT_OPACITY_SHOWN = (byte) 255;

    @Nullable
    public static <T extends Entity> T rayTraceEntity(Vector origin, Vector direction, Collection<T> candidates) {
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

            if (distance < 0.12 && distance < minDistance) {
                minDistance = distance;
                closest = candidate;
            }
        }

        return closest;
    }

    @Nullable
    public static InteractUnit asInteractUnit(@Nullable Entity entity) {
        if (entity == null) return null;
        return GridDataCache.index().get(entity);
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
        var active = new ActiveGrid(option);
        Int2ObjectOpenHashMap<InteractUnit> units = new Int2ObjectOpenHashMap<>();
        var gap = option.getGap();
        var o = GridOrientation.fromYawPitch(location.getYaw(), location.getPitch());
        for (int h = 0; h < option.getHeight(); h++) {
            for (int w = 0; w < option.getWidth(); w++) {
                Location loc = o.apply(location, gap * (w + 0.5), gap * (option.getHeight() - h - 0.5), 0);
                int i = h * option.getWidth() + w;
                var unit = new InteractUnit(i, loc, active);
                option.init(i, unit);
                units.put(i, unit);
            }
        }
        units.values().forEach(u -> {
            GridDataCache.index().put(u.itemDisplay, u);
            GridDataCache.index().put(u.titleDisplay, u);
            GridDataCache.index().put(u.amountDisplay, u);
        });
        active.units = units;
        GridDataCache.activeGrids().put(BlockPos.from(location), active);
        if (option.defaultBackground()) {
            addDefaultBackground(active, location);
        }
    }

    private static void addDefaultBackground(ActiveGrid active, Location location) {
        var option = active.option;
        var scale = 0.1f; // 物体大小
        var precision = 2f; // 精确度，数值越大，像素显示的越精确（只改变字数，不会影响性能，但会影响发包大小）
        var heightPerText = 1f / 16f * 4f * scale;
        var widthPerText = (1f / 24 / 16 - 0.001f) * 10 * scale; // 24 个 . 在 scale 0.1 下对应 1/16 格
        var gap = option.gap;
        var o = GridOrientation.fromYawPitch(location.getYaw(), location.getPitch());
        var blockWidth = (gap + 1f / 320f) * option.getWidth();
        var blockHeight = gap * option.getHeight();
        var textWidth = blockWidth / widthPerText * 0.625f * precision;
        var t = Math.round(blockHeight / heightPerText);
        var ht = Component.text(".".repeat(Math.round(textWidth / 4f)));
        for (int h = 1; h <= t; h++) {
            var bg = location.getWorld().spawn(o.apply(location, gap * (option.getWidth() / 2f + 0.5), gap * (option.getHeight() - 0.5) - heightPerText * h - 0.003, 0.001), TextDisplay.class);
            bg.setRotation(o.getYaw(), o.getPitch());
            bg.text(ht);
            bg.setTextOpacity((byte) 0);
            bg.setTransformation(TransformationBuilder.create().scale(scale * 2f / precision).build());
            bg.setBackgroundColor(Color.fromRGB(0x8B8B8B));
            bg.setBrightness(MDB);
            bg.setLineWidth(999999);
            active.backgrounds.add(bg);
        }

        var s = ".\n".repeat(Math.round(blockHeight / heightPerText * precision));
        s = s.substring(0, s.length() - 1);
        var wt = Component.text(s);
        for (int w = 0; w <= option.getWidth(); w++) {
            var divider = location.getWorld().spawn(o.apply(location, gap * (w + 0.5), -gap * 0.5, 0.002), TextDisplay.class);
            divider.setRotation(o.getYaw(), o.getPitch());
            divider.text(wt);
            divider.setTextOpacity((byte) 0);
            divider.setTransformation(TransformationBuilder.create().scale(scale / precision).build());
            divider.setBackgroundColor(Color.fromRGB(0xC3C3C3));
            divider.setBrightness(MDB);
            divider.setLineWidth(1);
            active.backgrounds.add(divider);
        }

        for (int h = 0; h <= option.getHeight(); h++) {
            var divider = location.getWorld().spawn(o.apply(location, gap * (option.getWidth() / 2f + 0.5), gap * (option.getHeight() - h - 0.5) - 0.003, 0.003), TextDisplay.class);
            divider.setRotation(o.getYaw(), o.getPitch());
            divider.text(Component.text(".".repeat(Math.round(textWidth * 2f))));
            divider.setTextOpacity((byte) 0);
            divider.setTransformation(TransformationBuilder.create().scale(scale / 4f / precision).build());
            divider.setBackgroundColor(Color.fromRGB(0xC3C3C3));
            divider.setBrightness(MDB);
            divider.setLineWidth(999999);
            active.backgrounds.add(divider);
        }
    }

    public static void removeGrid(BlockPos pos) {
        var active = GridDataCache.activeGrids().remove(pos);
        if (active == null) return;
        for (var unit : active.units.values()) {
            GridDataCache.index().remove(unit.itemDisplay);
            GridDataCache.index().remove(unit.titleDisplay);
            GridDataCache.index().remove(unit.amountDisplay);
            unit.itemDisplay.remove();
            unit.titleDisplay.remove();
            unit.amountDisplay.remove();
        }
        for (var display : active.backgrounds) {
            display.remove();
        }
        GridDataCache.watching().values().removeIf(u ->
            u.itemDisplay.isDead() || !u.itemDisplay.isValid()
            || u.titleDisplay.isDead() || !u.titleDisplay.isValid()
            || u.amountDisplay.isDead() || !u.amountDisplay.isValid()
        );
    }

    public static void offGrid(Player player) {
        var u = GridDataCache.watching().remove(player.getUniqueId());
        if (u != null) {
            new PlayerOffGridEvent(player, u.grid).callEvent();
            u.grid.viewers.remove(player.getUniqueId());
            offHover(u, player);
        }
    }

    public static void offHover(InteractUnit old, Player p) {
        new PlayerOffHoverUnitEvent(p, old).callEvent();
        old.grid.option.offHover(old, p);
        old.titleDisplay.setTextOpacity(GridUtil.TEXT_OPACITY_HIDDEN);
        old.itemDisplay.setBrightness(GridUtil.KDB);
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
    public static InteractUnit rayTraceUnit(Player p) {
        var result = rayTraceEntity(p.getEyeLocation().toVector(), getDirection(p.getYaw(), p.getPitch()), p.getLocation().getWorld().getNearbyEntitiesByType(ItemDisplay.class, p.getLocation(), 5));
        if (result == null) return null;
        return asInteractUnit(result);
    }
}
