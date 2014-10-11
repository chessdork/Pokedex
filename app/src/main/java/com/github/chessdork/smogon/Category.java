package com.github.chessdork.smogon;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

public enum Category {
    PHYSICAL(R.drawable.ic_physical, 0xffdc7b69, 0xffd25640, 0xff73241f, "physical"),
    SPECIAL(R.drawable.ic_special, 0xff7590be, 0x5274ae, 0x4a3932, "special"),
    NON_DAMAGING(R.drawable.ic_non_damaging, 0xffc3bdb1, 0xffada594, 0xff525252, "non-damaging");

    private final int resId, color1, color2, borderColor;
    private final String name;

    Category(int resId, int color1, int color2, int borderColor, String name) {
        this.resId = resId;
        this.color1 = color1;
        this.color2 = color2;
        this.borderColor = borderColor;
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public String getName() {
        return name;
    }

    private static final int r = 4;

    public static LayerDrawable createDrawable(Category category, Resources resources) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {category.getColor1(), category.getColor2()} );
        gradient.setStroke(1, category.getBorderColor());
        gradient.setCornerRadius(r);

        Drawable drawable = resources.getDrawable(category.getResId());
        return new LayerDrawable(new Drawable[]{gradient, drawable});
    }
}