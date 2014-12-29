package com.github.chessdork.pokedex.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    public Pokemon(Cursor c) {
        this.name = c.getString(0);
        this.hp = c.getInt(1);
        this.attack = c.getInt(2);
        this.defense = c.getInt(3);
        this.specialAttack = c.getInt(4);
        this.specialDefense = c.getInt(5);
        this.speed = c.getInt(6);
        String[] typeString = c.getString(7).split(",");
        this.types = new ArrayList<>(typeString.length);

        for (String type : typeString) {
            types.add(PokemonType.valueOf(type.toUpperCase()));
        }
    }

    private final String name;
    private final List<PokemonType> types;
    private final int hp;
    private final int attack;
    private final int defense;
    private final int specialAttack;
    private final int specialDefense;
    private final int speed;

    public Pokemon(String name, List<PokemonType> types, int hp, int attack, int defense,
                    int specialAttack, int specialDefense, int speed) {
        this.name = name;
        this.types = types;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public String toString() {
        return name;
    }
}
