package com.github.chessdork.smogon;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SmogonActivity extends Activity {

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smogon);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,0,0);

        mDrawerList.setAdapter(new DrawerAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smogon, menu);
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_smogon, container, false);
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
        public Object getItem(int index) {
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
            textView.setCompoundDrawablesWithIntrinsicBounds(mData[index].getResId(),0,0,0);

            return textView;
        }

        private enum Category {
            POKEMON(R.drawable.ic_pkball, "POKEMON"),
            MOVES(R.drawable.ic_moves, "MOVES"),
            ABILITIES(R.drawable.ic_abilities, "ABILITIES"),
            ITEMS(R.drawable.ic_items, "ITEMS"),
            TYPES(R.drawable.ic_types, "TYPES"),
            TAGS(R.drawable.ic_tags, "TAGS");

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
    }

    /**
     * OnItemClick implementation for Navigation Drawer.
     */
    private static class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int index, long id) {

        }
    }
}
