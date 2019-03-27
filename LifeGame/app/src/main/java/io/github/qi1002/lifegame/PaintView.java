package io.github.qi1002.lifegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.TextureView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import static java.lang.Math.min;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class PaintView extends View {

    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private Paint mPaint;
    private GameViewModel mViewModel;
    private int mWidth, mHeight;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setViewModel(GameViewModel viewModel) {
        mViewModel = viewModel;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mCanvas != null) return;
        mWidth = getWidth();
        mHeight = getHeight();
        Log.d("LifeGame", "LifeGameInfo W = " + Integer.valueOf(mWidth).toString() + " H = " + Integer.valueOf(mHeight).toString());
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mViewModel.onLayout(mWidth, mHeight, mPaint, getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mViewModel.onDraw(mCanvas, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mViewModel.onTouch(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}