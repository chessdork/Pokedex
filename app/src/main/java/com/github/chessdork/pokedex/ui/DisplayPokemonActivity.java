package com.github.chessdork.pokedex.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.chessdork.pokedex.R;


public class DisplayPokemonActivity extends ActionBarActivity {
    public static final String POKEMON_NAME = "POKEMON_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pokemon);
        String name = getIntent().getStringExtra(POKEMON_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new PokemonPagerAdapter(getSupportFragmentManager(), name, "xy"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_pokemon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class PokemonPagerAdapter extends FragmentStatePagerAdapter {
        private String name, gen;

        public PokemonPagerAdapter(FragmentManager fm, String name, String gen) {
            super(fm);
            this.name = name;
            this.gen = gen;
        }

        @Override
        public Fragment getItem(int position) {
            return DisplayNameplateFragment.newInstance(name, gen);
        }

        @Override
        public String getPageTitle(int position) {
            return "Basic";
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
