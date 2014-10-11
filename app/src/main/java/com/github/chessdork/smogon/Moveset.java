package com.github.chessdork.smogon;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Moveset {
    private final String name, description;
    private final List<Item> items;
    private final List<String> abilities;
    private final int[] evConfigs;
    private final List<String>[] moves;
    private final List<Nature> natures;

    public Moveset(String name, String description, List<Item> items, List<String> abilities,
                   int[] evConfigs, List<String>[] moves, List<Nature> natures) {
        this.name = name;
        this.description = description;
        this.items = items;
        this.abilities = abilities;
        this.evConfigs = evConfigs;
        this.moves = moves;
        this.natures = natures;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public int[] getEvConfigs() {
        return evConfigs;
    }

    public List<String>[] getMoves() {
        return moves;
    }

    public List<Nature> getNatures() {
        return natures;
    }

    public static Moveset parseMoveset(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");

            JSONArray itemArray = jsonObject.getJSONArray("items");
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                Item item = Item.parseItem(itemArray.getJSONObject(i));
                if (item != null) {
                    items.add(item);
                }
            }

            JSONArray abilityArray = jsonObject.getJSONArray("abilities");
            List<String> abilities = new ArrayList<>();
            for (int i = 0; i < abilityArray.length(); i++) {
                String ability = abilityArray.getJSONObject(i).getString("name");
                abilities.add(ability);
            }

            // assume there won't be more than one set of EVs.  Verified for all pokemon as of
            // Oct 11 2014.
            //TODO write a test to verify each pokemon only has one set of EVs.
            if (jsonObject.getJSONArray("evconfigs").length() > 1) {
                Log.i("Movesets", name + " has more than one evConfig.");
            }
            JSONObject evs = jsonObject.getJSONArray("evconfigs").getJSONObject(0);
            int[] evConfigs = new int[6];
            evConfigs[0] = evs.getInt("hp");
            evConfigs[1] = evs.getInt("patk");
            evConfigs[2] = evs.getInt("pdef");
            evConfigs[3] = evs.getInt("spatk");
            evConfigs[4] = evs.getInt("spdef");
            evConfigs[5] = evs.getInt("spe");

            JSONArray moveslots = jsonObject.getJSONArray("moveslots");
            List<String>[] moves = new List[ moveslots.length() ];
            for (int i = 0; i < moves.length; i++) {
                moves[i] = new ArrayList<>();
                JSONArray singleSlot = moveslots.getJSONObject(i).getJSONArray("moves");

                for (int j = 0; j < singleSlot.length(); j++) {
                    String move = singleSlot.getJSONObject(j).getString("name");
                    moves[i].add(move);
                }
            }

            JSONArray natureArray = jsonObject.getJSONArray("natures");
            List<Nature> natures = new ArrayList<>();
            for (int i = 0; i < natureArray.length(); i++) {
                Nature nature = Nature.parseNature( natureArray.getJSONObject(i) );
                if (nature != null) {
                    natures.add(nature);
                }
            }
            return new Moveset(name, description, items, abilities, evConfigs, moves, natures);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
