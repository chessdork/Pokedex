package com.github.chessdork.smogon.models;

import java.util.List;

public class Pokemon {
    private String name;
    private List<PokemonType> types;

    public String getName() {
        return name;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public String toString() {
        return name;
    }
}
