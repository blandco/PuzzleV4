package com.joelbland.puzzle;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class DynamicFontSizing {
    public static final int MAX_FONT_SIZE = 200;
    public static final int MIN_FONT_SIZE = 1;

    public static int setFontSizeToFitInView ( TextView tv) {
        int fontSize = MAX_FONT_SIZE;
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int lines = tv.getLineCount();
        if(lines > 0) {
            while(lines != 1 && fontSize >= MIN_FONT_SIZE) {
                fontSize--;
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                lines = tv.getLineCount();
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,--fontSize);
        }

        return fontSize;
    }
}
