package com.github.chessdork.smogon;


public enum Pokemon {
    ROTOM_WASH("Rotom-Wash"),
    ROTOM_MOW("Rotom-Mow");

    private final String name;

    Pokemon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
