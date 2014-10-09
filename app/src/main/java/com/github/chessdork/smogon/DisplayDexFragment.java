package com.github.chessdork.smogon;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class DisplayDexFragment extends Fragment {

    private PokedexAdapter mAdapter;
    // holds the SearchView query if the user attempted a search before the adapter was prepared.
    private String mQueryBeforeUi;
    private static List<Pokemon> sData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (sData == null) {
            // parse assets asynchronously and set up the UI.
            new ParsePokedexTask(this).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_display_dex, container, false);
        if (sData != null) {
            setupUi(view, sData);
        }
        return view;
    }

    private void setupUi(View rootView, List<Pokemon> pokemon) {
        mAdapter = new PokedexAdapter(getActivity(), pokemon);
        if (mQueryBeforeUi != null) {
            mAdapter.getFilter().filter(mQueryBeforeUi);
        }
        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new DexItemClickListener());

        // Hide the progress bar once the ui is setup.
        rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new DexQueryTextListener());
    }

    private void setData(List<Pokemon> pokemonList) {
        sData = pokemonList;
    }

    private class DexQueryTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (mAdapter != null) {
                mAdapter.getFilter().filter(s);
            } else {
                mQueryBeforeUi = s;
            }
            return false;
        }
    }

    private class DexItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Pokemon pokemon = (Pokemon) adapterView.getItemAtPosition(pos);
            String alias = pokemon.getAlias();
            Intent intent = new Intent(getActivity(), DisplayPokemonActivity.class);
            intent.putExtra("test", alias);
            startActivity(intent);
        }
    }

    private static class PokedexAdapter extends BaseAdapter implements Filterable {
        private List<Pokemon> mData, mOriginalData;
        private LayoutInflater mInflater;

        public PokedexAdapter(Context context, List<Pokemon> pokemon) {
            mInflater = LayoutInflater.from(context);
            mData = pokemon;
            mOriginalData = new ArrayList<>(mData);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Pokemon getItem(int index) {
            return mData.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence s) {
                    String lowercase = s.toString().toLowerCase();

                    List<Pokemon> filteredList = new ArrayList<>();
                    for (Pokemon pokemon : mOriginalData) {
                        if (pokemon.getName().toLowerCase().contains(lowercase)) {
                            filteredList.add(pokemon);
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredList;
                    results.count = filteredList.size();
                    return results;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence s, FilterResults results) {
                    mData = (List<Pokemon>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        static class ViewHolder {
            TextView name;
            TextView type1, type2;
        }

        // setBackgroundDrawable renamed to setBackground in API 16.  It is deprecated, but
        // setBackground simply calls setBackgroundDrawable, so we're okay to use it here and
        // suppress warnings.
        @SuppressWarnings("deprecation")
        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = mInflater.inflate(R.layout.item_dex, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.pokemon_name);
                holder.type1 = (TextView) view.findViewById(R.id.pokemon_type1);
                holder.type2 = (TextView) view.findViewById(R.id.pokemon_type2);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Pokemon pokemon = mData.get(index);
            holder.name.setText(pokemon.getName());

            Type[] types = pokemon.getTypes();

            holder.type1.setText(types[0].getName());
            GradientDrawable gradient1 = createGradient(types[0]);
            holder.type1.setBackgroundDrawable(gradient1);

            if (types.length > 1) {
                holder.type2.setText(types[1].getName());
                GradientDrawable gradient2 = createGradient(types[1]);
                holder.type2.setBackgroundDrawable(gradient2);

                // for pokemon with two types, don't round the corners of the TextView intersection.
                gradient1.setCornerRadii(LEFT_CORNERS);
                gradient2.setCornerRadii(RIGHT_CORNERS);
                holder.type2.setVisibility(View.VISIBLE);
            } else {
                holder.type2.setVisibility(View.INVISIBLE);
            }

            return view;
        }

        private static final float r = 4;
        private static final float[] LEFT_CORNERS = {r, r, 0, 0, 0, 0, r, r};
        private static final float[] RIGHT_CORNERS = {0, 0, r, r, r, r, 0, 0};

        private GradientDrawable createGradient(Type type) {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] {type.getColor1(), type.getColor2()} );
            gradient.setCornerRadius(r);
            gradient.setStroke(1, type.getBorderColor());
            return gradient;
        }
    }

    /**
     * An AsyncTask for parsing the Pokemon from a JSON asset and updating the UI.
     */
    private static class ParsePokedexTask extends AsyncTask<Void, Void, List<Pokemon>> {

        private WeakReference<DisplayDexFragment> mFragReference;

        public ParsePokedexTask(DisplayDexFragment fragment) {
            mFragReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Pokemon> doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            Log.d("Pokedex", "Starting pokedex parsing...");

            List<Pokemon> pokemon = Pokemon.getPokemon(mFragReference.get().getResources());

            long elapsed = System.currentTimeMillis() - start;
            Log.d("Pokedex", "Finished pokedex parsing in " + elapsed + " ms.");
            return pokemon;
        }

        @Override
        protected void onPostExecute(List<Pokemon> pokemonList) {
            super.onPostExecute(pokemonList);
            DisplayDexFragment fragment = mFragReference.get();

            // if the fragment has been destroyed, do nothing.
            if (fragment == null || fragment.getView() == null) return;

            fragment.setData(pokemonList);
            fragment.setupUi(fragment.getView(), pokemonList);
        }
    }
}
