package io.github.iluxonchik.colorpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.util.HashMap;

/**
 * A dialog which takes in a input an array of colors and creates a pallete allowing the user to
 * select one or more color swatches, which invokes a onColorSelectedListener.
 *
 * Created by ILUXONCHIK on 21/08/2015.
 */
public class ColorPickerDialog extends DialogFragment implements ColorPickerSwatch.OnColorSelectedListener {

    public interface OnOkCancelPressListener {
        public void onColorPickerDialogOkPressed(boolean[] selectedColors);
        public void onColorPickerDialogCancelPressed(boolean[] selectedColors);
    }

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    protected AlertDialog alertDialog;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions"; // TODO
    protected static final String KEY_SELECTED_COLORS = "selected_color";
    protected static final String KEY_NUM_COLUMNS = "columns";
    protected static final String KEY_SWATCH_SIZE = "size";
    protected static final String KEY_INDEX_OF_COLOR = "indexOfColor";

    protected String LOG_TAG = "io.github.iluxonchik.ColorPickerDialog";

    protected int titleResId = R.string.dialog_title;
    protected int[] colors = null; // colors to show in palette
    protected String[] colorContentDescriptions = null; // TODO
    protected boolean[] selectedColors;
    protected int numColumns; // number of columns in palette
    protected int swatchSize; // used for circle height/width
    protected HashMap<Integer, Integer> indexOfColor;

    protected boolean showOkCancelButtons = true; // TODO: do something with this later?

    private ColorPickerPalette palette;
    private ProgressBar progressBar;

    protected ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener;

    protected OnOkCancelPressListener onOkCancelPressListener;

    /**
     * Creates a hashmap that maps the color to its position in the colors array.
     */
    protected Thread createColorToIndexMap = new Thread(new Runnable() {
        @Override
        public void run() {
            HashMap<Integer, Integer> hasMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < colors.length; i++) {
                hasMap.put(colors[i], i);
            }
            indexOfColor = hasMap;
            Log.d(LOG_TAG, "Finished hashmap building");
        }
    });

    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments
    }


    /**
     * Created a ColorDialog instance that allows multiple colors to be chosen.
     */
    public static ColorPickerDialog newMultipleChoiceInstance(int titleResId, int[] colors, boolean selectedColors[],
                                                              int columns, int size) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(titleResId, colors, selectedColors, columns, size);
        return colorPickerDialog;
    }

    /**
     * Created a ColorDialog instance that allows a single to be chosen.
     */
    public static ColorPickerDialog newSingleChoiceInstance(int titleResId, int[] colors, boolean[] selectedColors,
                                                            int columns, int size) {
        // TODO
        return null;
    }

    public void initialize(int titleResId, int[] colors, boolean[] selectedColors, int numColumns, int swatchSize) {
        setArguments(titleResId, numColumns, swatchSize);
        createColorToIndexMap.start();
        setColors(colors, selectedColors);
    }

    public void setArguments(int titleResId, int numColumns, int swatchSize) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE_ID, titleResId);
        bundle.putInt(KEY_NUM_COLUMNS, numColumns);
        bundle.putInt(KEY_SWATCH_SIZE, swatchSize);
        setArguments(bundle);
    }

    public void setOnColorSelectedListener(ColorPickerSwatch.OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
    }

    public void setOnOkCancelPressListener(OnOkCancelPressListener onOkCancelPressListener) {
        this.onOkCancelPressListener = onOkCancelPressListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleResId = getArguments().getInt(KEY_TITLE_ID);
            numColumns = getArguments().getInt(KEY_NUM_COLUMNS);
            swatchSize = getArguments().getInt(KEY_SWATCH_SIZE);
        }

        if (savedInstanceState != null) {
            colors = savedInstanceState.getIntArray(KEY_COLORS);
            selectedColors = savedInstanceState.getBooleanArray(KEY_SELECTED_COLORS);
            colorContentDescriptions = savedInstanceState.getStringArray(
                    KEY_COLOR_CONTENT_DESCRIPTIONS);
            indexOfColor = (HashMap<Integer, Integer>) savedInstanceState.getSerializable(KEY_INDEX_OF_COLOR);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Activity activity = getActivity();

        View view = LayoutInflater.from(activity).inflate(R.layout.color_picker_dialog, null);
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
        palette = (ColorPickerPalette)view.findViewById(R.id.color_picker);
        palette.init(swatchSize, numColumns, this);

        if (colors != null) {
            showPaletteView();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
                .setTitle(titleResId)
                .setView(view);

        // Add positve and negative buttons to dialog, if needed
        if (showOkCancelButtons) {
            alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onOkCancelPressListener != null) {
                        onOkCancelPressListener.onColorPickerDialogOkPressed(selectedColors);
                    }

                    if (getTargetFragment() instanceof OnOkCancelPressListener) {
                        final OnOkCancelPressListener listener =
                                (OnOkCancelPressListener) getTargetFragment();
                        listener.onColorPickerDialogOkPressed(selectedColors);
                    }
                }
            })
             .setNegativeButton(R.string.dialog_negative_button_text, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     if (onOkCancelPressListener != null) {
                         onOkCancelPressListener.onColorPickerDialogCancelPressed(selectedColors);
                     }

                     if (getTargetFragment() instanceof OnOkCancelPressListener) {
                         final OnOkCancelPressListener listener =
                                 (OnOkCancelPressListener) getTargetFragment();
                         listener.onColorPickerDialogCancelPressed(selectedColors);
                     }
                 }
             });
        }

        alertDialog = alertDialogBuilder.create();

        return alertDialog;

    }

    public void showPaletteView() {
        if (progressBar != null && palette != null) {
            progressBar.setVisibility(View.GONE);
            refreshPalette();
            palette.setVisibility(View.VISIBLE);
        }
    }

    public void showProgressBarView() {
        if (progressBar != null && palette != null) {
            progressBar.setVisibility(View.VISIBLE);
            palette.setVisibility(View.GONE);
        }
    }

    public void setColors(int[] colors, boolean[] selectedColors) {
        if (this.colors != colors || this.selectedColors != selectedColors) {
            this.colors = colors;
            this.selectedColors = selectedColors;
            refreshPalette();
        }
    }

    public void setColors(int[] colors) {
        if (this.colors != colors) {
            this.colors = colors;
            refreshPalette();
        }
    }

    public void setKeySelectedColors(boolean[] selectedColors) {
        if(this.selectedColors != selectedColors) {
            this.selectedColors = selectedColors;
            refreshPalette();
        }
    }

    public void setKeyColorContentDescriptions(String[] contentDescriptions) {
        if (this.colorContentDescriptions != contentDescriptions) {
            this.colorContentDescriptions = contentDescriptions;
            refreshPalette();
        }
    }

    public int[] getColors() { return colors; }

    public boolean[] getSelectedColors() { return selectedColors; }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_COLORS, colors);
        outState.putBooleanArray(KEY_SELECTED_COLORS, selectedColors);
        outState.putStringArray(KEY_COLOR_CONTENT_DESCRIPTIONS, colorContentDescriptions);
        outState.putSerializable(KEY_INDEX_OF_COLOR, indexOfColor);
    }

    private void refreshPalette() {
        if (palette != null && colors != null) {
            palette.drawPalette(colors, selectedColors, colorContentDescriptions);
        }
    }

    @Override
    public void onColorSelected(int color) {
        // TODO
        /* Have a boolean array (selected), where each index corresponds to the index in "colors"
         * and just flip the value in that color's position (?)
         */
        int index;

        if (indexOfColor == null) {
            Log.d(LOG_TAG, "indexOfColor hashmap is null");
            // TODO: this will block the Main thread
            if (createColorToIndexMap.isAlive()) {
                // If the thread is still running, wait for it to finish
                try {
                    createColorToIndexMap.join();
                } catch (InterruptedException e) {
                    // Try running the thread again
                    createColorToIndexMap.start();
                }
            }
        }
        index = indexOfColor.get(color);
        selectedColors[index] = !selectedColors[index];
        palette.drawPalette(colors, selectedColors);
    }


}
