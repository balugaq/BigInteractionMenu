/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import com.balugaq.bim.BIMLoader;
import com.balugaq.bim.BIMMain;
import com.balugaq.bim.general.BlockPos;
import com.balugaq.bim.general.TransformationBuilder;
import com.balugaq.bim.events.PlayerOffGridEvent;
import com.balugaq.bim.events.PlayerOffHoverUnitEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * @author balugaq
 */
@NullMarked
public class GridUtil {
    private final Plugin instance;
    public final NamespacedKey TAG;
    public GridUtil(Plugin instance) {
        this.instance = instance;
        this.TAG = new NamespacedKey(instance, "tag");
    }

    public static final Display.Brightness MDB = new Display.Brightness(15, 15);
    public static final Display.Brightness KDB = new Display.Brightness(12, 12);
    public static final int SHOW_DISTANCE = 10;
    public static final int HIDE_DISTANCE = 50;

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

    /**
     * Bukkit 的 rayTrace 扫不到 Display 实体
     * 这个 rayTrace 是一个点+方向的形式，以空间中点到直线的距离为标准
     * rayTrace 如图所示，左边是玩家的眼睛，右边是一些 candidates 坐标
     * 玩家观察的不同方向（yaw, pitch）决定了 direction 方向
     *       ·
     *     /   ·
     *   /      ·
     * ○ - - - - ·
     *   \      ·
     *     \   ·
     *       ·
     *
     */
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

    /**
     * 简短格式化数字，例如：
     * 123 -> 123
     * -123 -> -123
     * 1456 -> 1.4K
     * -1456 -> -1.4K
     * 40964096 -> 40.9M
     * -40964096 -> -40.9M
     */
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

    public BIMLoader getLoader() {
        return BIMLoader.get(instance);
    }

    /**
     * 生成 InteractUnit + 背景（可选）以放置一个 Grid
     */
    public ActiveGrid placeGrid(Location location, GridPreset option) {
        // 先去除已经存在的
        var pos = BlockPos.from(location);
        var cache = getLoader().getCache();
        var act = cache.activeGrids().get(pos);
        if (act != null) {
            removeGrid(pos, act.getOccupiedBoundingBox());
        } else {
            for (var ori : GridOrientation.values()) {
                removeGrid(pos, option.getOccupiedBoundingBox(location, ori));
            }
        }

        var o = GridOrientation.fromYawPitch(location.getYaw(), location.getPitch());
        var active = new ActiveGrid(location,  option, o);
        var units = new Int2ObjectOpenHashMap<InteractUnit>();
        active.units = units;
        var gap = option.getGap();
        for (int h = 0; h < option.getHeight(); h++) {
            for (int w = 0; w < option.getWidth(); w++) {
                Location loc = o.apply(location, gap * (w + 0.5), gap * (option.getHeight() - h - 0.5), 0);
                int i = h * option.getWidth() + w;
                var u = new InteractUnit(i, loc, active);
                units.put(i, u);
                cache.index().put(u.itemDisplay, u);
                cache.index().put(u.titleDisplay, u);
                cache.index().put(u.amountDisplay, u);
                option.init(active, i, u);
            }
        }
        cache.activeGrids().put(pos, active);
        if (option.defaultBackground()) {
            addDefaultBackground(active, location);
        }
        option.postInit(active);
        return active;
    }

    private void addDefaultBackground(ActiveGrid active, Location location) {
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

        // 灰色背景，用若干个 TextDisplay 实现
        // 因为如果只用 1 个时，可能会因为 text 过大超出 65535 字节，客户端收包会失败
        for (int h = 1; h <= t; h++) {
            var bg = location.getWorld().spawn(o.apply(location, gap * (option.getWidth() / 2f + 0.5), gap * (option.getHeight() - 0.5) - heightPerText * h - 0.003, 0.001), TextDisplay.class);
            bg.setRotation(o.getYaw(), o.getPitch());
            bg.text(ht);
            bg.setTextOpacity((byte) 0);
            bg.setTransformation(TransformationBuilder.create().scale(scale * 2f / precision).build());
            bg.setBackgroundColor(Color.fromRGB(0x8B8B8B));
            bg.setBrightness(MDB);
            bg.setLineWidth(999999);
            active.tag(bg);
            active.backgrounds.add(bg);
        }

        var s = ".\n".repeat(Math.round(blockHeight / heightPerText * precision));
        s = s.substring(0, s.length() - 1);
        var wt = Component.text(s);
        // 白色竖条
        for (int w = 0; w <= option.getWidth(); w++) {
            var divider = location.getWorld().spawn(o.apply(location, gap * (w + 0.5), -gap * 0.5, 0.002), TextDisplay.class);
            divider.setRotation(o.getYaw(), o.getPitch());
            divider.text(wt);
            divider.setTextOpacity((byte) 0);
            divider.setTransformation(TransformationBuilder.create().scale(scale / precision).build());
            divider.setBackgroundColor(Color.fromRGB(0xC3C3C3));
            divider.setBrightness(MDB);
            divider.setLineWidth(1);
            active.tag(divider);
            active.backgrounds.add(divider);
        }

        // 白色横条
        for (int h = 0; h <= option.getHeight(); h++) {
            var divider = location.getWorld().spawn(o.apply(location, gap * (option.getWidth() / 2f + 0.5), gap * (option.getHeight() - h - 0.5) - 0.003, 0.003), TextDisplay.class);
            divider.setRotation(o.getYaw(), o.getPitch());
            divider.text(Component.text(".".repeat(Math.round(textWidth * 2f))));
            divider.setTextOpacity((byte) 0);
            divider.setTransformation(TransformationBuilder.create().scale(scale / 4f / precision).build());
            divider.setBackgroundColor(Color.fromRGB(0xC3C3C3));
            divider.setBrightness(MDB);
            divider.setLineWidth(999999);
            active.tag(divider);
            active.backgrounds.add(divider);
        }
    }

    public void removeGrid(BlockPos pos, BoundingBox boundingBox) {
        var cache = getLoader().getCache();
        var active = cache.activeGrids().remove(pos);
        if (active == null) {
            var entities = pos.toLocation().getWorld().getNearbyEntities(boundingBox);
            for (var e : entities) {
                var c = e.getPersistentDataContainer().get(TAG, PersistentDataType.STRING);
                // 只删对应坐标tag的
                if (pos.getTag().equals(c)) {
                    e.remove();
                }
            }
            return;
        }
        active.option.onDestroy(active);
        for (var display : active.backgrounds) {
            display.remove();
        }
        cache.watching().values().removeIf(u ->
            u.itemDisplay.isDead() || !u.itemDisplay.isValid()
            || u.titleDisplay.isDead() || !u.titleDisplay.isValid()
            || u.amountDisplay.isDead() || !u.amountDisplay.isValid()
        );
    }

    public void offGrid(Player player) {
        var u = getLoader().getCache().watching().remove(player.getUniqueId());
        if (u != null) {
            new PlayerOffGridEvent(player, u.grid).callEvent();
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
    public InteractUnit rayTraceUnit(Player p) {
        var result = rayTraceEntity(p.getEyeLocation().toVector(), getDirection(p.getYaw(), p.getPitch()), p.getLocation().getWorld().getNearbyEntitiesByType(ItemDisplay.class, p.getLocation(), 5));
        if (result == null) return null;
        return getLoader().getCache().index().get(result);
    }
}
