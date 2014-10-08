package com.github.chessdork.smogon;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DisplayDexFragment extends Fragment {

    WeakReference<DownloadPokedexTask> mTaskReference;
    private PokedexAdapter mAdapter;
    private static List<Pokemon> sData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (sData == null) {
            maybeStartDownload();
        }
    }

    /**
     * Starts downloading abilities, if a network connection is available.
     */
    private void maybeStartDownload() {
        if ( Utils.isConnected(getActivity())) {
            DownloadPokedexTask task = new DownloadPokedexTask(this);
            mTaskReference = new WeakReference<>(task);
            task.execute();
        } else {
            Toast.makeText(getActivity(), R.string.no_network_conn, Toast.LENGTH_SHORT).show();
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

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new DexItemClickListener());

        EditText editText = (EditText) rootView.findViewById(R.id.edit_text);
        editText.addTextChangedListener(new DexTextWatcher());

        // Hide the progress bar once the ui is setup.
        rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        rootView.findViewById(R.id.edit_text).setVisibility(View.VISIBLE);
    }

    private void setData(List<Pokemon> pokemonList) {
        sData = pokemonList;
    }

    private class DexTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class DexItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Pokemon pokemon = (Pokemon) adapterView.getItemAtPosition(pos);
            String name = pokemon.getName();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, DisplayPokemonFragment.newInstance(name))
                    .addToBackStack(null)
                    .commit();
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
     * An AsyncTask for downloading the abilities and updating the UI.
     */
    private static class DownloadPokedexTask extends AsyncTask<Void, Void, List<Pokemon>> {
        // for readability, the query string is:
        // {"pokemonalt":{"gen":"xy"},"$":["name","alias","base_alias","gen",
        // {"types":["alias","name","gen"]},{"abilities":["alias","name","gen"]},
        // {"tags":["name","alias","shorthand","gen"]},
        // "weight","height","hp","patk","pdef","spatk","spdef","spe"]}
        private static final String dexUrl = "http://www.smogon.com/dex/api/query?q=" +
                "{\"pokemonalt\":{\"gen\":\"xy\"},\"$\":[\"name\",{\"types\":[\"name\"]}," +
                "{\"abilities\":[\"name\"]},{\"tags\":[\"shorthand\"]}," +
                "\"hp\",\"patk\",\"pdef\",\"spatk\",\"spdef\",\"spe\"]}";

        private WeakReference<DisplayDexFragment> mFragReference;

        public DownloadPokedexTask(DisplayDexFragment fragment) {
            mFragReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Pokemon> doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            Log.d("Pokedex", "Starting pokedex download...");

            try {
                URL url = new URL(dexUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));

                StringBuilder text = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    text.append(line);
                }
                long elapsed = System.currentTimeMillis() - start;
                Log.d("Pokedex", "Finished pokedex download in " + elapsed + " ms.");
                start = System.currentTimeMillis();

                JSONObject jsonObject = new JSONObject(text.toString());
                JSONArray results = jsonObject.getJSONArray("result");
                List<Pokemon> pokemonList = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    Pokemon pokemon = Pokemon.parsePokemon(results.getJSONObject(i));
                    if (pokemon != null) {
                        pokemonList.add(pokemon);
                    }
                }
                elapsed = System.currentTimeMillis() - start;
                Log.d("Pokedex", "Finished pokedex parsing in " + elapsed + " ms.");
                return pokemonList;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
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
