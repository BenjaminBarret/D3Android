package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

class LegendProperties {
    private static final float DEFAULT_TEXT_SIZE = 15F;

    @NonNull private VerticalAlignment verticalAlignment;
    @NonNull private HorizontalAlignment horizontalAlignment;

    private float offsetX = 0F;
    private float offsetY = 0F;

    private float textSizeInPixels = DEFAULT_TEXT_SIZE;

    @ColorInt private int color;

    public LegendProperties() {
        this(VerticalAlignment.TOP, HorizontalAlignment.RIGHT);
    }

    public LegendProperties(@NonNull VerticalAlignment verticalAlignment) {
        this(verticalAlignment, HorizontalAlignment.RIGHT);
    }

    public LegendProperties(@NonNull HorizontalAlignment horizontalAlignment) {
        this(VerticalAlignment.TOP, horizontalAlignment);
    }

    public LegendProperties(
        @NonNull VerticalAlignment verticalAlignment,
        @NonNull HorizontalAlignment horizontalAlignment
    ) {
        this.verticalAlignment = verticalAlignment;
        this.horizontalAlignment = horizontalAlignment;
        color = Color.rgb(0, 0, 0);
    }

    /**
     * Returns the vertical alignment of the LegendProperties.
     */
    @NonNull public VerticalAlignment verticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment of the LegendProperties.
     */
    public void verticalAlignment(@NonNull VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Returns the horizontal alignment of the LegendProperties.
     */
    @NonNull public HorizontalAlignment horizontalAlignement() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment of the LegendProperties.
     */
    public void horizontalAlignement(@NonNull HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    /**
     * Returns the horizontal offset of the LegendProperties.
     */
    public float offsetX() {
        return offsetX;
    }

    /**
     * Sets the horizontal offset of the LegendProperties.
     */
    public void offsetX(float paddingX) {
        this.offsetX = paddingX;
    }

    /**
     * Returns the vertical offset of the LegendProperties.
     */
    public float offsetY() {
        return offsetY;
    }

    /**
     * Sets the vertical offset of the LegendProperties.
     */
    public void offsetY(float paddingY) {
        this.offsetY = paddingY;
    }


    /**
     * Returns the size of the text of the LegendProperties.
     */
    public float textSizeInPixels() {
        return textSizeInPixels;
    }

    /**
     * Sets the size of the text of the LegendProperties.
     */
    public void textSizeInPixels(float textSizeInPixels) {
        this.textSizeInPixels = textSizeInPixels;
    }

    /**
     * Returns the color of the text of the LegendProperties.
     */
    public int color() {
        return color;
    }

    /**
     * Sets the color of the text of the LegendProperties.
     */
    public void color(@ColorInt int color) {
        this.color = color;
    }
}
