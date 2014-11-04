package com.github.chessdork.pokedex.models;


public enum StatType {
    ATTACK("Attack", "Atk"),
    DEFENSE("Defense", "Def"),
    SP_ATK("Special Attack", "SpA"),
    SP_DEF("Special Defense", "SpD"),
    SPEED("Speed", "Spe"),
    HP("HP", "HP"),
    NONE("N/A", "---");

    private final String name, shorthand;

    StatType(String name, String shorthand) {
        this.name = name;
        this.shorthand = shorthand;
    }

    public String getName() {
        return name;
    }

    public String getShorthand() {
        return shorthand;
    }

    public String toString() {
        return name;
    }
}
