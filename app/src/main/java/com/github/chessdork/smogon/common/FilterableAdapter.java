package com.github.chessdork.smogon.common;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility adapter for filterable AdapterViews.  Subclasses only need to override
 * getView(int, View, ViewGroup).
 * @param <T> The type of data to store.
 */
public abstract class FilterableAdapter<T> extends BaseAdapter implements Filterable{
    private final LayoutInflater inflater;
    private List<T> data;
    private List<T> originalData;

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence s) {
            String lowercase = s.toString().toLowerCase();
            FilterResults results = new FilterResults();

            if (s.length() == 0) {
                results.values = originalData;
                results.count = originalData.size();
            } else {
                List<T> filteredList = new ArrayList<>();
                for (T value : originalData) {
                    if (value.toString().toLowerCase().contains(lowercase)) {
                        filteredList.add(value);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence s, FilterResults results) {
            data = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public FilterableAdapter(Context context, List<T> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        originalData = new ArrayList<>(data);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int index) {
        return data.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }
}
