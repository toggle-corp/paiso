package com.togglecorp.paiso;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

public class TimelineView extends View {
    public static final String TAG = "Timeline View";
    final float scale = getResources().getDisplayMetrics().density;
    private ArrayList<TimeEvent> mTimeEvents;

    private Paint mLinePaint;

    public TimelineView(Context context) {
        super(context);
        this.prepare(context, null);
    }

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.prepare(context, attrs);
    }

    public TimelineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.prepare(context, attrs);
    }

    private void prepare(Context context, AttributeSet attrs){
        this.setFocusable(true);
        this.setClickable(true);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(20);
        mLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        mTimeEvents = new ArrayList<>();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawLine(0,0, getWidth(), getHeight(), mLinePaint);
    }

    private void render(Canvas canvas){

    }

    public class TimeEvent{
        public Date date;
        public String title;
        public long amount;
        public String dpUrl;

        public TimeEvent(){
            date = new Date();
            title = "Bla bla";
            amount = 5000;
        }

        public TimeEvent(Date _date, String _title, long _amount, String _dpUrl){
            date = _date;
            title = _title;
            amount = _amount;
            dpUrl = _dpUrl;
        }
    }
}
