package com.github.chessdork.pokedex.models;

import android.graphics.drawable.GradientDrawable;

public enum PokemonType {
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
    private final int color1;
    private final int color2;
    private final int borderColor;

    PokemonType(String name, int color1, int color2, int borderColor) {
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
        this.borderColor = borderColor;
    }

    public String getName() {
        return name;
    }

    private static final int BORDER_STROKE = 2;
    private static final float CORNER_RADIUS = 4;
    private static final float[] LEFT_CORNERS = {CORNER_RADIUS, CORNER_RADIUS, 0, 0, 0, 0, CORNER_RADIUS, CORNER_RADIUS};
    private static final float[] RIGHT_CORNERS = {0, 0, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, 0, 0};

    public GradientDrawable createGradient() {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color1, color2}
        );
        gradient.setStroke(BORDER_STROKE, borderColor);
        gradient.setCornerRadius(CORNER_RADIUS);
        return gradient;
    }

    public GradientDrawable createLeftGradient() {
        GradientDrawable gradient = createGradient();
        gradient.setCornerRadii(LEFT_CORNERS);
        return gradient;
    }

    public GradientDrawable createRightGradient() {
        GradientDrawable gradient = createGradient();
        gradient.setCornerRadii(RIGHT_CORNERS);
        return gradient;
    }
}
