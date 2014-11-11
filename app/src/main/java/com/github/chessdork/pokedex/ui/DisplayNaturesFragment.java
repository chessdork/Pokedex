package com.github.chessdork.pokedex.ui;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.FilterableAdapter;
import com.github.chessdork.pokedex.common.SearchableFragment;
import com.github.chessdork.pokedex.models.Nature;
import com.github.chessdork.pokedex.models.StatType;

import java.util.Arrays;
import java.util.List;


public class DisplayNaturesFragment extends SearchableFragment {
    private boolean isListView = true;
    private MenuItem listItem, gridItem, searchItem;

    private ListView listView;
    private FrameLayout scrollView;

    private static final String LOG_TAG = DisplayNaturesFragment.class.getSimpleName();

    public DisplayNaturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_natures, container, false);

        NatureAdapter adapter = new NatureAdapter(getActivity(), Arrays.asList(Nature.values()));
        setFilterableAdapter(adapter);

        listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));

        scrollView = (FrameLayout) view.findViewById(R.id.scrollview);

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.gridview);
        setupTable(inflater, tableLayout);

        // default is ListView
        if (!isListView) {
            scrollView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int numCalls = 0;

            @Override
            public void onGlobalLayout() {
                numCalls++;
                Log.d(LOG_TAG, "onLayout called " + numCalls + " times.");

                final View view = getView();

                if (view == null) return;

                if (view.getWidth() > 0) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    // for later, in case we want to size the table based on available screen dimens
                }
            }
        });
    }

    private void setupTable(LayoutInflater inflater, TableLayout table) {
        Context context = inflater.getContext();
        StatType[] statTypes = {StatType.ATTACK,
                            StatType.DEFENSE,
                            StatType.SPEED,
                            StatType.SP_ATK,
                            StatType.SP_DEF};
        Nature[] natures = Nature.values();
        final int TABLE_DIMEN = statTypes.length;

        // header row
        TableRow tableRow = new TableRow(context);
        TextView textView;

        // top left cell is empty
        tableRow.addView(new VerticalTextView(context));

        // set up header row
        for (StatType stat : statTypes) {
            textView = (TextView) inflater.inflate(R.layout.template_header_view, tableRow, false);
            textView.setText("-" + stat.getShorthand());
            tableRow.addView(textView);
        }
        table.addView(tableRow);

        // set up each remaining row one at a time, starting with the vertical stat
        for (int row = 0; row < TABLE_DIMEN; row++) {
            tableRow = new TableRow(context);
            textView = (VerticalTextView) inflater.inflate(R.layout.template_vertical_header_view, tableRow, false);
            textView.setText("+" + statTypes[row].getShorthand());
            tableRow.addView(textView);

            for (int col = 0; col < TABLE_DIMEN; col++) {
                textView = (SquareTextView) inflater.inflate(R.layout.template_cell_view, tableRow, false);
                // Convert our (row, col) to an index in the Natures array
                int index = TABLE_DIMEN * row + col;
                textView.setText(natures[index].getName());

                // if it's a neutral nature, set the background appropriately
                if (row == col) {
                    textView.setBackgroundResource(R.drawable.purple_bubble);
                }
                tableRow.addView(textView);
            }
            table.addView(tableRow);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.display_natures, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        listItem = menu.findItem(R.id.action_view_as_list);
        gridItem = menu.findItem(R.id.action_view_as_grid);
        searchItem = menu.findItem(R.id.action_search);

        // ListView is the default
        if (isListView) {
            listItem.setVisible(false);
            gridItem.setVisible(true);
            searchItem.setVisible(true);
        } else {
            listItem.setVisible(true);
            gridItem.setVisible(false);
            searchItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.action_view_as_grid:
            isListView = false;
            listView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            listItem.setVisible(true);
            gridItem.setVisible(false);
            searchItem.setVisible(false);

            return true;
        case R.id.action_view_as_list:
            isListView = true;
            listView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            listItem.setVisible(false);
            gridItem.setVisible(true);
            searchItem.setVisible(true);

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private static final class NatureAdapter extends FilterableAdapter<Nature> {
        private final int DARK_GREEN;
        private final int DARK_RED;

        public NatureAdapter(Context context, List<Nature> natures) {
            super(context, natures);
            DARK_GREEN = context.getResources().getColor(R.color.dark_green);
            DARK_RED = context.getResources().getColor(R.color.dark_red);
        }

        private static final class ViewHolder {
            TextView name;
            TextView incr, decr;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_nature, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nature_name);
                holder.incr = (TextView) view.findViewById(R.id.increased_stat);
                holder.decr = (TextView) view.findViewById(R.id.decreased_stat);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Nature nature = getItem(index);
            holder.name.setText(nature.getName());

            holder.incr.setText(format(nature.getIncreased(), "+"));
            holder.incr.setTextColor( nature.getIncreased() == StatType.NONE ? Color.BLACK : DARK_GREEN);

            holder.decr.setText(format(nature.getDecreased(), "-"));
            holder.decr.setTextColor( nature.getIncreased() == StatType.NONE ? Color.BLACK : DARK_RED);

            return view;
        }

        private static String format(StatType type, String prefix) {
            return ( type == StatType.NONE ? type.getShorthand() : prefix + type.getShorthand() );
        }
    }

}
