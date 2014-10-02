package com.github.chessdork.smogon;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pokemon {
    private final String name, tag;
    private final int hp, patk, pdef, spe, spatk, spdef;
    private final Type[] types;
    private final String[] abilityNames;

    public Pokemon(String name, String tag, int hp, int patk, int pdef, int spe, int spatk,
                   int spdef, Type[] types, String[] abilityNames) {
        this.name = name;
        this.tag = tag;
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

    public static Pokemon parsePokemon(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");

            // only one or zero tags, for now.
            JSONArray tagsArray = jsonObject.getJSONArray("tags");
            String tag = tagsArray.length() > 0 ? tagsArray.getJSONObject(0).getString("shorthand")
                                                : "";
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
            return new Pokemon(name, tag, hp, patk, pdef, spe, spatk, spdef, types, abilities);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
