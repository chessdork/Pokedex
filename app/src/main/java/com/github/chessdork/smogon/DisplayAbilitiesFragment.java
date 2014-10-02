package com.github.chessdork.smogon;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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


public class DisplayAbilitiesFragment extends Fragment {
    WeakReference<DownloadAbilitiesTask> mTaskReference;
    private static List<Ability> sData;

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
        if ( Utils.isConnected(getActivity()) ) {
            DownloadAbilitiesTask task = new DownloadAbilitiesTask(this);
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
        View view = inflater.inflate(R.layout.fragment_display_abilities, container, false);
        if (sData != null) {
            setupUi(view, sData);
        }
        return view;
    }

    private void setupUi(View rootView, List<Ability> abilities) {
        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(new AbilitiesAdapter(getActivity(), abilities));

        // Hide the progress bar once the ui is setup.
        rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    private void setData(List<Ability> abilities) {
        sData = abilities;
    }

    private static class AbilitiesAdapter extends BaseAdapter {
        private List<Ability> mData;
        private LayoutInflater mInflater;

        public AbilitiesAdapter(Context context, List<Ability> abilities) {
            mInflater = LayoutInflater.from(context);
            mData = abilities;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Ability getItem(int index) {
            return mData.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        static class ViewHolder {
            TextView name;
            TextView description;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = mInflater.inflate(R.layout.item_ability, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.description = (TextView) view.findViewById(R.id.description);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Ability ability = mData.get(index);
            holder.name.setText(ability.getName());
            holder.description.setText(ability.getDescription());

            return view;
        }
    }

    /**
     * An AsyncTask for downloading the abilities and updating the UI.
     */
    private static class DownloadAbilitiesTask extends AsyncTask<Void, Void, List<Ability>> {
        // for readability, the query string is {"ability":{"gen":"xy"},"$":["name","description"]}
        private static final String abilityUrl = "http://www.smogon.com/dex/api/query?q=" +
                    "{\"ability\":{\"gen\":\"xy\"},\"$\":[\"name\",\"description\"]}";

        private WeakReference<DisplayAbilitiesFragment> mFragReference;

        public DownloadAbilitiesTask(DisplayAbilitiesFragment fragment) {
            mFragReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Ability> doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            Log.d("Abilities", "Starting ability download...");

            try {
                URL url = new URL(abilityUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));

                StringBuilder text = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    text.append(line);
                }

                JSONObject jsonObject = new JSONObject(text.toString());
                JSONArray results = jsonObject.getJSONArray("result");
                List<Ability> abilities = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    Ability ability = Ability.parseAbility( results.getJSONObject(i) );
                    if (ability != null) {
                        abilities.add(ability);
                    }
                }
                long elapsed = System.currentTimeMillis() - start;
                Log.d("Abilities", "Finished ability download in " + elapsed + " ms.");
                return abilities;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Ability> abilities) {
            super.onPostExecute(abilities);
            DisplayAbilitiesFragment fragment = mFragReference.get();

            // if the fragment has been destroyed, do nothing.
            if (fragment == null || fragment.getView() == null) return;

            fragment.setData(abilities);
            fragment.setupUi(fragment.getView(), abilities);
        }
    }
}
