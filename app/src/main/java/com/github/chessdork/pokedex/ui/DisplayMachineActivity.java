package com.github.chessdork.pokedex.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.Keys;
import com.github.chessdork.pokedex.common.PokeDatabase;
import com.github.chessdork.pokedex.models.MoveCategory;
import com.github.chessdork.pokedex.models.PokemonType;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class DisplayMachineActivity extends ActionBarActivity {

    public static final String MACHINE_NAME = "MACHINE_NAME";
    public static final String GEN_NAME = "GEN_NAME";

    private String machineName, machineLocation, moveName, typeName, genName;
    private int gradStartColor, gradEndColor, borderColor;
    private MoveCategory moveCategory;

    private YouTubePlayer player;

    private String videoId;
    private int startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_machine);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        YouTubePlayerFragment fragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);

        machineName = getIntent().getStringExtra(MACHINE_NAME);
        genName = getIntent().getStringExtra(GEN_NAME);

        queryDbAndInit();
        setupStaticUi();

        if (videoId != null && !videoId.equals("")) {
            fragment.initialize(Keys.YOUTUBE_KEY, new InitializationListener());
        } else {
            // If we don't have a video guide, hide the YouTubePlayerFragment.
            View view = fragment.getView();
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    private void queryDbAndInit() {
        PokeDatabase db = PokeDatabase.getInstance(this);
        String query = "select machines.location, moves.name, move_categories.name, types.name, " +
                "types.grad_start_color, types.grad_end_color, types.border_color, " +
                "machines.youtube_id, machines.start_time, machines.end_time " +
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
        startTime = toMillis(c.getString(8));
        endTime = toMillis(c.getString(9));
        c.close();
    }

    private static int toMillis(String mmss) {
        if (mmss.equals("")) {
            return 0;
        }
        String[] split = mmss.split(":");
        int mins = Integer.parseInt(split[0]);
        int secs = Integer.parseInt(split[1]);
        return ((60 * mins) + secs) * 1000;
    }

    @SuppressWarnings("deprecation")
    private void setupStaticUi() {
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
            if(player.getCurrentTimeMillis() > endTime || player.getCurrentTimeMillis() == -1) {
                player.pause();
                player.seekToMillis(startTime);
            }
            // Check player time until activity is destroyed, in case play is hit again.  This
            // method is preferred over YouTubePlayer callbacks, as seeking in onPlay has
            // inconsistent results.
            handler.postDelayed(this, 1000);
        }
    };

    private class InitializationListener implements YouTubePlayer.OnInitializedListener {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                            final YouTubePlayer player, boolean b) {
            DisplayMachineActivity.this.player = player;
            player.cueVideo(videoId, startTime);
            handler.postDelayed(checkTime, 1000);
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                            YouTubeInitializationResult result) { }
    }

}
