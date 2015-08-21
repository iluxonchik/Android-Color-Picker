package io.github.iluxonchik.colorpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by ILUXONCHIK on 21/08/2015.
 */
public class ColorPickerSwatch extends FrameLayout implements View.OnClickListener {

    /**
     * Interface for a callback when a color square is selected
     */
    public interface OnColorSelectedListener {
        public void onColorSelected(int color);
    }

    public ColorPickerSwatch(Context context, int color, boolean checked,
                             OnColorSelectedListener listener) {
        super(context);

        // TODO
    }

    @Override
    public void onClick(View v) {

    }
}
