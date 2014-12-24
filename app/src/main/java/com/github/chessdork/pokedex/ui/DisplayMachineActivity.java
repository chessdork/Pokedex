package com.github.chessdork.pokedex.ui;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.Keys;
import com.github.chessdork.pokedex.common.PokeDatabase;
import com.github.chessdork.pokedex.models.MoveCategory;
import com.github.chessdork.pokedex.models.PokemonType;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;


public class DisplayMachineActivity extends ActionBarActivity
        implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener {

    public static final String LOG_TAG = DisplayMachineActivity.class.getSimpleName();

    public static final String MACHINE_NAME = "MACHINE_NAME";
    public static final String GEN_NAME = "GEN_NAME";

    private static final long ANIM_DURATION = 300;
    private static final int[] ALL_STAT_BARS = {
            R.id.power_rectangle,
            R.id.accuracy_rectangle,
            R.id.pp_rectangle,
    };

    private String machineName, machineLocation, moveName, moveDescription, typeName, genName;
    private int gradStartColor, gradEndColor, borderColor, pp, power, accuracy;
    private MoveCategory moveCategory;

    private YouTubePlayer player;
    private Toolbar toolbar;
    private ScrollView scrollView;
    private YouTubePlayerFragment fragment;

    private boolean isFullScreen;

    private String videoId;
    private int startTime, endTime;

    private boolean statBarsSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_machine);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView = (ScrollView) findViewById(R.id.scrollview);

        fragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);

        machineName = getIntent().getStringExtra(MACHINE_NAME);
        genName = getIntent().getStringExtra(GEN_NAME);

        queryDbAndInit();
        doLayout();

        if (videoId != null && !videoId.equals("")) {
            fragment.initialize(Keys.YOUTUBE_KEY, this);
        } else {
            // If we don't have a video guide, hide the YouTubePlayerFragment.
            hidePlayer();
        }
    }

    private void hidePlayer() {
        View view = fragment.getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void queryDbAndInit() {
        PokeDatabase db = PokeDatabase.getInstance(this);
        String query = "select machines.location, moves.name, move_categories.name, types.name, " +
                "types.grad_start_color, types.grad_end_color, types.border_color, " +
                "machines.youtube_id, machines.start_time, machines.end_time, " +
                "moves.description, moves.pp, moves.accuracy, moves.power " +
                "from machines " +
                "join moves on moves.id = move_id " +
                "join types on types.id = type_id " +
                "join move_categories on move_category_id = move_categories.id " +
                "join gens on gen_id = gens.id " +
                "where machines.name =? " +
                "and gens.name=?";
        Cursor c = db.getReadableDatabase().rawQuery(query, new String[] {machineName, genName});
        c.moveToFirst();
        machineLocation = c.getString(0);
        moveName = c.getString(1);
        moveCategory = MoveCategory.valueOf(c.getString(2).replaceAll("-", "_").toUpperCase());
        typeName = c.getString(3);
        gradStartColor = Color.parseColor(c.getString(4));
        gradEndColor = Color.parseColor(c.getString(5));
        borderColor = Color.parseColor(c.getString(6));
        videoId = c.getString(7);

        String time = c.getString(8);
        startTime = "".equals(time) ? 0 : toMillis(time);
        time = c.getString(9);
        endTime = "".equals(time) ? 0 : toMillis(time);

        moveDescription = c.getString(10);
        pp = c.getInt(11);
        accuracy = c.getInt(12);
        power = c.getInt(13);
        c.close();
    }

    private static int toMillis(String mmss) {
        String[] split = mmss.split(":");
        int mins = Integer.parseInt(split[0]);
        int secs = Integer.parseInt(split[1]);
        return ((60 * mins) + secs) * 1000;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        final YouTubePlayer player, boolean wasRestored) {
        DisplayMachineActivity.this.player = player;
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT |
                                        YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
        player.setOnFullscreenListener(this);

        if (!wasRestored) {
            player.cueVideo(videoId, startTime);
        }
        handler.postDelayed(checkTime, 1000);
    }

    @SuppressWarnings("deprecation")
    private void doLayout() {
        View view = fragment.getView();
        ViewGroup.LayoutParams playerParams = view != null ? view.getLayoutParams() : null;
        if (isFullScreen) {
            toolbar.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            if (playerParams != null) {
                playerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        } else {
            toolbar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            if (playerParams != null) {
                playerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            TextView nameView = (TextView) findViewById(R.id.machine_name);
            nameView.setText(machineName + " - " + moveName);

            TextView typeView = (TextView) findViewById(R.id.move_type);
            typeView.setText(typeName);
            typeView.setBackgroundDrawable(PokemonType.createGradient(gradStartColor, gradEndColor, borderColor));

            ImageView categoryView = (ImageView) findViewById(R.id.move_category);
            categoryView.setBackgroundDrawable(moveCategory.createGradient());
            categoryView.setImageResource(moveCategory.getResId());

            TextView locationText = (TextView) findViewById(R.id.machine_location);
            locationText.setText(machineLocation);

            TextView moveText = (TextView) findViewById(R.id.move_description);
            moveText.setText(moveDescription);

            TextView powerText = (TextView) findViewById(R.id.power_stat);
            powerText.setText(power == 0 ? "-" : String.valueOf(power));

            TextView accText = (TextView) findViewById(R.id.accuracy_stat);
            accText.setText(accuracy == 0 ? "-" : String.valueOf(accuracy));

            TextView ppText = (TextView) findViewById(R.id.pp_stat);
            ppText.setText(String.valueOf(pp));
        }
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
            if (!statBarsSetup && !isFullScreen) {
                // only do this once per orientation
                statBarsSetup = true;
                setupStatBar(R.id.power_rectangle, power, 120);
                setupStatBar(R.id.accuracy_rectangle, accuracy, 120);
                setupStatBar(R.id.pp_rectangle, pp, 40);
            }

            ValueAnimator statAnim = ValueAnimator.ofFloat(0f, 1f);
            statAnim.setDuration(ANIM_DURATION);
            statAnim.addUpdateListener(new BarAnimatorUpdateListener(ALL_STAT_BARS));
            statAnim.start();
        }
    }

    /**
     * After the first layout pass is finished, dynamically size the stat bars based on
     * available screen width.
     *
     * @param rectId    resource id for rectangle shape
     * @param statValue stat
     */
    private void setupStatBar(int rectId, int statValue, int maxScale) {
        View rectangle = findViewById(rectId);
        LayerDrawable layer = (LayerDrawable) rectangle.getBackground();
        int color = createColorFromStat(statValue, maxScale);
        layer.findDrawableByLayerId(R.id.stat_color).setColorFilter(color, PorterDuff.Mode.SRC_OVER);

        int start = rectangle.getLeft();
        int end = rectangle.getRight();
        float scale = (end - start) / (float) maxScale;

        ViewGroup.LayoutParams params = rectangle.getLayoutParams();
        params.width = (int) (Math.min(statValue, maxScale) * scale);
        rectangle.setLayoutParams(params);
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
                View rect = findViewById(ids[i]);
                finalWidths[i] = rect.getLayoutParams().width;
                rect.setVisibility(View.VISIBLE);
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

    /**
     * Magic function to get stat colors.
     *
     * @param stat the stat
     * @param max the value for which
     * @return the color as an integer
     */
    private int createColorFromStat(int stat, int max) {
        int adjustedStat = (int) (float) (255 / max) * stat;
        int n = (int) Math.floor(2.55 * Math.min(Math.max(adjustedStat - 50, 0), 100));
        int r = Math.min(2 * (255 - n), 255);
        int g = Math.min(2 * n, 255);
        int b = (int) Math.floor(4.25 * Math.min(Math.max(adjustedStat - 140, 0), 60));
        return Color.rgb(r, g, b);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_machine, menu);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    // Keep track of player full screen state.  Back press on full screen should minimize the player.
    @Override
    public void onFullscreen(boolean b) {
        isFullScreen = b;
        doLayout();
    }

    // If the player is full screen, minimize on back press.  Otherwise, handle normally.
    @Override
    public void onBackPressed() {
        if (isFullScreen && player != null) {
            player.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
        handler.removeCallbacks(checkTime);
    }

    final Handler handler = new Handler();
    final Runnable checkTime = new Runnable() {
        @Override
        public void run() {
            try {
                if (player.getCurrentTimeMillis() > endTime) {
                    // If we're over the end time, seek back to the start and pause.  Pressing
                    // play will be like restarting the video.
                    player.pause();
                    player.seekToMillis(startTime);
                } else if (player.getCurrentTimeMillis() < startTime - 10000) {
                    // Ads sometimes mess up with the seek, particularly when there are two ads
                    // in a row.  If the time is more than 10 seconds before the start time, re-seek.
                    player.seekToMillis(startTime);
                }
                // Check player time until activity is destroyed, in case play is hit again.  This
                // method is preferred over YouTubePlayer callbacks, as seeking in onPlay has
                // inconsistent results.
                handler.postDelayed(this, 1000);
            } catch (Exception e) {
                Log.w(LOG_TAG, e);
                // If the player was already released or this fails for any other reason,
                // stop monitoring the video time.
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) { }
}
