package com.github.chessdork.pokedex.ui;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.FilterableAdapter;
import com.github.chessdork.pokedex.common.PokeDatabase;
import com.github.chessdork.pokedex.common.SearchableFragment;
import com.github.chessdork.pokedex.models.Machine;

import java.util.ArrayList;
import java.util.List;

public class DisplayMachinesFragment extends SearchableFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_machines, container, false);

        PokeDatabase db = PokeDatabase.getInstance(getActivity());
        String query = "select machines.name, moves.name, machines.location " +
                "from machines " +
                "join moves on move_id = moves.id";
        Cursor c = db.getReadableDatabase().rawQuery(query, null);

        List<Machine> machines = new ArrayList<>();

        while (c.moveToNext()) {
            machines.add(new Machine(c.getString(0), c.getString(1), c.getString(2)));
        }
        c.close();

        MachineAdapter adapter = new MachineAdapter(getActivity(), machines);
        setFilterableAdapter(adapter);
        setQueryHint("Search TMs/HMs");

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_text));
        listView.setOnItemClickListener(new MachineItemClickListener());

        return view;
    }

    /**
     * Starts DisplayMachineActivity on click.
     */
    private class MachineItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            hideSoftKeyboard();
            Machine machine = (Machine) adapterView.getItemAtPosition(pos);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new DisplayMachineFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private static final class MachineAdapter extends FilterableAdapter<Machine> {
        public MachineAdapter(Context context, List<Machine> machines) {
            super(context, machines);
        }

        private static final class ViewHolder {
            TextView name;
            TextView location;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getInflater().inflate(R.layout.item_machine, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.machine_name);
                holder.location = (TextView) view.findViewById(R.id.machine_location);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Machine machine = getItem(index);
            holder.name.setText(machine.getName());
            holder.location.setText(machine.getLocation());

            return view;
        }
    }

}
