package com.github.chessdork.pokedex.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.PokeDatabase;
import com.github.chessdork.pokedex.models.Move;
import com.github.chessdork.pokedex.models.MoveCategory;
import com.github.chessdork.pokedex.models.PokemonType;

import java.util.ArrayList;
import java.util.List;


public class DisplayNameplateFragment extends Fragment {
    private static final String LOG_TAG = "DisplayNameplateFragment";

    private static final String POKEMON_NAME = "POKEMON_NAME";
    private static final String POKEMON_GEN = "POKEMON_GEN";

    private static final int STAT_MAX_SCALE = 150;
    private static final long ANIM_DURATION = 500;

    private String name, gen;
    private int resId, hp, atk, def, spatk, spdef, speed;

    private List<PokemonType> types;
    private List<Move> levelUpMoves;
    private List<Integer> levels;

    ImageView imageView;

    public static DisplayNameplateFragment newInstance(String param1, String param2) {
        DisplayNameplateFragment fragment = new DisplayNameplateFragment();
        Bundle args = new Bundle();
        args.putString(POKEMON_NAME, param1);
        args.putString(POKEMON_GEN, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayNameplateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(POKEMON_NAME);
            gen = getArguments().getString(POKEMON_GEN);
        }
        doInit();
        new LoadMovesTask().execute();
    }

    private void doInit() {
        SQLiteDatabase db = PokeDatabase.getInstance(getActivity()).getReadableDatabase();
        String query = "select types.name from pokemon_types " +
                       "join types on type_id = types.id " +
                       "join pokemon on pokemon_id = pokemon.id " +
                       "where pokemon.name=?";
        String[] selectionArgs = new String[] {name};
        Cursor c = db.rawQuery(query, selectionArgs);
        types = new ArrayList<>(2);
        while (c.moveToNext()) {
            PokemonType type = PokemonType.valueOf(c.getString(0).toUpperCase());
            types.add(type);
        }
        c.close();

        query = "select pokemon.image_resource_name, pokemon.hp, pokemon.atk, pokemon.def, " +
                "pokemon.spatk, pokemon.spdef, pokemon.speed " +
                "from pokemon " +
                "where pokemon.name=?";
        c = db.rawQuery(query, selectionArgs);
        c.moveToFirst();
        resId = getResources().getIdentifier(c.getString(0), "drawable", getActivity().getPackageName());
        hp = c.getInt(1);
        atk = c.getInt(2);
        def = c.getInt(3);
        spatk = c.getInt(4);
        spdef = c.getInt(5);
        speed = c.getInt(6);
        c.close();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_nameplate, container, false);

        TextView textView = (TextView) view.findViewById(R.id.pokemon_name);
        textView.setText(name);

        TextView type1 = (TextView) view.findViewById(R.id.pokemon_type1);
        TextView type2 = (TextView) view.findViewById(R.id.pokemon_type2);
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

        imageView = (ImageView) view.findViewById(R.id.image_view);
        imageView.setImageResource(resId);

        TextView hpTextView = (TextView) view.findViewById(R.id.hp_stat);
        TextView atkTextView = (TextView) view.findViewById(R.id.patk_stat);
        TextView defTextView = (TextView) view.findViewById(R.id.pdef_stat);
        TextView spatkTextView = (TextView) view.findViewById(R.id.spatk_stat);
        TextView spdefTextView = (TextView) view.findViewById(R.id.spdef_stat);
        TextView speedTextView = (TextView) view.findViewById(R.id.spe_stat);

        hpTextView.setText(String.valueOf(hp));
        atkTextView.setText(String.valueOf(atk));
        defTextView.setText(String.valueOf(def));
        spatkTextView.setText(String.valueOf(spatk));
        spdefTextView.setText(String.valueOf(spdef));
        speedTextView.setText(String.valueOf(speed));

        final View hpStatBar = view.findViewById(R.id.hp_rectangle);
        final View atkStatBar = view.findViewById(R.id.patk_rectangle);
        final View defStatBar = view.findViewById(R.id.pdef_rectangle);
        final View spatkStatBar = view.findViewById(R.id.spatk_rectangle);
        final View spdefStatBar = view.findViewById(R.id.spdef_rectangle);
        final View speedStatBar = view.findViewById(R.id.spe_rectangle);

        // After the first layout pass is finished, dynamically size the stat bars based on
        // available screen width and begin animation.
        view.post(new Runnable() {
            @Override
            public void run() {
                setupStatBar(hpStatBar, hp);
                setupStatBar(atkStatBar, atk);
                setupStatBar(defStatBar, def);
                setupStatBar(spatkStatBar, spatk);
                setupStatBar(spdefStatBar, spdef);
                setupStatBar(speedStatBar, speed);

                ValueAnimator statAnim = ValueAnimator.ofFloat(0f, 1f);
                statAnim.setDuration(ANIM_DURATION);
                statAnim.addUpdateListener(new BarAnimatorUpdateListener(hpStatBar, atkStatBar,
                        defStatBar, spatkStatBar, spdefStatBar, speedStatBar));
                statAnim.start();
            }
        });
        return view;
    }

    private void setupStatBar(View statBar, int statValue) {
        statBar.setVisibility(View.VISIBLE);

        LayerDrawable layer = (LayerDrawable) statBar.getBackground();
        int color = createColorFromStat(statValue);
        layer.findDrawableByLayerId(R.id.stat_color).setColorFilter(color, PorterDuff.Mode.SRC_OVER);

        int start = statBar.getLeft();
        int end = statBar.getRight();
        float scale = (end - start) / (float) STAT_MAX_SCALE;

        ViewGroup.LayoutParams params = statBar.getLayoutParams();
        params.width = (int) (Math.min(statValue, STAT_MAX_SCALE) * scale);
        statBar.setLayoutParams(params);
    }

    /**
     * Magic function to get stat colors.
     *
     * @param stat the stat
     * @return the color as an integer
     */
    private static int createColorFromStat(int stat) {
        int n = (int) Math.floor(2.55 * Math.min(Math.max(stat - 50, 0), 100));
        int r = Math.min(2 * (255 - n), 255);
        int g = Math.min(2 * n, 255);
        int b = (int) Math.floor(4.25 * Math.min(Math.max(stat - 140, 0), 60));
        return Color.rgb(r, g, b);
    }

    /**
     * Animate the stat bar width from 0 to 100%.
     */
    private class BarAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final View[] views;
        private final int[] finalWidths;

        public BarAnimatorUpdateListener(View... views) {
            this.views = views;
            finalWidths = new int[views.length];

            for (int i = 0; i < finalWidths.length; i++) {
                finalWidths[i] = views[i].getLayoutParams().width;
            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float f = (Float) valueAnimator.getAnimatedValue();

            for (int i = 0; i < views.length; i++) {
                ViewGroup.LayoutParams params = views[i].getLayoutParams();
                params.width = (int) (finalWidths[i] * f);
                views[i].setLayoutParams(params);
            }
        }
    }

    /**
     * Load moves asynchronously.
     */
    private class LoadMovesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase db = PokeDatabase.getInstance(DisplayNameplateFragment.this.getActivity()).getReadableDatabase();
            String query = "select moves.name, types.name, moves.accuracy, moves.power, moves.pp, " +
                    "move_categories.name, moves.description, pokemon_level_moves.level " +
                    "from pokemon_level_moves " +
                    "join move_categories on move_category_id = move_categories.id " +
                    "join types on type_id = types.id " +
                    "join moves on move_id = moves.id " +
                    "join pokemon on pokemon_id = pokemon.id " +
                    "where pokemon.name=?";
            Cursor c = db.rawQuery(query, new String[] {name});
            levelUpMoves = new ArrayList<>();
            levels = new ArrayList<>();
            while (c.moveToNext()) {
                levelUpMoves.add(new Move(c));
                levels.add(c.getInt(7));
            }
            c.close();
            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void oneVoid) {
            Context context = DisplayNameplateFragment.this.getActivity();
            View view = getView();

            if (view == null || context == null) {
                Log.w(LOG_TAG, "Asynctask finished before view created");
                return;
            }
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout levelUpLayout = (LinearLayout) view.findViewById(R.id.level_up_moves);

            for (int i = 0; i < levelUpMoves.size(); i++) {
                View moveView = inflater.inflate(R.layout.item_machine, levelUpLayout, false);
                int level = levels.get(i);
                Move move = levelUpMoves.get(i);

                TextView levelView = (TextView) moveView.findViewById(R.id.machine_name);
                levelView.setText(String.valueOf(level));

                TextView moveName = (TextView) moveView.findViewById(R.id.move_name);
                moveName.setText(move.getName());

                PokemonType moveType = move.getType();
                TextView typeView = (TextView) moveView.findViewById(R.id.move_type);
                typeView.setText(moveType.getName());
                typeView.setBackgroundDrawable(moveType.createGradient());

                MoveCategory moveCategory = move.getCategory();
                ImageView categoryView = (ImageView) moveView.findViewById(R.id.move_category);
                categoryView.setBackgroundDrawable(moveCategory.createGradient());
                categoryView.setImageResource(moveCategory.getResId());
                levelUpLayout.addView(moveView);
            }
        }


    }
}
