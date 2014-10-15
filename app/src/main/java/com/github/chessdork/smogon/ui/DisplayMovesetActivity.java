package com.github.chessdork.smogon.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.chessdork.smogon.Moveset;
import com.github.chessdork.smogon.R;

public class DisplayMovesetActivity extends Activity {
    public static final String MOVESET_OBJECT = "MOVESET_OBJECT";

    private Moveset moveset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_moveset);
        moveset = (Moveset) getIntent().getSerializableExtra(MOVESET_OBJECT);

        final ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        setupUi();
    }

    private void setupUi() {
        TextView name = (TextView) findViewById(R.id.moveset_name);
        name.setText(moveset.getName());

        TextView description = (TextView) findViewById(R.id.moveset_description);
        description.setText(Html.fromHtml(moveset.getDescription()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_moveset, menu);
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
}
