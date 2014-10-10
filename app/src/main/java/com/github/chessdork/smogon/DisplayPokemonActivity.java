package com.github.chessdork.smogon;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DisplayPokemonActivity extends Activity {
    public static final String POKEMON_OBJECT = "POKEMON_OBJECT";

    private Pokemon mPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pokemon);

        final ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mPokemon = (Pokemon) getIntent().getSerializableExtra(POKEMON_OBJECT);
        setupUi();
    }

    private void setupUi() {
        TextView textView = (TextView) findViewById(R.id.pokemon_name);
        textView.setText(mPokemon.getName());

        TextView type1 = (TextView) findViewById(R.id.pokemon_type1);
        TextView type2 = (TextView) findViewById(R.id.pokemon_type2);
        Type[] types = mPokemon.getTypes();
        Type.setupTypeView(type1, type2, types);

        TextView tags = (TextView) findViewById(R.id.pokemon_tags);
        tags.setText(mPokemon.getTag());

        setupStatBar(R.id.hp_rectangle, R.id.hp_stat, mPokemon.getHp());
        setupStatBar(R.id.patk_rectangle, R.id.patk_stat, mPokemon.getPatk());
        setupStatBar(R.id.pdef_rectangle, R.id.pdef_stat, mPokemon.getPdef());
        setupStatBar(R.id.spatk_rectangle, R.id.spatk_stat, mPokemon.getSpatk());
        setupStatBar(R.id.spdef_rectangle, R.id.spdef_stat, mPokemon.getSpdef());
        setupStatBar(R.id.spe_rectangle, R.id.spe_stat, mPokemon.getSpe());
    }

    private void setupStatBar(int rectId, int textId, int statValue) {
        TextView stat = (TextView) findViewById(textId);
        stat.setText(String.valueOf(statValue));

        View rectangle = findViewById(rectId);
        LayerDrawable layer = (LayerDrawable) rectangle.getBackground();
        int color = createColorFromStat( statValue );
        layer.getDrawable(1).setColorFilter(color, PorterDuff.Mode.SRC_OVER);

        ViewGroup.LayoutParams params = rectangle.getLayoutParams();
        params.width = statValue * 2;
        rectangle.setLayoutParams(params);
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
        switch(id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int createColorFromStat(int stat) {
        int n = (int) Math.floor(2.55 * Math.min( Math.max(stat-50, 0),100));
        int r = Math.min(2*(255-n),255);
        int g = Math.min(2*n,255);
        int b = (int) Math.floor(4.25*Math.min(Math.max(stat-140,0),60));
        return Color.rgb(r, g, b);
    }
}
