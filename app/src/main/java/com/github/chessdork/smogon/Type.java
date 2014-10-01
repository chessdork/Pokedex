package com.github.chessdork.smogon;



public enum Type {
    NORMAL("NORMAL", 0xffa8a878, 0xff8a8a59, 0xff79794E),
    FIRE("FIRE", 0xfff08030, 0xffdd6610, 0xffB4530D),
    WATER("WATER", 0xff6890f0, 0xff386ceb, 0xff1753E3),
    ELECTRIC("ELECTRIC", 0xfff8d030, 0xfff0c108, 0xffC19B07),
    GRASS("GRASS", 0xff78c850, 0xff5ca935, 0xff4A892B),
    ICE("ICE", 0xff98d8d8, 0xff69c6c6, 0xff45B6B6),
    FIGHTING("FIGHTING", 0xffc03028, 0xff9d2721, 0xff82211B),
    POISON("POISON", 0xffa040a0, 0xff803380, 0xff662966),
    GROUND("GROUND", 0xffe0c068, 0xffd4a82f, 0xffAA8623),
    FLYING("FLYING", 0xffa890f0, 0xff9180c4, 0xff7762B6),
    PSYCHIC("PSYCHIC", 0xfff85888, 0xfff61c5d, 0xffD60945),
    BUG("BUG", 0xffa8b820, 0xff8d9a1b, 0xff616B13),
    ROCK("ROCK", 0xffb8a038, 0xff93802d, 0xff746523),
    GHOST("GHOST", 0xff705898, 0xff554374, 0xff413359),
    DRAGON("DRAGON", 0xff7038f8, 0xff4c08ef, 0xff3D07C0),
    DARK("DARK", 0xff705848, 0xff513f34, 0xff362A23),
    STEEL("STEEL", 0xffb8b8d0, 0xff9797ba, 0xff7A7AA7),
    FAIRY("FAIRY", 0xfff98cff, 0xfff540ff, 0xffC1079B);

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
}
