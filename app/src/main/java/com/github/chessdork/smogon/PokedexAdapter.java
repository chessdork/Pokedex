package com.github.chessdork.smogon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PokedexAdapter extends BaseAdapter {
    private static final Pokemon[] pokemon = Pokemon.values();

    private LayoutInflater mInflater;

    public PokedexAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return pokemon.length;
    }

    @Override
    public Pokemon getItem(int position) {
        return pokemon[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ViewHolder for pokedex_list_item layout
     */
    static class ViewHolder {
        ImageView pokemonImage;
        TextView pokemonName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.pokedex_list_item, parent, false);

            holder = new ViewHolder();
            holder.pokemonImage = (ImageView) view.findViewById(R.id.pokemon_image);
            holder.pokemonName = (TextView) view.findViewById(R.id.pokemon_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        int resId = pokemon[position].getResId();
        String name = pokemon[position].getName();

        holder.pokemonImage.setImageResource(resId);
        holder.pokemonName.setText(name);

        return view;
    }
}