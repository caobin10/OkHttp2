package org.chuck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import org.chuck.R;

/**
 * Created by Administrator on 15-12-17.
 */
public class LinedEditText extends EditText {
    private Paint paint = new Paint();

    public LinedEditText(Context context) {
        super(context);
        init();
    }

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.paper_line));
        paint.setStrokeWidth(getLineHeight() / 10);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startX = getPaddingLeft();
        float stopX = getWidth() - getPaddingRight();
        float offsetY = getPaddingTop() - getPaint().getFontMetrics().top + paint.getStrokeWidth() / 2;

        for (int i = 0; i < getLineCount(); ++i) {
            float y = offsetY + getLineHeight() * i;
            canvas.drawLine(startX, y, stopX, y, paint);
        }

        super.onDraw(canvas);
    }
}
