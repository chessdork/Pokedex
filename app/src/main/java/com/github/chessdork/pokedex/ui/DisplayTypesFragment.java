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
        setQueryHint("Search types");

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));

        return view;
    }

    private static class TypesAdapter extends FilterableAdapter<PokemonType> {
        final float scale;

        public TypesAdapter(Context context, List<PokemonType> types) {
            super(context, types);
            scale = context.getResources().getDisplayMetrics().density;
        }

        private static class ViewHolder {
            TextView name, nameBackground;
            ViewGroup doubleDmgTo, halfDmgFrom, doubleDmgFrom, halfDmgTo, immuneFrom, cannotDamage;
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
                holder.nameBackground = (TextView) view.findViewById(R.id.name_background);
                holder.doubleDmgTo = (ViewGroup) view.findViewById(R.id.double_dmg_to_types);
                holder.halfDmgFrom = (ViewGroup) view.findViewById(R.id.half_dmg_from_types);
                holder.doubleDmgFrom = (ViewGroup) view.findViewById(R.id.double_dmg_from_types);
                holder.halfDmgTo = (ViewGroup) view.findViewById(R.id.half_dmg_to_types);
                holder.immuneFrom = (ViewGroup) view.findViewById(R.id.immune_from_types);
                holder.cannotDamage = (ViewGroup) view.findViewById(R.id.cannot_dmg_types);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            PokemonType type = getItem(index);
            holder.name.setText(type.getName());
            holder.nameBackground.setBackgroundDrawable(type.createRotatedGradient());

            setupMatchups(holder.doubleDmgTo, type.getStrongTo(), true);
            setupMatchups(holder.halfDmgFrom, type.getResistsFrom(), true);
            setupMatchups(holder.doubleDmgFrom, type.getWeakFrom(), true);
            setupMatchups(holder.halfDmgTo, type.getWeakTo(), true);

            setupImmunities(holder.immuneFrom, type.getImmuneFrom());
            setupImmunities(holder.cannotDamage, type.getCannotDamage());

            return view;
        }

        private void setupImmunities(ViewGroup container, List<PokemonType> types) {
            if (types.size() > 0) {
                // remove all views except the descriptor text
                container.removeViews(1, container.getChildCount() - 1);
                setupMatchups(container, types, false);
                container.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
            }
        }

        private void setupMatchups(ViewGroup container, List<PokemonType> types, boolean removeViews) {
            if (removeViews) {
                container.removeAllViews();
            }

            for (PokemonType type : types) {
                TypeView typeView = (TypeView) getInflater().inflate(R.layout.template_type_view, container, false);
                typeView.setType(type);
                typeView.getLayoutParams().width = (int) (60 * scale + 0.5f);
                container.addView(typeView);
            }
        }
    }
}
