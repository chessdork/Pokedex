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

public class Ability {
    private static List<Ability> masterList;

    private final String name, description;

    public Ability(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<Ability> getAbilities(Resources resources) {
        if (masterList == null) {
            InputStream is = resources.openRawResource(R.raw.abilities);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            masterList = new ArrayList<>();

            try {
                StringBuilder text = new StringBuilder();
                String line;

                while ( (line = br.readLine()) != null) {
                    text.append(line);
                }

                JSONObject response = new JSONObject(text.toString());
                JSONArray abilities = response.getJSONArray("result");

                for (int i = 0; i < abilities.length(); i++) {
                    Ability ability = parseAbility(abilities.getJSONObject(i));
                    if (ability != null) {
                        masterList.add(ability);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return masterList;
    }

    public static Ability parseAbility(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            String desc = jsonObject.getString("description");
            return new Ability(name, desc);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
