package com.github.chessdork.smogon.models;

import android.graphics.drawable.GradientDrawable;

import com.github.chessdork.smogon.R;

public enum MoveCategory {
    PHYSICAL(R.drawable.ic_physical, 0xffdc7b69, 0xffd25640, 0xff73241f, "physical"),
    SPECIAL(R.drawable.ic_special, 0xff7590be, 0xff5274ae, 0xff4a3932, "special"),
    NON_DAMAGING(R.drawable.ic_non_damaging, 0xffc3bdb1, 0xffada594, 0xff525252, "non-damaging");

    private final int resId, color1, color2, borderColor;
    private final String name;

    MoveCategory(int resId, int color1, int color2, int borderColor, String name) {
        this.resId = resId;
        this.color1 = color1;
        this.color2 = color2;
        this.borderColor = borderColor;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }

    private static final int BORDER_STROKE = 2;
    private static final float CORNER_RADIUS = 4;

    public GradientDrawable createGradient() {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{color1, color2}
        );
        gradient.setStroke(BORDER_STROKE, borderColor);
        gradient.setCornerRadius(CORNER_RADIUS);

        return gradient;
    }
}