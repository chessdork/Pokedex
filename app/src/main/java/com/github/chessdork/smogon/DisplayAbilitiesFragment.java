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

import java.lang.ref.WeakReference;
import java.util.List;


public class DisplayAbilitiesFragment extends Fragment {
    private static List<Ability> sData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (sData == null) {
            new ParseAbilitiesTask(this).execute();
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
     * An AsyncTask for parsing the abilities and updating the UI.
     */
    private static class ParseAbilitiesTask extends AsyncTask<Void, Void, List<Ability>> {

        private WeakReference<DisplayAbilitiesFragment> mFragReference;

        public ParseAbilitiesTask(DisplayAbilitiesFragment fragment) {
            mFragReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Ability> doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            Log.d("Abilities", "Starting ability download...");

            List<Ability> abilities = Ability.getAbilities(mFragReference.get().getResources());

            long elapsed = System.currentTimeMillis() - start;
            Log.d("Abilities", "Finished ability download in " + elapsed + " ms.");
            return abilities;
        }

        @Override
        protected void onPostExecute(List<Ability> abilities) {
            super.onPostExecute(abilities);
            DisplayAbilitiesFragment fragment = mFragReference.get();

            // if the fragment has been destroyed or is unattached, do nothing.
            if (fragment == null || fragment.getView() == null || !fragment.isAdded()) return;

            fragment.setData(abilities);
            fragment.setupUi(fragment.getView(), abilities);
        }
    }
}
