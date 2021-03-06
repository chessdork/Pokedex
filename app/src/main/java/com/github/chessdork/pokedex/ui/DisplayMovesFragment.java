package com.github.chessdork.pokedex.ui;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.FilterableAdapter;
import com.github.chessdork.pokedex.common.PokeDatabase;
import com.github.chessdork.pokedex.common.SearchableFragment;
import com.github.chessdork.pokedex.models.Move;
import com.github.chessdork.pokedex.models.MoveCategory;

import java.util.ArrayList;
import java.util.List;


public class DisplayMovesFragment extends SearchableFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_moves, container, false);

        SQLiteDatabase db = PokeDatabase.getInstance(getActivity()).getReadableDatabase();
        String query = "select moves.name, types.name, moves.accuracy, moves.power, moves.pp, " +
                       "move_categories.name, moves.description " +
                       "from moves " +
                       "join move_categories on move_category_id = move_categories.id " +
                       "join types on type_id = types.id";
        Cursor c = db.rawQuery(query, null);

        List<Move> moves = new ArrayList<>();
        while (c.moveToNext()) {
            moves.add(new Move(c));
        }
        c.close();

        MovesAdapter adapter = new MovesAdapter(getActivity(), moves);
        setFilterableAdapter(adapter);
        setQueryHint("Search moves");

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));
        return view;
    }

    private static class MovesAdapter extends FilterableAdapter<Move> {

        public MovesAdapter(Context context, List<Move> moves) {
            super(context, moves);
        }

        private static class ViewHolder {
            TextView name;
            TextView type;
            ImageView category;
            TextView description;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_move, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.move_name);
                holder.type = (TextView) view.findViewById(R.id.move_type);
                holder.category = (ImageView) view.findViewById(R.id.move_category);
                holder.description = (TextView) view.findViewById(R.id.move_description);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Move move = getItem(index);
            holder.name.setText(move.getName());

            if (holder.type != null) {
                holder.type.setText(move.getType().getName());
                holder.type.setBackgroundDrawable(move.getType().createGradient());
            }

            if (holder.category != null ) {
                MoveCategory category = move.getCategory();
                holder.category.setBackgroundDrawable(category.createGradient());
                holder.category.setImageResource(category.getResId());
            }

            holder.description.setText(move.getDescription());

            return view;
        }
    }
}
