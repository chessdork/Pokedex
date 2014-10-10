package com.github.chessdork.smogon;


import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pokemon implements Serializable {
    private static List<Pokemon> masterList;

    private final String name, tag, alias;
    private final int hp, patk, pdef, spe, spatk, spdef;
    private final Type[] types;
    private final String[] abilityNames;

    public Pokemon(String name, String tag, String alias, int hp, int patk, int pdef, int spe,
                   int spatk, int spdef, Type[] types, String[] abilityNames) {
        this.name = name;
        this.tag = tag;
        this.alias = alias;
        this.hp = hp;
        this.patk = patk;
        this.pdef = pdef;
        this.spe = spe;
        this.spatk = spatk;
        this.spdef = spdef;
        this.types = types;
        this.abilityNames = abilityNames;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getAlias() {
        return alias;
    }

    public int getHp() {
        return hp;
    }

    public int getPatk() {
        return patk;
    }

    public int getPdef() {
        return pdef;
    }

    public int getSpe() {
        return spe;
    }

    public int getSpatk() {
        return spatk;
    }

    public int getSpdef() {
        return spdef;
    }

    public Type[] getTypes() {
        return types;
    }

    public String[] getAbilityNames() {
        return abilityNames;
    }

    public static List<Pokemon> getPokemon(Resources resources) {
        if (masterList == null) {
            InputStream is = resources.openRawResource(R.raw.pokemon);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            masterList = new ArrayList<>();

            try {
                StringBuilder text = new StringBuilder();
                String line;

                while ( (line = br.readLine()) != null) {
                    text.append(line);
                }

                JSONObject response = new JSONObject(text.toString());
                JSONArray pokemonArray = response.getJSONArray("result");

                for (int i = 0; i < pokemonArray.length(); i++) {
                    Pokemon pokemon = parsePokemon(pokemonArray.getJSONObject(i));
                    if (pokemon != null) {
                        masterList.add(pokemon);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return masterList;
    }

    public static Pokemon parsePokemon(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            String alias = jsonObject.getString("base_alias");

            // only one or zero tags, for now.
            JSONArray tagsArray = jsonObject.getJSONArray("tags");
            String tag;
            if (tagsArray.length() == 0) {
                Log.d("Pokedex", name + " is missing tag.  Defaulting to: Uber.");
                tag = "Uber";
            } else {
                tag = tagsArray.getJSONObject(0).getString("shorthand");
            }

            int hp = jsonObject.getInt("hp");
            int patk = jsonObject.getInt("patk");
            int pdef = jsonObject.getInt("pdef");
            int spe = jsonObject.getInt("spe");
            int spatk = jsonObject.getInt("spatk");
            int spdef = jsonObject.getInt("spdef");

            JSONArray typesArray = jsonObject.getJSONArray("types");
            int numTypes = typesArray.length();
            Type[] types = new Type[numTypes];

            for (int i = 0; i < numTypes; i++) {
                // types are provided in Camelcase, and enums are ALL_CAPS
                types[i] = Type.valueOf(
                        typesArray.getJSONObject(i).getString("name").toUpperCase() );
            }

            JSONArray abilitiesArray = jsonObject.getJSONArray("abilities");
            int numAbilities = abilitiesArray.length();
            String[] abilities = new String[numAbilities];

            for (int i = 0; i < numAbilities; i++) {
                abilities[i] = abilitiesArray.getJSONObject(i).getString("name");
            }
            return new Pokemon(name, tag, alias, hp, patk, pdef, spe, spatk, spdef,
                    types, abilities);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
