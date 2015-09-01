package io.github.iluxonchik.colorpickerdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.github.iluxonchik.colorpicker.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] selected = new int[2];
        selected[0] = Color.BLACK;
        selected[1] = Color.CYAN;

        int[] colors = new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW};

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true) // force the usage of material dialog
                .maxSelectedColors(3)
                .colors(colors)
                .numColumns(2)
                .build();
        colorPickerDialog.show(getFragmentManager(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Log.d("Main", Integer.toString(Color.parseColor("#33b5e5")));
            Log.d("Main", Integer.toString(Color.parseColor("#aa66cc")));
            Log.d("Main", Integer.toString(Color.parseColor("#99cc00")));
            Log.d("Main", Integer.toString(Color.parseColor("#ffbb33")));
            Log.d("Main", Integer.toString(Color.parseColor("#ff4444")));
            Log.d("Main", Integer.toString(Color.parseColor("#0099cc")));
            Log.d("Main", Integer.toString(Color.parseColor("#9933cc")));
            Log.d("Main", Integer.toString(Color.parseColor("#669900")));
            Log.d("Main", Integer.toString(Color.parseColor("#ff8800")));
            Log.d("Main", Integer.toString(Color.parseColor("#cc0000")));
            Log.d("Main", Integer.toString(Color.parseColor("#ffffff")));
            Log.d("Main", Integer.toString(Color.parseColor("#eeeeee")));
            Log.d("Main", Integer.toString(Color.parseColor("#cccccc")));
            Log.d("Main", Integer.toString(Color.parseColor("#888888")));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}