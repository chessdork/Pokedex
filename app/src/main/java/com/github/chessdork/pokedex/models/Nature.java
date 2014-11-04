package com.github.chessdork.pokedex.models;


public enum Nature {
    HARDY("Hardy", StatType.NONE, StatType.NONE),
    LONELY("Lonely", StatType.ATTACK, StatType.DEFENSE),
    BRAVE("Brave", StatType.ATTACK, StatType.SPEED),
    ADAMANT("Adamant", StatType.ATTACK, StatType.SP_ATK),
    NAUGHTY("Naughty", StatType.ATTACK, StatType.SP_DEF),
    BOLD("Bold", StatType.DEFENSE, StatType.ATTACK),
    DOCILE("Docile", StatType.NONE, StatType.NONE),
    RELAXED("Relaxed", StatType.DEFENSE, StatType.SPEED),
    IMPISH("Impish", StatType.DEFENSE, StatType.SP_ATK),
    LAX("Lax", StatType.DEFENSE, StatType.SP_DEF),
    TIMID("Timid", StatType.SPEED, StatType.ATTACK),
    HASTY("Hasty", StatType.SPEED, StatType.DEFENSE),
    SERIOUS("Serious", StatType.NONE, StatType.NONE),
    JOLLY("Jolly", StatType.SPEED, StatType.SP_ATK),
    NAIVE("Naive", StatType.SPEED, StatType.SP_DEF),
    MODEST("Modest", StatType.SP_ATK, StatType.ATTACK),
    MILD("Mild", StatType.SP_ATK, StatType.DEFENSE),
    QUIET("Quiet", StatType.SP_ATK, StatType.SPEED),
    BASHFUL("Bashful", StatType.NONE, StatType.NONE),
    RASH("Rash", StatType.SP_ATK, StatType.SP_DEF),
    CALM("Calm", StatType.SP_DEF, StatType.ATTACK),
    GENTLE("Gentle", StatType.SP_DEF, StatType.DEFENSE),
    SASSY("Sassy", StatType.SP_DEF, StatType.SPEED),
    CAREFUL("Careful", StatType.SP_DEF, StatType.SP_ATK),
    QUIRKY("Quirky", StatType.NONE, StatType.NONE);

    private final String name;
    private final StatType increased, decreased;

    Nature(String name, StatType increased, StatType decreased) {
        this.name = name;
        this.increased = increased;
        this.decreased = decreased;
    }

    public String getName() {
        return name;
    }

    public StatType getIncreased() {
        return increased;
    }

    public StatType getDecreased() {
        return decreased;
    }
}

