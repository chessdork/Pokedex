package com.github.chessdork.smogon;


public enum Pokemon {
    ROTOM_WASH(R.drawable.rotom_wash, "Rotom-Wash");

    private final int resId;
    private final String name;

    Pokemon(int resId, String name) {
        this.resId = resId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }
}
