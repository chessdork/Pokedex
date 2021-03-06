package com.github.chessdork.pokedex.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class VerticalTextView extends TextView {

    public VerticalTextView(Context context) {
        this(context, null);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVerticalGravity();
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleId) {
        super(context, attrs, defStyleId);
        setVerticalGravity();
    }

    // without this, the Natures GridView gets shifted down one pixel
    private void setVerticalGravity() {
        final int gravity = Gravity.BOTTOM | Gravity.CENTER;
        setGravity((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();

        canvas.translate(0, getHeight());
        canvas.rotate(-90);

        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

        getLayout().draw(canvas);
        canvas.restore();
    }
}