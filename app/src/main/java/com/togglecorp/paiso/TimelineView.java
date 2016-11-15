package com.togglecorp.paiso;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Date;

/**
 * Created by fhx on 11/14/16.
 */

public class TimelineView extends ImageView {
    private Paint mBgPaint;
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
        this.setScaleType(ScaleType.CENTER_INSIDE);
        this.setClickable(true);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
        canvas.drawRect(getWidth()/2, 0, getWidth()/2, getHeight(), mLinePaint);

        canvas.drawRect(getWidth()/2, 60, 50+getWidth()/2, 60, mLinePaint);
        canvas.drawRect(-50+getWidth()/2, 100, getWidth()/2, 100, mLinePaint);
        canvas.drawRect(-50+getWidth()/2, 260, getWidth()/2, 260, mLinePaint);
        canvas.drawRect(getWidth()/2, 360, 50+getWidth()/2, 360, mLinePaint);

        super.onDraw(canvas);
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
