package com.github.chessdork.pokedex.models;

import android.database.Cursor;
import android.provider.BaseColumns;

public class Ability {
    private final int id;
    private final String name;
    private final String description;

    public Ability(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static final class Columns implements BaseColumns {
        public static final int ABILITY_ID = 0;
        public static final int ABILITY_NAME = 1;
        public static final int ABILITY_DESCRIPTION = 2;
    }

    public Ability(Cursor c) {
        this.id = c.getInt(Columns.ABILITY_ID);
        this.name = c.getString(Columns.ABILITY_NAME);
        this.description = c.getString(Columns.ABILITY_DESCRIPTION);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return name;
    }
}
