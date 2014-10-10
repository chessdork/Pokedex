package com.github.chessdork.smogon;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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

//TODO different layout for landscape
//TODO allow sort by Pokemon number or Alphabetically
//TODO select whether to display types, tags, abilities, etc.

public class DisplayDexFragment extends Fragment {
    // used to restore the scroll position of the ListView.
    private static final String LISTVIEW_INDEX = "LISTVIEW_INDEX";
    private static final String LISTVIEW_TOP = "LISTVIEW_TOP";

    private PokedexAdapter mAdapter;
    private ListView mListView;
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
            // Populate views, e.g., on orientation change.
            setupUi(view, sData);
        }
        return view;
    }

    private void setupUi(View rootView, List<Pokemon> pokemon) {
        mAdapter = new PokedexAdapter(getActivity(), pokemon);
        // If the user typed into the SearchView before this point, we need to filter.
        if (mQueryBeforeUi != null) {
            mAdapter.getFilter().filter(mQueryBeforeUi);
        }
        mListView = (ListView) rootView.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView( rootView.findViewById(R.id.empty_text) );
        mListView.setOnItemClickListener( new DexItemClickListener() );

        // Hide the progress bar once the ui is setup.
        rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(new DexMenuCollapseListener());

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new DexQueryTextListener());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (mListView != null) {
            int index = mListView.getFirstVisiblePosition();
            View view = mListView.getChildAt(0);
            int top = (view == null) ? 0 : view.getTop();

            savedInstanceState.putInt(LISTVIEW_INDEX, index);
            savedInstanceState.putInt(LISTVIEW_TOP, top);
        }
    }

    /**
     * Restore the scroll position of the ListView.
     * @param savedInstanceState the fragment state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && mListView != null) {
            int index = savedInstanceState.getInt(LISTVIEW_INDEX);
            int top = savedInstanceState.getInt(LISTVIEW_TOP);
            mListView.setSelectionFromTop(index, top);
        }
    }

    private void setData(List<Pokemon> pokemonList) {
        sData = pokemonList;
    }

    /**
     * Resets the dex when the SearchView is collapsed and closes the navigation drawer
     * when the SearchView is expanded.
     */
    private class DexMenuCollapseListener implements MenuItem.OnActionExpandListener {
        @Override
        public boolean onMenuItemActionExpand(MenuItem menuItem) {
            // close the navigation drawer on search.
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawers();
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            // reset the ListView to an unfiltered state when the SearchView is collapsed
            mAdapter.reset();
            return true;
        }
    }

    /**
     * Filters the Dex on text change.
     */
    private class DexQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (mAdapter != null) {
                mAdapter.getFilter().filter(s);
                mQueryBeforeUi = null;
            } else {
                mQueryBeforeUi = s;
            }
            return false;
        }
    }

    /**
     * Starts DisplayPokemonActivity on click.
     */
    private class DexItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Pokemon pokemon = (Pokemon) adapterView.getItemAtPosition(pos);
            Intent intent = new Intent(getActivity(), DisplayPokemonActivity.class);
            intent.putExtra(DisplayPokemonActivity.POKEMON_OBJECT, pokemon);
            startActivity(intent);
        }
    }

    /**
     * BaseAdapter implementation for the Dex.
     */
    private static class PokedexAdapter extends BaseAdapter implements Filterable {
        private List<Pokemon> mData, mOriginalData;
        private LayoutInflater mInflater;

        public PokedexAdapter(Context context, List<Pokemon> pokemon) {
            mInflater = LayoutInflater.from(context);
            mData = pokemon;
            mOriginalData = new ArrayList<>(mData);
        }

        public void reset() {
            mData = mOriginalData;
            notifyDataSetChanged();
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
            Type.setupTypeView(holder.type1, holder.type2, types);

            return view;
        }
    }

    /**
     * An AsyncTask for parsing the Pokemon from a JSON asset and updating the UI.  Note that
     * we do not want to cancel the AsyncTask onStop, as the parsed data may be useful at
     * some point in the future.
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

            // if the fragment has been destroyed or is unattached, do nothing.
            if (fragment == null || fragment.getView() == null || !fragment.isAdded()) return;

            fragment.setData(pokemonList);
            fragment.setupUi(fragment.getView(), pokemonList);
        }
    }
}
