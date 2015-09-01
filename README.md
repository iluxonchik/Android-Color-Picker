# Android Color Picker

This is an Andorid color picker dialog that allows the user to select one or multiple colors.
It's a fork of the [Google's calendar color picker][ColorPickerGoogle], which was re-written and
additional functionality was added, notably the **ablity to select multiple colors**.

# Usage

`ColorPickerDialog` uses the **builder pattern** to create instances of itself. The demo app (**TO BE ADDED!**) 
also contains some examples.

## Basic Material Color Picker (Max One Selected Color)

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true) // force the usage of material dialog
                .build();
        colorPickerDialog.show(getFragmentManager(), null);

## Adding A Listener

To add a listener, simply call the `setOnOkCancelPressListener()` on the `Builder` object, 
proving it with an instance of `ColorPickerDialog.OnOkCancelPressListener` (either implement the
`ColorPickerDialog.OnOkCancelPressListener` intefrace in your activity or fragment or use an anonymous class.

You'll have to override two methods: `public void onColorPickerDialogOkPressed(int[] selectedColors)` and
`public void onColorPickerDialogOkPressed(int[] selectedColors)` both of them receive an array of the **selected
colors in the `ColorPickerDialog`, at the time "Ok" or "CANCEL" was pressed**. 

Here is the anonymous class approach (completing the previous example):

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true) // force the usage of material dialog
                .setOnOkCancelPressListener(new ColorPickerDialog.OnOkCancelPressListener() {
                    @Override
                    public void onColorPickerDialogOkPressed(int[] selectedColors) {
                        Log.d("MainActivity", "Ok pressed");
                        if (selectedColors != null) {
                            for (int color : selectedColors) {
                                Log.d("MainActivity", Integer.toString(color));
                            }
                        }
                    }

                    @Override
                    public void onColorPickerDialogCancelPressed(int[] selectedColors) {
                        Log.d("MainActivity", "CANCEL pressed");
                    }
                })
                .build();
        colorPickerDialog.show(getFragmentManager(), null);
        
It's also possible to change the dialog's listener after it's initialized, by calling `setOnOkCancelPressListener()`
on the `ColorPickerDialog` instance.

## Allowing Selection Of Multiple Colors

To set the maximum number of colors which the user can select, use the `maxSelectedColors()` method. Simply
pass in the upper limit of colors which the user can select as it's only argument. This method should be called
on the `Builder` object and the dialog's behaviour is described in the documentation.

In this exmaple, let's limit the number of selected colros to 3, this means that the user will be able to select
0, 1, 2 or 3 colors:

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true)
                .maxSelectedColors(3) // limit maximum number of selected colors to 3
                .build();
        colorPickerDialog.show(getFragmentManager(), null);
        
## Custom Colors In Color Picker Dialog

To display a custom list of colors in the dialog, use the `colors()` method on the `Builder` object,
passing in an array of colors to display as an argument.

In the example below, the resulting dialog will contain 3 color swatches: red, green and blue:

        int[] colors = new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.GRAY};

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true)
                .maxSelectedColors(3)
                .colors(colors) // set the colors array to display
                .build();
        colorPickerDialog.show(getFragmentManager(), null);
        
## Custom Number Of Columns In Color Picker Dialog

The default value of columns is 4, if you want to change it, use the `numColumns()` method on the
`Builder` object. This will set the number of columns the color palette has.

Let's set that number to 2:

        int[] colors = new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.GRAY};

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true)
                .maxSelectedColors(3)
                .colors(colors)
                .numColumns(2) // set the number of columns to 2
                .build();
        colorPickerDialog.show(getFragmentManager(), null);

## Set Selected Colors

To set the selected colors, call the `selectedColors()` method on the `Builder` object, passing
in the array of colors which you want to mark as selected in the color pikcer dialog.

Please note that this assumes, that the color palette provided in `colors()` does not contain
repeated colors.

If we wanted to mark the red and blue colors as selected on the dialog from the beginning,
we could use the follwing code:

        int[] colors = new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.GRAY};
        int[] selectedColors = new int[] {Color.RED, Color.BLUE}; // the order doesn't matter
        
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .useMaterial(true)
                .maxSelectedColors(3)
                .colors(colors)
                .numColumns(2)
                .selectedColors(selectedColors) // set which colors to mark as selected
                .build();
        colorPickerDialog.show(getFragmentManager(), null);



# Documentation

There is some basic documentation available in-code, here is the one extracted from the `Builder` class:

## `public static class Builder`

Builder for {@link #ColorPickerDialog}.

## `public Builder titleResId(int value)`

Set the title using the given resource id. Default value is "Select a Color".

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder colors(int[] value)`

Set the color array to be displayed in the palette.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder selectedColors(int[] value)`

Set which colors are selected in a palette. Assumes that there are no duplicate colors.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder numColumns(int value)`

Sets the number of columns in the palette. Default value is 4.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder swatchSize(int value)`

Sets the size of the color swatch. Size should be a pre-defined size (SIZE_LARGE or SIZE_SMALL) from ColorPickerDialogFragment.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder maxSelectedColors(int value)`

Sets the maximum possible number of selected colors in the palette. The user can select less, but no more than the specified value. If the user tries to select more colors than this value, a color will be de-selected before selecting the one pressed by the user, according to the following rules: 

1. If it's the first color to be selected since the dialog instantiation, the first color in the selected colors array will be de-selected. 2. If it's not the first color to be selected sine the dialog instantiation, the last selected color will be de-selected. 

If no parameter is specified, the default value 1 is used, which represents a dialog, where the user can select a single color. The parameter must be greater than zero.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder useMaterial(boolean value)`

Sets whether do or not force the material dialog design. If this value is set to true, android.support.v7.app.AlertDialog is used, otherwise android.app.AlertDialog is.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder colorContentDescriptions(String[] value)`

Sets the content descriptions for the color swatches. Each item at the index i will be attributed to the swatch with color at the portion i in the provided colors array.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder useDefaultColorContentDescriptions(boolean useDefaultColorContentDescriptions)`

Sets weather to use the default color content descriptions. The default description consists of the following string: "Color [index]", where [index] is the accessibility index of the color swatch. 

Default content descriptions can be mixed with custom ones specified using the {@link #colorContentDescriptions(String[])} colorContentDescriptions} method. For examples, if the colors array has 10 colors, and the colorContentDescriptions array has 7 elements in it, then colors in the colors array with indices 0 through 6 will have the corresponding descriptions from the colorContentDescriptions array and colors with indices 7 though 9 will have the default ones.

 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public Builder setOnOkCancelPressListener(OnOkCancelPressListener listener)`

Sets the listener that gets called when the "Ok" or the "CANCEL" button gets pressed on the color picker dialog. 

You'll have to override two methods:

 * **Parameters:** `listener` — an instance of the listener to assign
 * **Returns:** This Builder object to allow for chaining of calls to set methods

## `public ColorPickerDialog build()`

Builds the ColorPickerDialog.

 * **Returns:** color picker dialog



[ColorPickerGoogle]: https://android.googlesource.com/platform/frameworks/opt/colorpicker/ "Google Calendar Color Picker Source"