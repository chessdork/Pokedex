package com.github.chessdork.smogon;



import android.app.Activity;
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
    private boolean mConnAvailable;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mConnAvailable = Utils.isConnected(activity);
        if (!mConnAvailable) {
            Toast.makeText(activity, R.string.no_network_conn, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mConnAvailable) {
            startDownload();
        }
    }

    private void startDownload() {
        DownloadAbilitiesTask task = new DownloadAbilitiesTask(this);
        mTaskReference = new WeakReference<>(task);
        task.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_abilities, container, false);
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

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            TextView textView;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_drawer, parent, false);
            }

            textView = (TextView) convertView;
            textView.setText(mData.get(index).getName() + mData.get(index).getDescription());

            return textView;
        }
    }

    /**
     * An AsyncTask for downloading the abilities and updating the UI.
     */
    private static class DownloadAbilitiesTask extends AsyncTask<Void, Void, List<Ability>> {
        // for readability, the query string is {"ability":{"gen":"xy"},"$":["name","description"]}
        private static final String abilityUrl = "http://www.smogon.com/dex/api/query?q=" +
                    "{\"ability\":{\"gen\":\"xy\"},\"$\":[\"name\",\"description\"]}";

        private WeakReference<Fragment> mFragReference;

        public DownloadAbilitiesTask(Fragment fragment) {
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
            Fragment fragment = mFragReference.get();

            // if the fragment has been destroyed, do nothing.
            if (fragment == null || fragment.getView() == null) return;

            View view = fragment.getView();
            ListView listView = (ListView) view.findViewById(R.id.listview);
            listView.setAdapter( new AbilitiesAdapter(fragment.getActivity(), abilities) );
        }
    }
}
