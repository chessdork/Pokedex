package com.github.chessdork.pokedex;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class DisplayNaturesFragment extends Fragment {
    private boolean isListView = true;

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
        return inflater.inflate(R.layout.fragment_display_natures, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.display_natures, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem listItem = menu.findItem(R.id.action_view_as_list);
        MenuItem gridItem = menu.findItem(R.id.action_view_as_grid);

        // toggle between ListView and GridView menu items.
        if (isListView) {
            listItem.setVisible(false);
            gridItem.setVisible(true);
        } else {
            listItem.setVisible(true);
            gridItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.action_view_as_grid:
        case R.id.action_view_as_list:
            isListView = !isListView;
            getActivity().invalidateOptionsMenu();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
