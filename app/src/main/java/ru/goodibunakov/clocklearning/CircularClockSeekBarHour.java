package ru.goodibunakov.clocklearning;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

public class CircularClockSeekBarHour extends CircularClockSeekBar {

    public CircularClockSeekBarHour(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        isHourHand = true;
    }

    public CircularClockSeekBarHour(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isHourHand = true;
    }

    void setHandDrawables(Resources r){
        mHourHand = r.getDrawable(R.drawable.hour);
        mMinuteHand = r.getDrawable(R.drawable.empty);
    }
}
