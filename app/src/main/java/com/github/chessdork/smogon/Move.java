package com.github.chessdork.smogon;


import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Move {
    private static List<Move> masterList;

    private final int accuracy, power, pp;
    private final Type type;
    private final String name, alias, description;
    private final Category category;


    public Move(String name, String alias, String description, int accuracy, int power, int pp,
                Type type, Category category) {
        this.name = name;
        this.alias = alias;
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

    public String getAlias() {
        return alias;
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

    public Type getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    /**
     * Returns a list of all the moves in gen xy.
     * @param resources app resources
     * @return a master list of moves
     */
    public static List<Move> getMoves(Resources resources) {
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
                JSONArray moveArray = response.getJSONArray("result");

                for (int i = 0; i < moveArray.length(); i++) {
                    Move move = parseMove(moveArray.getJSONObject(i));
                    if (move != null) {
                        masterList.add(move);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return masterList;
    }

    public static Move parseMove(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            String alias = jsonObject.getString("alias");
            String desc = jsonObject.getString("description");

            int accuracy = jsonObject.getInt("accuracy");
            int power = jsonObject.getInt("power");
            int pp = jsonObject.getInt("pp");

            Type type = Type.valueOf(
                    jsonObject.getJSONObject("type").getString("name").toUpperCase() );

            Category category = Category.valueOf(
                    jsonObject.getString("category").replaceAll("-","_").toUpperCase() );

            return new Move(name, alias, desc, accuracy, power, pp, type, category);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}