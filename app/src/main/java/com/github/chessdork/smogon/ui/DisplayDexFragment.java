package com.github.chessdork.smogon.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.github.chessdork.smogon.R;
import com.github.chessdork.smogon.models.Pokemon;
import com.github.chessdork.smogon.models.PokemonType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayDexFragment extends Fragment {

    private static final String TAG = DisplayDexFragment.class.getSimpleName();

    private PokedexAdapter adapter;
    private String query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_display_dex, container, false);
        setupUi(view);
        return view;
    }

    private void setupUi(View rootView) {
        adapter = new PokedexAdapter(getActivity(), Arrays.asList(Pokemon.values()));
        // If the user typed into the SearchView before this point, we need to filter.
        if (query != null) {
            adapter.getFilter().filter(query);
        }
        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(R.id.empty_text));
        listView.setOnItemClickListener(new DexItemClickListener());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(new DexMenuCollapseListener());

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new DexQueryTextListener());
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
            query = s;
            Log.d(TAG, "query: " + query);
            if (adapter != null) {
                adapter.getFilter().filter(query);
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

    private static class PokedexAdapter extends BaseAdapter implements Filterable {
        private final LayoutInflater inflater;
        private final Filter filter;
        private List<Pokemon> data;
        private List<Pokemon> originalData;

        public PokedexAdapter(Context context, List<Pokemon> pokemon) {
            inflater = LayoutInflater.from(context);
            data = pokemon;
            originalData = new ArrayList<>(data);

            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence s) {
                    String lowercase = s.toString().toLowerCase();
                    FilterResults results = new FilterResults();

                    if (s.length() == 0) {
                        results.values = originalData;
                        results.count = originalData.size();
                    } else {
                        List<Pokemon> filteredList = new ArrayList<>();
                        for (Pokemon pokemon : originalData) {
                            if (pokemon.getName().toLowerCase().contains(lowercase)) {
                                filteredList.add(pokemon);
                            }
                        }
                        results.values = filteredList;
                        results.count = filteredList.size();
                    }
                    return results;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence s, FilterResults results) {
                    data = (List<Pokemon>) results.values;
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Pokemon getItem(int index) {
            return data.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
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
                view = inflater.inflate(R.layout.item_dex, parent, false);
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

        @Override
        public Filter getFilter() {
            return filter;
        }
    }
}
