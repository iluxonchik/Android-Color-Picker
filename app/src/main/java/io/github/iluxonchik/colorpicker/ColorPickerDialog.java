package io.github.iluxonchik.colorpicker;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

    public static class Builder {

        private final String NON_POSITVE_NUM_MSG = "Argument must be greater than zero";

        // Optional parameters, initialized to default values
        private int titleResId = R.string.dialog_title;
        private int[] colors = new int[] {Color.RED, Color.GREEN, Color.BLUE};
        private String[] colorContentDescriptions = null; // TODO
        private int[] selectedColors = new int[0];
        private int numColumns = 5;
        private int swatchSize = ColorPickerDialog.SIZE_SMALL;
        private int maxSelectedColors = Integer.MAX_VALUE;
        private boolean useMaterial = false;

        public Builder() { }

        public Builder titleResId(int value) {this.titleResId = value; return this; }
        public Builder colors(int[] value) {this.colors = value; return this; }
        public Builder selectedColors(int[] value) {
            this.selectedColors = value;
            return this;
        }
        public Builder numColumns(int value) { this.numColumns = value; return this; }
        public Builder swatchSize(int value) {
            positiveIntegerCheck(value, NON_POSITVE_NUM_MSG);
            this.swatchSize = value;
            return this;
        }
        public Builder maxSelectedColors(int value) {
            positiveIntegerCheck(value, NON_POSITVE_NUM_MSG);
            this.maxSelectedColors = value;
            return this;
        }
        public Builder useMaterial(boolean value) { this.useMaterial = value; return this; }

        public ColorPickerDialog build() {return ColorPickerDialog.newInstance(this);}

        private void positiveIntegerCheck(int number, String message) {
            if (number < 1) {
                throw new IllegalArgumentException(message);
            }
        }

    }

    public interface OnOkCancelPressListener {
        public void onColorPickerDialogOkPressed(int[] selectedColors);
        public void onColorPickerDialogCancelPressed(int[] selectedColors);
    }

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions"; // TODO
    protected static final String KEY_SELECTED_COLORS = "selected_color";
    protected static final String KEY_NUM_COLUMNS = "columns";
    protected static final String KEY_SWATCH_SIZE = "size";
    protected static final String KEY_INDEX_OF_COLOR = "indexOfColor";
    protected static final String KEY_LAST_SELECTED_COLOR_INDEX = "lastSelectedColorIndex";
    protected static final String KEY_NUM_SELECTED_COLORS = "numSelectedColors";
    protected static final String KEY_MAX_SELECTED_COLORS = "maxSelectedColors";



    protected String LOG_TAG = "io.github.iluxonchik.ColorPickerDialog";

    protected int titleResId = R.string.dialog_title;
    protected int[] colors = null; // colors to show in palette
    protected String[] colorContentDescriptions = null; // TODO
    protected int[] selectedColors;
    protected int numColumns; // number of columns in palette
    protected int swatchSize; // used for circle height/width
    protected boolean[] colorSelected;
    protected HashMap<Integer, Integer> indexOfColor;
    protected int numSelectedColors;
    protected int maxSelectedColors = Integer.MAX_VALUE;
    protected int lastSelectedColorIndex; // index of the last selected color
    protected boolean useMaterialDialog;

    protected boolean showOkCancelButtons = true; // TODO: do something with this later?

    private ColorPickerPalette palette;
    private ProgressBar progressBar;

    protected ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener;
    protected OnOkCancelPressListener onOkCancelPressListener;
    protected DialogInterface.OnClickListener onOkPressedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (onOkCancelPressListener != null) {
                onOkCancelPressListener.onColorPickerDialogOkPressed(getCurrentlySelectedColors());
            }

            if (getTargetFragment() instanceof OnOkCancelPressListener) {
                final OnOkCancelPressListener listener =
                        (OnOkCancelPressListener) getTargetFragment();
                listener.onColorPickerDialogOkPressed(getCurrentlySelectedColors());
            }
        }
    };

    protected DialogInterface.OnClickListener onCancelPressedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (onOkCancelPressListener != null) {
                onOkCancelPressListener.onColorPickerDialogCancelPressed(getCurrentlySelectedColors());
            }

            if (getTargetFragment() instanceof OnOkCancelPressListener) {
                final OnOkCancelPressListener listener =
                        (OnOkCancelPressListener) getTargetFragment();
                listener.onColorPickerDialogCancelPressed(getCurrentlySelectedColors());
            }
        }
    };


    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments
    }


    private static ColorPickerDialog newInstance(Builder builder) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(builder.titleResId, builder.colors, builder.selectedColors,
                builder.numColumns, builder.swatchSize, builder.maxSelectedColors);
        colorPickerDialog.setUseMaterialDialog(builder.useMaterial);
        return colorPickerDialog;
    }


    /**
     * Created a ColorDialog instance that allows multiple colors to be chosen.
     */
    private static ColorPickerDialog newMultipleChoiceInstance(int titleResId, int[] colors, int[] selectedColors,
                                                              int columns, int size) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(titleResId, colors, selectedColors, columns, size);
        return colorPickerDialog;
    }

    /**
     * Created a ColorDialog instance that allows a single to be chosen.
     */
    private static ColorPickerDialog newSingleChoiceInstance(int titleResId, int[] colors, int[] selectedColors,
                                                            int columns, int size) {
        // TODO
        return null;
    }

    public void initialize(int titleResId, int[] colors, int[] selectedColors, int numColumns,
                           int swatchSize, int maxSelectedColors) {
        setArguments(titleResId, numColumns, swatchSize);
        initializeStateVars(selectedColors, colors);
        setColors(colors, colorSelected);
        this.maxSelectedColors = maxSelectedColors;
    }

    public void initialize(int titleResId, int[] colors, int[] selectedColors, int numColumns,
                           int swatchSize) {
        initialize(titleResId, colors, selectedColors, numColumns, swatchSize, Integer.MAX_VALUE);
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
            colorSelected = savedInstanceState.getBooleanArray(KEY_SELECTED_COLORS);
            colorContentDescriptions = savedInstanceState.getStringArray(
                    KEY_COLOR_CONTENT_DESCRIPTIONS);
            indexOfColor = (HashMap<Integer, Integer>) savedInstanceState.getSerializable(KEY_INDEX_OF_COLOR);
            lastSelectedColorIndex = savedInstanceState.getInt(KEY_LAST_SELECTED_COLOR_INDEX);
            numSelectedColors = savedInstanceState.getInt(KEY_NUM_SELECTED_COLORS);
            maxSelectedColors = savedInstanceState.getInt(KEY_MAX_SELECTED_COLORS);
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

        // TODO: This screams refactoring!
        if (useMaterialDialog) {
            return createMaterialDialog(activity, view);
        }
        else {
            return createDefaultDialog(activity, view);
        }
    }

    /**
     * Creates a material dialog.
     * @param context
     * @param view dialog view
     * @return
     */
    private android.support.v7.app.AlertDialog createMaterialDialog(Context context, View view) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder =
                new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setView(view);

        // Add positve and negative buttons to dialog, if needed
        if (showOkCancelButtons) {
            alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button_text, onOkPressedListener)
                    .setNegativeButton(R.string.dialog_negative_button_text, onCancelPressedListener);
        }

        return alertDialogBuilder.create();
    }

    /**
     * Creates a default dialog.
     * @param context
     * @param view dialog view
     * @return
     */
    private android.app.AlertDialog createDefaultDialog(Context context, View view) {
        android.app.AlertDialog.Builder alertDialogBuilder =
                new android.app.AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setView(view);

        // Add positve and negative buttons to dialog, if needed
        if (showOkCancelButtons) {
            alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button_text, onOkPressedListener)
                    .setNegativeButton(R.string.dialog_negative_button_text, onCancelPressedListener);
        }

        return alertDialogBuilder.create();
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
        if (this.colors != colors || this.colorSelected != selectedColors) {
            this.colors = colors;
            this.colorSelected = selectedColors;
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
        if(this.colorSelected != selectedColors) {
            this.colorSelected = selectedColors;
            refreshPalette();
        }
    }

    public void setKeyColorContentDescriptions(String[] contentDescriptions) {
        if (this.colorContentDescriptions != contentDescriptions) {
            this.colorContentDescriptions = contentDescriptions;
            refreshPalette();
        }
    }


    /**
     * Creates a hashmap that maps the color to its position in the colors array and fills the
     * colorSelected array.
     */
    private void initializeStateVars(int[] selectedColors, int[] colors) {
        /* NOTE: be careful if you want to run this on a new thread. If the state vars are not
         * initialized correctly, when a call to ".show()" is made, the dialog might exhibit
         * erroneous behavior.
         */
        numSelectedColors = selectedColors.length;

        HashMap<Integer, Integer> hasMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < colors.length; i++) {
            hasMap.put(colors[i], i);
        }
        indexOfColor = hasMap;
        Log.d(LOG_TAG, "Finished hashmap building");

        colorSelected = new boolean[colors.length];
       // Arrays.fill(colorSelected, false);

        for (int color : selectedColors) {
            colorSelected[indexOfColor.get(color)] = true;
        }

        if(selectedColors.length > 0) {
            // by default, use the first color from seleectedColors are the lastSelectedColorIndex
            lastSelectedColorIndex = indexOfColor.get(selectedColors[0]);
        }

    }

    public int[] getColors() { return colors; }

    public int[] getSelectedColors() { return selectedColors; }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_COLORS, colors);
        outState.putBooleanArray(KEY_SELECTED_COLORS, colorSelected);
        outState.putStringArray(KEY_COLOR_CONTENT_DESCRIPTIONS, colorContentDescriptions);
        outState.putSerializable(KEY_INDEX_OF_COLOR, indexOfColor);
        outState.putInt(KEY_LAST_SELECTED_COLOR_INDEX, lastSelectedColorIndex);
        outState.putInt(KEY_NUM_SELECTED_COLORS, numSelectedColors);
        outState.putInt(KEY_MAX_SELECTED_COLORS, maxSelectedColors);
    }

    private void refreshPalette() {
        if (palette != null && colors != null) {
            palette.drawPalette(colors, colorSelected, colorContentDescriptions);
        }
    }

    @Override
    public void onColorSelected(int color) {
        int index;

        index = indexOfColor.get(color);

        if (numSelectedColors >= maxSelectedColors) {
            // if max num of selected colors has been reached, de-select the last one
            if (index != lastSelectedColorIndex && !colorSelected[index]) {
                colorSelected[lastSelectedColorIndex] = false;
                numSelectedColors--;
            }
        }

        colorSelected[index] = !colorSelected[index];

        if(colorSelected[index]) {
            // New color was selected, increment the number of selected colors
            lastSelectedColorIndex = index;
            numSelectedColors++;
        } else {
            // Color de-selected, decremented number of selected colors
            numSelectedColors--;
        }

        palette.drawPalette(colors, colorSelected);
    }

    /**
     * Get an array of currently selected colors.
     */
    public int[] getCurrentlySelectedColors() {
        if (numSelectedColors < 1)
            return null;

        int[] currentlySelectedColors = new int[numSelectedColors];
        int j =0;
        for (int i = 0; i < colorSelected.length; i++) {
            if (colorSelected[i]) {
                currentlySelectedColors[j++] = colors[i];
            }
        }

        if (currentlySelectedColors.length != numSelectedColors) {
            Log.e("MainActivity", "Error in color counting. Expected = " + numSelectedColors +
                    " Obtained = " + currentlySelectedColors.length);
        }

        return currentlySelectedColors;
    }


    public boolean isMaterialDialog() {
        return useMaterialDialog;
    }

    protected void setUseMaterialDialog(boolean useMaterialDialog) {
        this.useMaterialDialog = useMaterialDialog;
    }

}
