package com.github.chessdork.smogon.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.smogon.R;
import com.github.chessdork.smogon.common.FilterableAdapter;
import com.github.chessdork.smogon.common.SearchableFragment;
import com.github.chessdork.smogon.models.Pokemon;
import com.github.chessdork.smogon.models.PokemonType;

import java.util.Arrays;
import java.util.List;

public class DisplayDexFragment extends SearchableFragment {

    private static final String TAG = DisplayDexFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_display_dex, container, false);

        PokedexAdapter adapter = new PokedexAdapter(getActivity(), Arrays.asList(Pokemon.values()));
        setFilterableAdapter(adapter);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));
        listView.setOnItemClickListener(new DexItemClickListener());
        return view;
    }

    /**
     * Starts DisplayPokemonActivity on click.
     */
    private class DexItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            hideSoftKeyboard();
            Pokemon pokemon = (Pokemon) adapterView.getItemAtPosition(pos);
            Intent intent = new Intent(getActivity(), DisplayPokemonActivity.class);
            intent.putExtra(DisplayPokemonActivity.POKEMON_OBJECT, pokemon);
            startActivity(intent);
        }
    }

    private static class PokedexAdapter extends FilterableAdapter<Pokemon> {
        public PokedexAdapter(Context context, List<Pokemon> pokemon) {
            super(context, pokemon);
        }

        static class ViewHolder {
            TextView name;
            TextView type1;
            TextView type2;
        }

        @Override
        @SuppressWarnings("deprecation")
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_dex, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.pokemon_name);
                holder.type1 = (TextView) view.findViewById(R.id.pokemon_type1);
                holder.type2 = (TextView) view.findViewById(R.id.pokemon_type2);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Pokemon pokemon = getItem(index);
            holder.name.setText(pokemon.getName());
            List<PokemonType> types = pokemon.getTypes();
            if (types.size() == 1) {
                holder.type1.setVisibility(View.VISIBLE);
                holder.type1.setText(types.get(0).getName());
                holder.type1.setBackgroundDrawable(types.get(0).createGradient());

                holder.type2.setVisibility(View.INVISIBLE);
            } else if (types.size() == 2) {
                holder.type1.setVisibility(View.VISIBLE);
                holder.type1.setText(types.get(0).getName());
                holder.type1.setBackgroundDrawable(types.get(0).createLeftGradient());

                holder.type2.setVisibility(View.VISIBLE);
                holder.type2.setText(types.get(1).getName());
                holder.type2.setBackgroundDrawable(types.get(1).createRightGradient());
            } else {
                Log.w(TAG, "Pokemon with wrong number of types: " + types.size());
            }

            return view;
        }
    }
}
