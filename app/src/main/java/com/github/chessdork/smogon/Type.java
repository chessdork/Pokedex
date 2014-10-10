package com.github.chessdork.smogon;


import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

public enum Type {
    NORMAL("Normal", 0xffa8a878, 0xff8a8a59, 0xff79794E),
    FIRE("Fire", 0xfff08030, 0xffdd6610, 0xffB4530D),
    WATER("Water", 0xff6890f0, 0xff386ceb, 0xff1753E3),
    ELECTRIC("Electric", 0xfff8d030, 0xfff0c108, 0xffC19B07),
    GRASS("Grass", 0xff78c850, 0xff5ca935, 0xff4A892B),
    ICE("Ice", 0xff98d8d8, 0xff69c6c6, 0xff45B6B6),
    FIGHTING("Fighting", 0xffc03028, 0xff9d2721, 0xff82211B),
    POISON("Poison", 0xffa040a0, 0xff803380, 0xff662966),
    GROUND("Ground", 0xffe0c068, 0xffd4a82f, 0xffAA8623),
    FLYING("Flying", 0xffa890f0, 0xff9180c4, 0xff7762B6),
    PSYCHIC("Psychic", 0xfff85888, 0xfff61c5d, 0xffD60945),
    BUG("Bug", 0xffa8b820, 0xff8d9a1b, 0xff616B13),
    ROCK("Rock", 0xffb8a038, 0xff93802d, 0xff746523),
    GHOST("Ghost", 0xff705898, 0xff554374, 0xff413359),
    DRAGON("Dragon", 0xff7038f8, 0xff4c08ef, 0xff3D07C0),
    DARK("Dark", 0xff705848, 0xff513f34, 0xff362A23),
    STEEL("Steel", 0xffb8b8d0, 0xff9797ba, 0xff7A7AA7),
    FAIRY("Fairy", 0xfff98cff, 0xfff540ff, 0xffC1079B);

    private final String name;
    private final int color1, color2, borderColor;

    Type(String name, int color1, int color2, int borderColor) {
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
        this.borderColor = borderColor;
    }

    public String getName() {
        return name;
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

    private static final float r = 4;
    private static final float[] LEFT_CORNERS = {r, r, 0, 0, 0, 0, r, r};
    private static final float[] RIGHT_CORNERS = {0, 0, r, r, r, r, 0, 0};

    private static GradientDrawable[] createGradient(Type[] types) {
        GradientDrawable[] drawables = new GradientDrawable[types.length];

        for (int i = 0; i < drawables.length; i++) {
            drawables[i] = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] {types[i].getColor1(), types[i].getColor2()} );
            drawables[i].setCornerRadius(r);
            drawables[i].setStroke(1, types[i].getBorderColor());
        }

        if (types.length > 1) {
            drawables[0].setCornerRadii(LEFT_CORNERS);
            drawables[1].setCornerRadii(RIGHT_CORNERS);
        }
        return drawables;
    }

    // setBackgroundDrawable renamed to setBackground in API 16.  It is deprecated, but
    // setBackground simply calls setBackgroundDrawable, so we're okay to use it here and
    // suppress warnings.
    @SuppressWarnings("deprecation")
    public static void setupTypeView(TextView type1, TextView type2, Type[] types) {
        GradientDrawable[] gradients = createGradient(types);

        type1.setText(types[0].getName());
        type1.setBackgroundDrawable(gradients[0]);

        if (types.length > 1) {
            type2.setText(types[1].getName());
            type2.setBackgroundDrawable(gradients[1]);
            type2.setVisibility(View.VISIBLE);
        } else {
            type2.setVisibility(View.INVISIBLE);
        }
    }
}
