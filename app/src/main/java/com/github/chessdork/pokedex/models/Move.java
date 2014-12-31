package com.github.chessdork.pokedex.models;


import android.database.Cursor;

public class Move {
    private final int accuracy, power, pp;
    private final PokemonType type;
    private final String name, description;
    private final MoveCategory category;

    public Move(Cursor c) {
        this.name = c.getString(0);
        this.type = PokemonType.valueOf(c.getString(1).toUpperCase());
        this.accuracy = c.getInt(2);
        this.power = c.getInt(3);
        this.pp = c.getInt(4);
        this.category = MoveCategory.valueOf(c.getString(5).toUpperCase().replaceAll("-","_"));
        this.description = c.getString(6);
    }

    public Move(String name, PokemonType type, int accuracy, int power, int pp,
                MoveCategory category, String description) {
        this.name = name;
        this.description = description;
        this.accuracy = accuracy;
        this.power = power;
        this.pp = pp;
        this.type = type;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getPower() {
        return power;
    }

    public int getPp() {
        return pp;
    }

    public PokemonType getType() {
        return type;
    }

    public MoveCategory getCategory() {
        return category;
    }

    public String toString() {
        return name;
    }
}
