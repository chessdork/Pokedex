package com.github.chessdork.pokedex.models;

import android.graphics.drawable.GradientDrawable;

import java.util.Arrays;
import java.util.List;

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

    // Type match-ups cannot be initialized in constructors due to illegal forward references.
    static {
        NORMAL.strongTo = Arrays.asList();
        NORMAL.resistsFrom = Arrays.asList();
        NORMAL.weakTo = Arrays.asList(ROCK, STEEL);
        NORMAL.weakFrom = Arrays.asList(FIGHTING);

        FIRE.strongTo = Arrays.asList(GRASS, ICE, BUG, STEEL);
        FIRE.resistsFrom = Arrays.asList(FIRE, BUG, GRASS, ICE, STEEL, FAIRY);
        FIRE.weakTo = Arrays.asList(FIRE, WATER, ROCK, DRAGON);
        FIRE.weakFrom = Arrays.asList(WATER, GROUND, ROCK);

        WATER.strongTo = Arrays.asList(FIRE, GROUND, ROCK);
        WATER.resistsFrom = Arrays.asList(FIRE, WATER, ICE, STEEL);
        WATER.weakTo = Arrays.asList(WATER, GRASS, DRAGON);
        WATER.weakFrom = Arrays.asList(GRASS, ELECTRIC);

        ELECTRIC.strongTo = Arrays.asList(WATER, FLYING);
        ELECTRIC.resistsFrom = Arrays.asList(ELECTRIC, FLYING, STEEL);
        ELECTRIC.weakTo = Arrays.asList(GRASS, ELECTRIC, DRAGON);
        ELECTRIC.weakFrom = Arrays.asList(GROUND);

        GRASS.strongTo = Arrays.asList(WATER, GROUND, ROCK);
        GRASS.resistsFrom = Arrays.asList(WATER, GRASS, ELECTRIC, GROUND);
        GRASS.weakTo = Arrays.asList(FIRE, GRASS, POISON, FLYING, BUG, DRAGON, STEEL);
        GRASS.weakFrom = Arrays.asList(FIRE, ICE, FLYING, POISON, BUG);

        ICE.strongTo = Arrays.asList(GRASS, GROUND, FLYING, DRAGON);
        ICE.resistsFrom = Arrays.asList(ICE);
        ICE.weakTo = Arrays.asList(FIRE, WATER, ICE, STEEL);
        ICE.weakFrom = Arrays.asList(FIRE, FIGHTING, ROCK, STEEL);

        FIGHTING.strongTo = Arrays.asList(NORMAL, ICE, ROCK, DARK, STEEL);
        FIGHTING.resistsFrom = Arrays.asList(BUG, ROCK, DARK);
        FIGHTING.weakTo = Arrays.asList(POISON, FLYING, PSYCHIC, BUG, FAIRY);
        FIGHTING.weakFrom = Arrays.asList(FLYING, PSYCHIC, FAIRY);

        POISON.strongTo = Arrays.asList(GRASS, FAIRY);
        POISON.resistsFrom = Arrays.asList(GRASS, FIGHTING, POISON, BUG, FAIRY);
        POISON.weakTo = Arrays.asList(POISON, GROUND, ROCK, GHOST);
        POISON.weakFrom = Arrays.asList(GROUND, PSYCHIC);

        GROUND.strongTo = Arrays.asList(FIRE, ELECTRIC, POISON, ROCK, STEEL);
        GROUND.resistsFrom = Arrays.asList(POISON, ROCK);
        GROUND.weakTo = Arrays.asList(BUG, GRASS);
        GROUND.weakFrom = Arrays.asList(WATER, GRASS, ICE);

        FLYING.strongTo = Arrays.asList();
        FLYING.resistsFrom = Arrays.asList();
        FLYING.weakTo = Arrays.asList();
        FLYING.weakFrom = Arrays.asList();

        PSYCHIC.strongTo = Arrays.asList();
        PSYCHIC.resistsFrom = Arrays.asList();
        PSYCHIC.weakTo = Arrays.asList();
        PSYCHIC.weakFrom = Arrays.asList();

        BUG.strongTo = Arrays.asList();
        BUG.resistsFrom = Arrays.asList();
        BUG.weakTo = Arrays.asList();
        BUG.weakFrom = Arrays.asList();

        ROCK.strongTo = Arrays.asList();
        ROCK.resistsFrom = Arrays.asList();
        ROCK.weakTo = Arrays.asList();
        ROCK.weakFrom = Arrays.asList();

        GHOST.strongTo = Arrays.asList();
        GHOST.resistsFrom = Arrays.asList();
        GHOST.weakTo = Arrays.asList();
        GHOST.weakFrom = Arrays.asList();

        DRAGON.strongTo = Arrays.asList();
        DRAGON.resistsFrom = Arrays.asList();
        DRAGON.weakTo = Arrays.asList();
        DRAGON.weakFrom = Arrays.asList();

        DARK.strongTo = Arrays.asList();
        DARK.resistsFrom = Arrays.asList();
        DARK.weakTo = Arrays.asList();
        DARK.weakFrom = Arrays.asList();

        STEEL.strongTo = Arrays.asList();
        STEEL.resistsFrom = Arrays.asList();
        STEEL.weakTo = Arrays.asList();
        STEEL.weakFrom = Arrays.asList();

        FAIRY.strongTo = Arrays.asList();
        FAIRY.resistsFrom = Arrays.asList();
        FAIRY.weakTo = Arrays.asList();
        FAIRY.weakFrom = Arrays.asList();
    }

    private final String name;
    private final int color1;
    private final int color2;
    private final int borderColor;
    private List<PokemonType> strongTo, weakTo, resistsFrom, weakFrom;

    PokemonType(String name, int color1, int color2, int borderColor) {
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
        this.borderColor = borderColor;
    }

    public List<PokemonType> getStrongTo() {
        return strongTo;
    }

    public List<PokemonType> getResistsFrom() {
        return resistsFrom;
    }

    public List<PokemonType> getWeakTo() {
        return weakTo;
    }

    public List<PokemonType> getWeakFrom() {
        return weakFrom;
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
