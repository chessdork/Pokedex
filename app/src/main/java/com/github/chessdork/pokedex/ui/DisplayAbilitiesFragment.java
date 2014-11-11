package com.github.chessdork.pokedex.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.FilterableAdapter;
import com.github.chessdork.pokedex.common.SearchableFragment;
import com.github.chessdork.pokedex.models.Ability;

import java.util.Arrays;
import java.util.List;


public class DisplayAbilitiesFragment extends SearchableFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_display_abilities, container, false);

        AbilitiesAdapter adapter = new AbilitiesAdapter(getActivity(), Arrays.asList(Ability.values()));
        setFilterableAdapter(adapter);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setEmptyView( view.findViewById(R.id.empty_text) );
        listView.setAdapter(adapter);

        return view;
    }

    static class AbilitiesAdapter extends FilterableAdapter<Ability>{
        public AbilitiesAdapter(Context context, List<Ability> abilities) {
            super(context, abilities);
        }

        private static class ViewHolder {
            TextView name;
            TextView description;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_ability, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.description = (TextView) view.findViewById(R.id.description);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Ability ability = getItem(index);
            holder.name.setText(ability.getName());
            holder.description.setText(ability.getDescription());

            return view;
        }
    }
}
