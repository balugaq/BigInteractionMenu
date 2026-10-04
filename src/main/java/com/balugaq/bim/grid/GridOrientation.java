/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

/**
 * 网格的平面与朝向。
 * <p>
 * 内部用一组正交基向量表达任意朝向（理论支持全部旋转方向）：
 * <ul>
 *     <li>{@code widthDir (u)}：宽度方向，w 增大的一侧；</li>
 *     <li>{@code upDir}：网格"上方"，h 减小的一侧（即行号 h 增大朝 {@code -upDir}）；</li>
 *     <li>{@code normal (n)}：深度方向，指向观察者的一侧，层级偏移（背景/文字前后关系）沿它展开。</li>
 * </ul>
 * 约束 {@code u × down = -n}（down = -upDir），保证从 n 侧看过去文字不镜像。
 * 对外只暴露以下 6 个合法的平面/方向：
 * <ul>
 *     <li>{@link #XY}：竖直平面，默认朝向（观察者在 +Z / 南侧）；</li>
 *     <li>{@link #XY_REVERSED}：同一平面反转 180°（观察者在 -Z / 北侧）；</li>
 *     <li>{@link #XZ}：水平平面，从上往下看；</li>
 *     <li>{@link #XZ_REVERSED}：水平平面，从下往上看；</li>
 *     <li>{@link #YZ}：竖直平面，观察者在 +X / 东侧；</li>
 *     <li>{@link #YZ_REVERSED}：竖直平面，观察者在 -X / 西侧。</li>
 * </ul>
 *
 * @author balugaq
 */
@NullMarked
@Getter
public enum GridOrientation {
    XY(new Vector(1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, 1),
            0f, 0f,
            new Quaternionf().rotationY((float) Math.PI)),
    XY_REVERSED(new Vector(-1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, -1),
            180f, 0f,
            new Quaternionf()),
    XZ(new Vector(1, 0, 0), new Vector(0, 0, -1), new Vector(0, 1, 0),
            0f, -90f,
            new Quaternionf().rotationX((float) (-Math.PI / 2)).rotateY((float) Math.PI)),
    XZ_REVERSED(new Vector(1, 0, 0), new Vector(0, 0, 1), new Vector(0, -1, 0),
            0f, 90f,
            new Quaternionf().rotationX((float) (Math.PI / 2)).rotateY((float) Math.PI)),
    YZ(new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(1, 0, 0),
            270f, 0f,
            new Quaternionf().rotationY((float) (Math.PI / 2)).rotateY((float) Math.PI)),
    YZ_REVERSED(new Vector(0, 0, 1), new Vector(0, 1, 0), new Vector(-1, 0, 0),
            90f, 0f,
            new Quaternionf().rotationY((float) (-Math.PI / 2)).rotateY((float) Math.PI));

    final Vector widthDir;
    final Vector upDir;
    final Vector normal;
    /**
     * 固定朝向实体（背景板/分隔线）应设置的 yaw，使其正面朝向 {@link #normal}。
     */
    final float yaw;
    /**
     * 固定朝向实体应设置的 pitch，使其正面朝向 {@link #normal}。
     */
    final float pitch;
    /**
     * ItemDisplay（GUI 物品模型）应使用的左旋转四元数，使其正面朝向 {@link #normal}。
     * 基准：GUI 模型经 rotY(180°) 后正面朝 +Z（即 {@link #XY} 的工作配置）。
     */
    final Quaternionf itemRotation;

    GridOrientation(Vector widthDir, Vector upDir, Vector normal,
                    float yaw, float pitch, Quaternionf itemRotation) {
        this.widthDir = widthDir;
        this.upDir = upDir;
        this.normal = normal;
        this.yaw = yaw;
        this.pitch = pitch;
        this.itemRotation = itemRotation;
    }

    /**
     * 把网格本地坐标偏移 (a, b, c) 应用到 base 上：
     * a 沿宽度方向、b 沿网格"上方"、c 沿深度方向。
     * 等价于旧 XY 平面写法里的 {@code add(a, b, c)}。
     */
    public Location apply(Location base, double a, double b, double c) {
        return base.clone()
                .add(widthDir.clone().multiply(a))
                .add(upDir.clone().multiply(b))
                .add(normal.clone().multiply(c));
    }

    public boolean isHorizontal() {
        return this == XZ || this == XZ_REVERSED;
    }

    public GridOrientation reverse() {
        return switch (this) {
            case XZ -> XZ_REVERSED;
            case XZ_REVERSED -> XZ;
            case XY -> XY_REVERSED;
            case XY_REVERSED -> XY;
            case YZ -> YZ_REVERSED;
            case YZ_REVERSED -> YZ;
        };
    }

    /**
     * 根据 yaw/pitch 匹配最接近的合法朝向。
     * <p>
     * yaw 会先归一化到 [0, 360)，pitch 归一化到 [-90, 90]。
     * 采用球面角距离（先比较 pitch，再比较 yaw 的环形差）挑选最优项，
     * 找不到精确匹配时返回最接近的一个，而不是 null。
     *
     * @param yaw   水平角（度，任意实数，自动归一化）
     * @param pitch 俯仰角（度，任意实数，自动归一化）
     * @return 最接近的 {@link GridOrientation}
     */
    public static GridOrientation fromYawPitch(float yaw, float pitch) {
        float y = normalizeYaw(yaw);
        float p = normalizePitch(pitch);

        GridOrientation best = XY;
        float bestScore = Float.MAX_VALUE;
        for (GridOrientation o : values()) {
            // pitch 差（先决条件，权重放大，避免 pitch 差很大却因 yaw 接近而误选）
            float dPitch = Math.abs(p - o.pitch);
            // yaw 环形差，范围 [0, 180]
            float dYaw = yawDistance(y, o.yaw);
            // pitch 差异是主要判据，yaw 作为次要判据
            float score = dPitch * 4f + dYaw;
            if (score < bestScore) {
                bestScore = score;
                best = o;
            }
        }
        return best;
    }

    /** 归一化 yaw 到 [0, 360)。 */
    private static float normalizeYaw(float yaw) {
        float y = yaw % 360f;
        if (y < 0f) y += 360f;
        return y;
    }

    /** 归一化 pitch 到 [-90, 90]。 */
    private static float normalizePitch(float pitch) {
        // 超出范围说明翻转了，映射回等价的最小俯仰
        float p = pitch % 360f;
        if (p < -180f) p += 360f;
        if (p > 180f) p -= 360f;
        if (p > 90f) p = 180f - p;
        if (p < -90f) p = -180f - p;
        return p;
    }

    /** 两个 yaw 之间的最短环形距离，结果范围 [0, 180]。 */
    private static float yawDistance(float a, float b) {
        float d = Math.abs(a - b) % 360f;
        return d > 180f ? 360f - d : d;
    }
}
