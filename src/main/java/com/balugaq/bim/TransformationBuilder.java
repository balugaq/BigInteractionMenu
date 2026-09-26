package com.balugaq.bim;

import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * {@link Transformation} 的链式构建器。
 */
public class TransformationBuilder {

    private Vector3f translation = new Vector3f(0f, 0f, 0f);
    private Quaternionf leftRotation = new Quaternionf(0f, 0f, 0f, 1f);
    private Vector3f scale = new Vector3f(1f, 1f, 1f);
    private Quaternionf rightRotation = new Quaternionf(0f, 0f, 0f, 1f);

    public TransformationBuilder() {
    }

    public static TransformationBuilder create() {
        return new TransformationBuilder();
    }

    // ---------- translation ----------

    public TransformationBuilder translation(@NotNull Vector3f translation) {
        this.translation = new Vector3f(translation);
        return this;
    }

    public TransformationBuilder translation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return this;
    }

    // ---------- leftRotation ----------

    public TransformationBuilder leftRotation(@NotNull Quaternionf leftRotation) {
        this.leftRotation = new Quaternionf(leftRotation);
        return this;
    }

    public TransformationBuilder leftRotation(@NotNull AxisAngle4f leftRotation) {
        this.leftRotation = new Quaternionf(leftRotation);
        return this;
    }

    public TransformationBuilder leftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternionf(x, y, z, w);
        return this;
    }

    // ---------- scale ----------

    public TransformationBuilder scale(@NotNull Vector3f scale) {
        this.scale = new Vector3f(scale);
        return this;
    }

    public TransformationBuilder scale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public TransformationBuilder scale(float uniform) {
        this.scale = new Vector3f(uniform, uniform, uniform);
        return this;
    }

    // ---------- rightRotation ----------

    public TransformationBuilder rightRotation(@NotNull Quaternionf rightRotation) {
        this.rightRotation = new Quaternionf(rightRotation);
        return this;
    }

    public TransformationBuilder rightRotation(@NotNull AxisAngle4f rightRotation) {
        this.rightRotation = new Quaternionf(rightRotation);
        return this;
    }

    public TransformationBuilder rightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternionf(x, y, z, w);
        return this;
    }

    // ---------- build ----------

    public Transformation build() {
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

    @Override
    public String toString() {
        return "TransformationBuilder{" +
                "translation=" + translation +
                ", leftRotation=" + leftRotation +
                ", scale=" + scale +
                ", rightRotation=" + rightRotation +
                '}';
    }
}