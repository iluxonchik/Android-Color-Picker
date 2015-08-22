package io.github.iluxonchik.colorpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

/**
 * A dialog which takes in a input an array of colors and creates a pallete allowing the user to
 * select one or more color swatches, which invokes a listener.
 *
 * Created by ILUXONCHIK on 21/08/2015.
 */
public class ColorPickerDialog extends DialogFragment {

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    protected AlertDialog alertDialog;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions"; // TODO
    protected static final String KEY_SELECTED_COLORS = "selected_color";
    protected static final String KEY_NUM_COLUMNS = "columns";
    protected static final String KEY_SWATCH_SIZE = "size";

    protected int titleResId = R.string.dialog_title;
    protected int[] colors = null; // colors to show in palette
    protected String[] colorContentDescription = null; // TODO
    protected int[] selectedColors;
    protected int numColumns; // number of columns in palette
    protected int swatchSize; // used for circle height/width

    protected boolean showOkCancelButtons = true; // TODO: do something with this later?

    private ColorPickerPalette palette;
    private ProgressBar progressBar;

    // TODO: is this listener even need? Maybe use a listener from ColorPickerSwath
    protected ColorPickerSwatch.OnColorSelectedListener listener;

    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments
    }


    /**
     * Created a ColorDialog instance that allows multiple colors to be chosen.
     */
    public static ColorPickerDialog newMultipleChoiceInstance(int titleResId, int[] colors, int selectedColors[],
                                                              int columns, int size) {
        // TODO
        return null;
    }

    /**
     * Created a ColorDialog instance that allows a single to be chosen.
     */
    public static ColorPickerDialog newSingleChoiceInstance(int titleResId, int[] colors, int selectedColor,
                                                            int columns, int size) {

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();

        return colorPickerDialog;
    }

    public void initialize(int titleResId, int[] colors, int[] selectedColors, int numColumns, int swatchSize) {
        setArguments(titleResId, numColumns, swatchSize);
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
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            titleResId = getArguments().getInt(KEY_TITLE_ID);
            numColumns = getArguments().getInt(KEY_NUM_COLUMNS);
            swatchSize = getArguments().getInt(KEY_SWATCH_SIZE);
        }

        if (savedInstanceState != null) {
            colors = savedInstanceState.getIntArray(KEY_COLORS);
            selectedColors = savedInstanceState.getIntArray(KEY_SELECTED_COLORS);
            colorContentDescription = savedInstanceState.getStringArray(
                    KEY_COLOR_CONTENT_DESCRIPTIONS);
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
            showPalleteView();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
                .setTitle(titleResId)
                .setView(view);

        // Add positve and negative buttons to dialog, if needed
        if (showOkCancelButtons) {
            alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO
                }
            })
             .setNegativeButton(R.string.dialog_negative_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO
                        }
             });
        }

        alertDialog = alertDialogBuilder.create();

        return alertDialog;

    }

    public void showPalleteView() {
        if (progressBar != null && palette != null) {
            progressBar.setVisibility(View.GONE);
            refreshPalette();
            palette.setVisibility(View.VISIBLE);
        }
    }
}
