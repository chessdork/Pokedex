package com.github.chessdork.smogon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PokedexAdapter extends BaseAdapter implements Filterable {
    private static final Pokemon[] allPokemon = Pokemon.values();

    private LayoutInflater mInflater;
    private Filter mFilter;
    private List<Pokemon> mPokemon;

    public PokedexAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mFilter = new NameFilter();
        mPokemon = Arrays.asList(allPokemon);
    }

    @Override
    public int getCount() {
        return mPokemon.size();
    }

    @Override
    public Pokemon getItem(int position) {
        return mPokemon.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * ViewHolder for item_pokedex layout
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
            view = mInflater.inflate(R.layout.item_pokedex, parent, false);
            holder = new ViewHolder();
            holder.pokemonImage = (ImageView) view.findViewById(R.id.pokemon_image);
            holder.pokemonName = (TextView) view.findViewById(R.id.pokemon_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Pokemon pokemon = mPokemon.get(position);
        //int resId = pokemon.getResId();
        String name = pokemon.getName();

        //holder.pokemonImage.setImageResource(resId);
        holder.pokemonName.setText(name);

        return view;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pokemon> filteredPokemon = new ArrayList<>();

            for (Pokemon pokemon : allPokemon) {
                if (pokemon.getName().contains(constraint)) {
                    filteredPokemon.add(pokemon);
                }
            }

            FilterResults results = new FilterResults();
            results.count = filteredPokemon.size();
            results.values = filteredPokemon;

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPokemon = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }
    }
}