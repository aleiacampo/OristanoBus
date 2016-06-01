package com.aleiacampo.oristanobus.util;

        import android.content.Context;
        import android.util.AttributeSet;
        import android.widget.Spinner;

public class NoDefaultSpinner extends Spinner {

    public NoDefaultSpinner(Context context) {
        super(context);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}