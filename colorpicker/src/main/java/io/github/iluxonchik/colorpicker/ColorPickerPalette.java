package io.github.iluxonchik.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;


/**
 * Created by ILUXONCHIK on 21/08/2015.
 */
public class ColorPickerPalette extends TableLayout{

    public ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener;

    private String description;
    private String descriptionSelected;

    private int swatchLength;
    private int marginSize;
    private int numColumns;

    public ColorPickerPalette(Context context) { super(context); }

    public ColorPickerPalette(Context context, AttributeSet attrs) { super(context, attrs); }

    /**
     * Initialize color swatches size and palette's columns and onColorSelectedListener. size should be a
     * pre-defined size (SIZE_LARGE or SIZE_SMALL) from ColorPuckerDialog.
     */
    public void init(int size, int numColumns, ColorPickerSwatch.OnColorSelectedListener listener) {
        this.numColumns = numColumns;
        Resources res = getResources();
        if (size == ColorPickerDialog.SIZE_LARGE) {
            swatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_large);
            marginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margin_large);
        } else {
            swatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_small);
            marginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margin_small);
        }

        this.onColorSelectedListener = listener;
    }

    /**
     * Creates and returns an empty table row
     * @return
     */
    private TableRow createTableRow() {
        TableRow tableRow = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(params);
        return  tableRow;
    }

    /**
     * Adds swatched to table in a serpentine format.
     */
    public void drawPalette(int[] colors, boolean[] selectedColors) {
        drawPalette(colors, selectedColors, null);
    }

    /**
     * Adds swatched to table in a serpentine format.
     */
    public void drawPalette(int[] colors, boolean[] selectedColors, String[] colorContentDescriptions) {
        if (colors == null) {
            return;
        }

        this.removeAllViews();
        int tableElements = 0;
        int rowElements = 0;
        int rowNumber = 0;

        // Fill the table with swatches based on the colors array
        TableRow row = createTableRow();
        int color;
        for (int i = 0; i < colors.length; i++) {
            color = colors[i];
            View colorSwatch = createColorSwatch(color, selectedColors[i]);
            // TODO setSwatchDescription()
            addSwatchToRow(row, colorSwatch, rowNumber);

            tableElements++;
            rowElements++;

            if(rowElements == numColumns) {
                // if num of elems in the row matches the num of columns, pass to the next row
                addView(row);
                row = createTableRow();
                rowElements = 0;
                rowNumber++;
            }
        }

        // Create blank views to fill the row, if the last row has not been filled
        if (rowElements > 0) {
            while(rowElements != numColumns) {
                addSwatchToRow(row, createBlankSpace(), rowNumber);
                rowElements++;
            }
            addView(row);
        }
    }

    /**
     * Appends a swatch to the end of the row for even-numbered rows and to the beginning of the row
     * for odd-numbered rows.
     */
    private static void addSwatchToRow(TableRow row, View swatch, int rowNumber) {
        if (rowNumber % 2 == 0) {
            row.addView(swatch);
        } else {
            row.addView(swatch, 0);
        }
    }

    /**
     * Create a blank image to fill the row.
     */
    private ImageView createBlankSpace() {
        ImageView view = new ImageView(getContext());
        view.setLayoutParams(getSwatchLayoutParams());
        return view;
    }

    /**
     * Creates a color swatch.
     */
    private ColorPickerSwatch createColorSwatch(int color, boolean isColorSelected) {
        //  Pass the ColorPickerDialog (onColorSelectedListener) and register it as onColorSelectedListener
        //      in ColorPickerSwatch, that way a click on the color circle will be
        //      sent to ColorPickerDialog.
        ColorPickerSwatch view = new ColorPickerSwatch(getContext(), color,
                isColorSelected, onColorSelectedListener);
        view.setLayoutParams(getSwatchLayoutParams());
        return view;
    }

    private TableRow.LayoutParams getSwatchLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(swatchLength, swatchLength);
        params.setMargins(marginSize, marginSize, marginSize, marginSize);
        return params;
    }

}
