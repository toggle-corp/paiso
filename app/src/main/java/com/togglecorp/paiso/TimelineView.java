package com.togglecorp.paiso;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by fhx on 11/14/16.
 */

public class TimelineView extends ImageView {
    private int mWidth, mHeight;
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#212121"));

        Paint fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaint.setStyle(Paint.Style.STROKE);
        fgPaint.setStrokeWidth(1);
        fgPaint.setColor(Color.parseColor("#e04757"));

        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        canvas.drawRect(getWidth()/2, 0, getWidth()/2, getHeight(), fgPaint);

        canvas.drawRect(getWidth()/2, 60, 50+getWidth()/2, 60, fgPaint);
        canvas.drawRect(-50+getWidth()/2, 100, getWidth()/2, 100, fgPaint);
        canvas.drawRect(-50+getWidth()/2, 260, getWidth()/2, 260, fgPaint);
        canvas.drawRect(getWidth()/2, 360, 50+getWidth()/2, 360, fgPaint);

        super.onDraw(canvas);
    }
}
