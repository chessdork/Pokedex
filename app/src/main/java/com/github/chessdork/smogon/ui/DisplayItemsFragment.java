package com.github.chessdork.smogon.ui;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.smogon.R;
import com.github.chessdork.smogon.common.FilterableAdapter;
import com.github.chessdork.smogon.common.SearchableFragment;
import com.github.chessdork.smogon.models.Item;

import java.util.Arrays;
import java.util.List;


public class DisplayItemsFragment extends SearchableFragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_items, container, false);

        ItemAdapter adapter = new ItemAdapter(getActivity(), Arrays.asList(Item.values()));
        setFilterableAdapter(adapter);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));

        return view;
    }

    private static final class ItemAdapter extends FilterableAdapter<Item> {
        public ItemAdapter(Context context, List<Item> items) {
            super(context, items);
        }

        private static final class ViewHolder {
            TextView name;
            TextView description;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_item, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.item_name);
                holder.description = (TextView) view.findViewById(R.id.item_description);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Item item = getItem(index);
            holder.name.setText(item.getName());
            holder.description.setText(item.getDescription());

            return view;
        }
    }

}
