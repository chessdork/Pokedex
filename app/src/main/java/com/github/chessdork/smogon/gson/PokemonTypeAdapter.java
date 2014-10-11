package com.github.chessdork.smogon.gson;

import android.util.Log;

import com.github.chessdork.smogon.models.PokemonType;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Deserializes an object such as the following
 * <code>
 * {
 * "alias":"fire",
 * "name":"Fire",
 * "gen":"xy"
 * }
 * </code>
 * into the correct PokemonType. Only uses the "alias" field
 */
public class PokemonTypeAdapter extends TypeAdapter<PokemonType> {

    private static final String TAG = PokemonTypeAdapter.class.getSimpleName();

    @Override
    public void write(JsonWriter out, PokemonType value) throws IOException {
        // Don't care about write
    }

    @Override
    public PokemonType read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        PokemonType type = PokemonType.NORMAL;
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("alias")) {
                String value = in.nextString();
                try {
                    type = PokemonType.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "Unknown type: " + type);
                }
            } else {
                in.skipValue();
            }
        }
        in.endObject();

        return type;
    }
}
