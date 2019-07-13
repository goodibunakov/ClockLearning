package ru.goodibunakov.clocklearning;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

public class CircularClockSeekBarMinute extends CircularClockSeekBar {

    public CircularClockSeekBarMinute(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        isHourHand = false;
    }

    public CircularClockSeekBarMinute(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isHourHand = false;
    }

    void setHandDrawables(Resources r){
        mHourHand = r.getDrawable(R.drawable.minute);
        mMinuteHand = r.getDrawable(R.drawable.empty);
    }
}
