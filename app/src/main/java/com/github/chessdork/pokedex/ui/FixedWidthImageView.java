package com.github.chessdork.pokedex.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class FixedWidthImageView extends ImageView{

    public FixedWidthImageView(Context context) {
        super(context);
    }

    public FixedWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();

        if (drawable != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
