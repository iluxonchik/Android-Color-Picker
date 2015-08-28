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
        final int[] colors = new int[4];
        colors[0] = Color.BLACK;
        colors[1] = Color.BLUE;
        colors[2] = Color.GRAY;
        colors[3] = Color.CYAN;

        int[] selected = new int[2];
        selected[0] = Color.BLACK;
        selected[1] = Color.CYAN;


        if (savedInstanceState != null) {
            Log.d("MainActivity", "SavedInstaceState not null!");
            ColorPickerDialog colorPickerDialog =
                    (ColorPickerDialog) getFragmentManager().findFragmentByTag("cal");
            if (colorPickerDialog != null) {
                colorPickerDialog.setOnOkCancelPressListener(new ColorPickerDialog.OnOkCancelPressListener() {
                    @Override
                    public void onColorPickerDialogOkPressed(int[] selectedColors) {
                        Log.d("MainActivity", "Okay " + selectedColors.length);
                        for (int color : selectedColors) {
                            Log.d("MainActivity", Integer.toString(color));
                        }
                    }

                    @Override
                    public void onColorPickerDialogCancelPressed(int[] selectedColors) {
                        Log.d("MainActivity", "Cancel");
                    }
                });
            }
        } else {

            ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                    .maxSelectedColors(2)
                    .useMaterial(true)
                    .colorContentDescriptions(new String[] {"Hello", "World"})
                    .build();
            colorPickerDialog.show(getFragmentManager(), "cal");

            colorPickerDialog.setOnOkCancelPressListener(new ColorPickerDialog.OnOkCancelPressListener() {
                @Override
                public void onColorPickerDialogOkPressed(int[] selectedColors) {
                    Log.d("MainActivity", "Okay");

                    for (int color : selectedColors) {
                        Log.d("MainActivity", Integer.toString(color));
                    }
                }

                @Override
                public void onColorPickerDialogCancelPressed(int[] selectedColors) {
                    Log.d("MainActivity", "Cancel");
                }
            });
        }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}