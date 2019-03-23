package io.github.qi1002.lifegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class PaintView extends View {

    public static int PIXEL_DEFAULT_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;

    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private int width, height, count = 0;

    private int currentColor = DEFAULT_COLOR;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int pixelSize = PIXEL_DEFAULT_SIZE;
    private Paint mPaint;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void update() {
        count += 10;
        if (count > height) count = 0;
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mCanvas != null) return;
        width = getWidth();
        height = getHeight();
        Log.d("LifeGame", "LifeGameInfo W = " + Integer.valueOf(width).toString() + " H = " + Integer.valueOf(height).toString());
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currentColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas.drawColor(backgroundColor);
        mCanvas.drawRect(0, 0, width, count, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("LifeGame", "LifeGameInfo X = " + Float.valueOf(x).toString() + " Y = " + Float.valueOf(y).toString());
                count = Math.round(y) - 10;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}