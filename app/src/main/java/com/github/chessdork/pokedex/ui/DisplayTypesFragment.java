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
import com.github.chessdork.pokedex.models.PokemonType;

import java.util.Arrays;
import java.util.List;


public class DisplayTypesFragment extends SearchableFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_types, container, false);

        TypesAdapter adapter = new TypesAdapter(getActivity(), Arrays.asList(PokemonType.values()));
        setFilterableAdapter(adapter);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));

        return view;
    }

    private static class TypesAdapter extends FilterableAdapter<PokemonType> {
        public TypesAdapter(Context context, List<PokemonType> types) {
            super(context, types);
        }

        private static class ViewHolder {
            TextView name;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_type, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.type_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            PokemonType type = getItem(index);
            holder.name.setText(type.getName());
            holder.name.setBackgroundDrawable(type.createGradient());

            setupMatchups((ViewGroup) view.findViewById(R.id.double_dmg_to_types), type.getStrongTo());
            setupMatchups((ViewGroup) view.findViewById(R.id.half_dmg_from_types), type.getResistsFrom());
            setupMatchups((ViewGroup) view.findViewById(R.id.double_dmg_from_types), type.getWeakFrom());
            setupMatchups((ViewGroup) view.findViewById(R.id.half_dmg_to_types), type.getWeakTo());

            return view;
        }

        private void setupMatchups(ViewGroup container, List<PokemonType> types) {
            container.removeAllViews();

            for (PokemonType type : types) {
                TypeView typeView = (TypeView) getInflater().inflate(R.layout.template_type_view, container, false);
                typeView.setType(type);
                typeView.getLayoutParams().width = 90;
                container.addView(typeView);
            }
        }
    }
}
