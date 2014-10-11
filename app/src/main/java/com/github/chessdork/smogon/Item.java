package com.github.chessdork.smogon;


import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private final String name, alias, description;

    public Item(String name, String alias, String description) {
        this.name = name;
        this.alias = alias;
        this.description = description;
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

    public static Item parseItem(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            String alias = jsonObject.getString("alias");
            String description = jsonObject.getString("description");
            return new Item(name, alias, description);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
