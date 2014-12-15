package com.github.chessdork.pokedex.common;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class PokeDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "pokedex.db";
    private static final int DATABASE_VERSION = 1;

    private static PokeDatabase database;

    public PokeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // We're providing all the data, so just overwrite existing db on upgrade.
        setForcedUpgrade();
    }

    public synchronized static PokeDatabase getInstance(Context context) {
        if (database == null) {
            database = new PokeDatabase(context);
        }
        return database;
    }
}
