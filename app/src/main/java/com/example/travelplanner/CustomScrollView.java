package com.example.travelplanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1) {
            // وقتی بیش از یک انگشت لمس کرد، اسکرول ویو رو رویداد رو نگیره، یعنی بزاره پایین تر هندل بشه
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
}

