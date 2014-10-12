package com.github.chessdork.smogon.ui;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.smogon.R;
import com.github.chessdork.smogon.models.Ability;

import java.util.Arrays;
import java.util.List;


public class DisplayAbilitiesFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_display_abilities, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(new AbilitiesAdapter(getActivity(), Arrays.asList(Ability.values())));
        return view;
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
}
