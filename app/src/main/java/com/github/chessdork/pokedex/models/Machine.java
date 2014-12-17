package com.github.chessdork.pokedex.models;


import android.database.Cursor;

public class Machine {
    private final String name, location, move;
    private final PokemonType type;
    private final MoveCategory category;

    public Machine(String name, String location, Move move) {
        this.name = name;
        this.location = location;
        this.move = move.getName();
        this.type = move.getType();
        this.category = move.getCategory();
    }

    public Machine(String name, String location, String move, PokemonType type, MoveCategory category) {
        this.name = name;
        this.location = location;
        this.move = move;
        this.type = type;
        this.category = category;
    }

    public Machine(Cursor c) {
        this.name = c.getString(0);
        this.location = c.getString(1);
        this.move = c.getString(2);
        this.type = PokemonType.valueOf(c.getString(3).toUpperCase());
        this.category = MoveCategory.valueOf(c.getString(4).toUpperCase().replaceAll("-","_"));
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getMove() {
        return move;
    }

    public PokemonType getType() {
        return type;
    }

    public MoveCategory getCategory() {
        return category;
    }

    // enable searching on the TM/HM number or move name.
    public String toString() {
        return name + move;
    }

}
