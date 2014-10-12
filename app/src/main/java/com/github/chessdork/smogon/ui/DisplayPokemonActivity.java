package com.github.chessdork.smogon.ui;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chessdork.smogon.Move;
import com.github.chessdork.smogon.Moveset;
import com.github.chessdork.smogon.R;
import com.github.chessdork.smogon.models.Pokemon;
import com.github.chessdork.smogon.models.PokemonType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DisplayPokemonActivity extends Activity {
    public static final String POKEMON_OBJECT = "POKEMON_OBJECT";

    private static final long ANIM_DURATION = 300;
    private static final int[] ALL_STAT_BARS = {
            R.id.hp_rectangle,
            R.id.patk_rectangle,
            R.id.pdef_rectangle,
            R.id.spatk_rectangle,
            R.id.spdef_rectangle,
            R.id.spe_rectangle
    };

    // if the activity is stopped before the moveset parsing finishes, we should kill the AsyncTask
    // since the result is no longer useful.
    private AsyncTask mTask;
    private List<Moveset> mMoveSets;
    private List<Move> mMoves;
    private Pokemon mPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pokemon);
        mPokemon = (Pokemon) getIntent().getSerializableExtra(POKEMON_OBJECT);

        final ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mTask = new ParseMovesetTask().execute(mPokemon.getBaseAlias());
        setupStaticUi();
    }

    /**
     * Setup views that don't rely on the result of the AsyncTask.
     */
    private void setupStaticUi() {
        TextView textView = (TextView) findViewById(R.id.pokemon_name);
        textView.setText(mPokemon.getName());

        TextView type1 = (TextView) findViewById(R.id.pokemon_type1);
        TextView type2 = (TextView) findViewById(R.id.pokemon_type2);
        List<PokemonType> types = mPokemon.getTypes();
        if (types.size() == 1) {
            type1.setVisibility(View.VISIBLE);
            type1.setText(types.get(0).getName());
            type1.setBackgroundDrawable(types.get(0).createGradient());

            type2.setVisibility(View.INVISIBLE);
        } else if (types.size() == 2) {
            type1.setVisibility(View.VISIBLE);
            type1.setText(types.get(0).getName());
            type1.setBackgroundDrawable(types.get(0).createLeftGradient());

            type2.setVisibility(View.VISIBLE);
            type2.setText(types.get(1).getName());
            type2.setBackgroundDrawable(types.get(1).createRightGradient());
        }

        TextView tags = (TextView) findViewById(R.id.pokemon_tags);
        // TODO(yang): add this back
//        tags.setText(mPokemon.getTag());

        setupStatBar(R.id.hp_rectangle, R.id.hp_stat, mPokemon.getHp());
        setupStatBar(R.id.patk_rectangle, R.id.patk_stat, mPokemon.getAttack());
        setupStatBar(R.id.pdef_rectangle, R.id.pdef_stat, mPokemon.getDefense());
        setupStatBar(R.id.spatk_rectangle, R.id.spatk_stat, mPokemon.getSpecialAttack());
        setupStatBar(R.id.spdef_rectangle, R.id.spdef_stat, mPokemon.getSpecialDefense());
        setupStatBar(R.id.spe_rectangle, R.id.spe_stat, mPokemon.getSpeed());
    }

    /**
     * Populate the ListView after the AsyncTask has completed
     */
    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.moveset_list);
        listView.setAdapter(new MovesetAdapter(this, mMoveSets));
        listView.setEmptyView(findViewById(R.id.empty_text));
        //hide the progress bar now that the ListView is populated
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    private void setData(MovesetWrapper wrapper) {
        mMoves = wrapper.moves;
        mMoveSets = wrapper.movesets;
    }

    /**
     * Recolors the stat bar and sets the correct text for a stat.  After the Android system
     * finishes the layout, resizeStatBarToFit is called.
     *
     * @param rectId    resource id for rectangle shape
     * @param textId    resource id for TextView
     * @param statValue stat
     */
    private void setupStatBar(int rectId, int textId, int statValue) {
        TextView stat = (TextView) findViewById(textId);
        stat.setText(String.valueOf(statValue));

        View rectangle = findViewById(rectId);
        LayerDrawable layer = (LayerDrawable) rectangle.getBackground();
        int color = createColorFromStat(statValue);
        layer.findDrawableByLayerId(R.id.stat_color).setColorFilter(color, PorterDuff.Mode.SRC_OVER);

        ViewGroup.LayoutParams params = rectangle.getLayoutParams();
        params.width = statValue * 2;
        rectangle.setLayoutParams(params);
    }

    /**
     * Resize stat bars to fit the screen once they are created and begin animation.
     *
     * @param hasFocus whether the window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            resizeStatBarToFit(ALL_STAT_BARS);

            ValueAnimator statAnim = ValueAnimator.ofFloat(0f, 1f);
            statAnim.setDuration(ANIM_DURATION);
            statAnim.addUpdateListener(new BarAnimatorUpdateListener(ALL_STAT_BARS));
            statAnim.start();
        }
    }

    /**
     * Resize stat bars to fit the screen.  Otherwise, the rounded end of the rectangle is drawn
     * off-screen.
     *
     * @param rectIds resource ids for rectangle shapes
     */
    private void resizeStatBarToFit(int... rectIds) {
        for (int id : rectIds) {
            View view = findViewById(id);
            Rect r = new Rect();

            if (view.getGlobalVisibleRect(r)) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = r.width();
                view.setLayoutParams(params);
            }
        }
    }

    /**
     * Animate the stat bar width from 0 to 100%.
     */
    private class BarAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final int[] ids;
        private final int[] finalWidths;

        public BarAnimatorUpdateListener(int... rectIds) {
            ids = rectIds;
            finalWidths = new int[ids.length];

            for (int i = 0; i < finalWidths.length; i++) {
                finalWidths[i] = findViewById(ids[i]).getLayoutParams().width;
            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float f = (Float) valueAnimator.getAnimatedValue();

            for (int i = 0; i < ids.length; i++) {
                View view = findViewById(ids[i]);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) (finalWidths[i] * f);
                view.setLayoutParams(params);
            }
        }
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
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Magic function to get stat colors that match Smogon.
     *
     * @param stat the stat
     * @return the color as an integer
     */
    private int createColorFromStat(int stat) {
        int n = (int) Math.floor(2.55 * Math.min(Math.max(stat - 50, 0), 100));
        int r = Math.min(2 * (255 - n), 255);
        int g = Math.min(2 * n, 255);
        int b = (int) Math.floor(4.25 * Math.min(Math.max(stat - 140, 0), 60));
        return Color.rgb(r, g, b);
    }

    private static class MovesetAdapter extends BaseAdapter {
        private List<Moveset> mMovesets;
        private LayoutInflater mInflater;

        public MovesetAdapter(Context ctx, List<Moveset> movesets) {
            mInflater = LayoutInflater.from(ctx);
            mMovesets = movesets;
        }

        @Override
        public int getCount() {
            return mMovesets.size();
        }

        @Override
        public Moveset getItem(int index) {
            return mMovesets.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.item_moveset, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.moveset_name);
            textView.setText(mMovesets.get(index).getName());
            return view;
        }
    }

    private static class MovesetWrapper {
        List<Moveset> movesets;
        List<Move> moves;
    }

    private class ParseMovesetTask extends AsyncTask<String, Void, MovesetWrapper> {

        @Override
        protected MovesetWrapper doInBackground(String... strings) {
            final String alias = strings[0];
            long start = System.currentTimeMillis();
            Log.d("Pokemon", "Starting moveset parsing for " + alias + "...");
            try {
                InputStream is = getAssets().open("movesets/" + alias + ".json");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder text = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }

                JSONObject jsonObject = new JSONObject(text.toString());
                JSONArray results = jsonObject.getJSONArray("result");
                int length = results.length();

                // assume there is exactly one result.  Verified as of October 11 2014.
                // TODO write a test to verify number of results
                if (length != 1) {
                    Log.i("Moveset", "results array size is " + length + " for " + alias);
                }
                JSONArray movesetArray = results.getJSONObject(0).getJSONArray("movesets");
                List<Moveset> movesets = new ArrayList<>();
                for (int i = 0; i < movesetArray.length(); i++) {
                    Moveset moveset = Moveset.parseMoveset(movesetArray.getJSONObject(i));
                    if (moveset != null) {
                        movesets.add(moveset);
                    }
                }

                JSONArray moveArray = results.getJSONObject(0).getJSONArray("moves");
                List<Move> moves = new ArrayList<>();
                for (int i = 0; i < moveArray.length(); i++) {
                    Move move = Move.parseMove(moveArray.getJSONObject(i));
                    if (move != null) {
                        moves.add(move);
                    }
                }

                MovesetWrapper wrapper = new MovesetWrapper();
                wrapper.movesets = movesets;
                wrapper.moves = moves;

                long elapsed = System.currentTimeMillis() - start;
                Log.d("Pokemon", "Finished moveset parsing for " + alias + " in " + elapsed + " ms");
                return wrapper;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovesetWrapper wrapper) {
            setData(wrapper);
            setupListView();
        }
    }
}
