package com.github.chessdork.pokedex;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.pokedex.ui.DisplayAbilitiesFragment;
import com.github.chessdork.pokedex.ui.DisplayDexFragment;
import com.github.chessdork.pokedex.ui.DisplayItemsFragment;
import com.github.chessdork.pokedex.ui.DisplayMachinesFragment;
import com.github.chessdork.pokedex.ui.DisplayMovesFragment;
import com.github.chessdork.pokedex.ui.DisplayNaturesFragment;
import com.github.chessdork.pokedex.ui.DisplayTypesFragment;


public class MainActivity extends ActionBarActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private SearchView searchView;
    private Category currentCategory;
    private boolean userLearnedDrawer;

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (!userLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    userLearnedDrawer = true;
                    getPreferences(MODE_PRIVATE).edit()
                            .putBoolean(PREF_USER_LEARNED_DRAWER, true)
                            .apply();
                    Log.d(LOG_TAG, "No longer opening drawers automatically on startup");
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);

        View headerView = new View(this);
        headerView.setBackgroundResource(R.drawable.extra_divider);
        mDrawerList.addHeaderView(headerView);

        mDrawerList.setAdapter(new DrawerAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        userLearnedDrawer = getPreferences(MODE_PRIVATE).getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (!userLearnedDrawer) {
            Log.d(LOG_TAG, "Opening drawers automatically");
            mDrawerLayout.openDrawer(Gravity.START);
        }

        if (savedInstanceState == null) {
            currentCategory = Category.POKEMON;
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new DisplayDexFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Categories for the Navigation Drawer.
     */
    private enum Category {
        POKEMON(R.drawable.ic_drawer_pkball, "Pokémon"),
        TYPES(R.drawable.ic_drawer_types, "Type Chart"),
        MACHINES(0, "TMs/HMs"),
        MOVES(R.drawable.ic_drawer_moves, "Moves"),
        ABILITIES(R.drawable.ic_drawer_abilities, "Abilities"),
        ITEMS(R.drawable.ic_drawer_items, "Items"),
        NATURES(R.drawable.ic_drawer_natures, "Natures");
        //TAGS(R.drawable.ic_drawer_tags, "Tags");

        private final int resId;
        private final String name;

        Category(int resId, String name) {
            this.resId = resId;
            this.name = name;
        }

        public int getResId() {
            return resId;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Adapter implementation for the Navigation Drawer using a TextView with a CompoundDrawable.
     */
    private static class DrawerAdapter extends BaseAdapter {
        private static final Category[] mData = Category.values();

        private Context mCtx;

        public DrawerAdapter(Context context) {
            mCtx = context;
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public Category getItem(int index) {
            return mData[index];
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            TextView textView;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mCtx);
                convertView = inflater.inflate(R.layout.item_drawer, parent, false);
            }

            textView = (TextView) convertView;
            textView.setText(mData[index].getName());
            //textView.setCompoundDrawablesWithIntrinsicBounds(mData[index].getResId(), 0, 0, 0);

            return textView;
        }
    }

    /**
     * OnItemClick implementation for Navigation Drawer.
     */
    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
            Category category = (Category) parent.getItemAtPosition(index);

            // if we're already viewing this fragment, close the drawers and do nothing
            if (currentCategory == category) {
                mDrawerLayout.closeDrawers();
                return;
            } else {
                currentCategory = category;
            }

            switch (category) {
                case ABILITIES:
                    handleClick(new DisplayAbilitiesFragment());
                    break;
                case NATURES:
                    handleClick(new DisplayNaturesFragment());
                    break;
                case POKEMON:
                    handleClick(new DisplayDexFragment());
                    break;
                case TYPES:
                    handleClick(new DisplayTypesFragment());
                    break;
                case MOVES:
                    handleClick(new DisplayMovesFragment());
                    break;
                case ITEMS:
                    handleClick(new DisplayItemsFragment());
                    break;
                case MACHINES:
                    handleClick(new DisplayMachinesFragment());
                    break;
                default:
            }
        }

        private void handleClick(Fragment fragment) {
            FragmentManager fm = getFragmentManager();
            // clear the back stack for top-level navigation
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            searchView.setQuery("", false);
            mDrawerLayout.closeDrawers();
        }
    }
}
