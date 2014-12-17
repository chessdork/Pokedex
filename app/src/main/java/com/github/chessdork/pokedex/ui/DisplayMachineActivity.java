package com.github.chessdork.pokedex.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chessdork.pokedex.R;
import com.github.chessdork.pokedex.common.PokeDatabase;
import com.github.chessdork.pokedex.models.MoveCategory;
import com.github.chessdork.pokedex.models.PokemonType;

public class DisplayMachineActivity extends ActionBarActivity {

    public static final String MACHINE_NAME = "MACHINE_NAME";
    private String machineName, machineLocation, moveName, typeName;
    private int gradStartColor, gradEndColor, borderColor;
    private MoveCategory moveCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_machine);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        machineName = getIntent().getStringExtra(MACHINE_NAME);

        PokeDatabase db = PokeDatabase.getInstance(this);
        String query = "select machines.location, moves.name, move_categories.name, types.name, " +
                "types.grad_start_color, types.grad_end_color, types.border_color " +
                "from machines " +
                "join moves on moves.id = move_id " +
                "join types on types.id = type_id " +
                "join move_categories on move_category_id = move_categories.id " +
                "where machines.name =?";
        Cursor c = db.getReadableDatabase().rawQuery(query, new String[] {machineName});
        c.moveToFirst();
        machineLocation = c.getString(0);
        moveName = c.getString(1);
        moveCategory = MoveCategory.valueOf(c.getString(2).replaceAll("-","_").toUpperCase());
        typeName = c.getString(3);
        gradStartColor = Color.parseColor(c.getString(4));
        gradEndColor = Color.parseColor(c.getString(5));
        borderColor = Color.parseColor(c.getString(6));
        c.close();

        setupStaticUi();
    }

    @SuppressWarnings("deprecation")
    private void setupStaticUi() {
        TextView nameView = (TextView) findViewById(R.id.machine_name);
        nameView.setText(machineName);

        TextView typeView = (TextView) findViewById(R.id.move_type);
        typeView.setText(typeName);
        typeView.setBackgroundDrawable(PokemonType.createGradient(gradStartColor, gradEndColor, borderColor));

        ImageView categoryView = (ImageView) findViewById(R.id.move_category);
        categoryView.setBackgroundDrawable(moveCategory.createGradient());
        categoryView.setImageResource(moveCategory.getResId());
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
}
