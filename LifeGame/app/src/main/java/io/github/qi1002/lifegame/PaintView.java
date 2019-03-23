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

    public static int PIXEL_SIZE = 16; // 90x140
    public static final int DEFAULT_FG_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;

    private int width, height, count = 0;
    private int foregroundColor = DEFAULT_FG_COLOR;
    private int backgroundColor = DEFAULT_BG_COLOR;

    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private Paint mPaint;
    private LifeGame mGame;

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

    private void readData(int data[][]) {
        try {
            int h = data.length, w = data[0].length;
            // https://raw.githubusercontent.com/Peter-Slump/game-of-life/master/patterns/gosper-glider-gun.txt
            InputStream input = getResources().openRawResource(R.raw.gosper_glider_gun);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String str = null; int i, j, k;
            for(i = 0; i < h; i++) {
                str = reader.readLine();
                if (str == null) break;
                k = min(w, str.length());
                for(j = 0; j < k; j++)
                    if (str.charAt(j) == 'X') data[i][j] = 1;
                for(; j < w; j++) data[i][j] = 0;
            }
            for(; i < h; i++) {
                for(j = 0; j < w; j++) data[i][j] = 0;
            }

            reader.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        int data[][] = new int[height/PIXEL_SIZE][width/PIXEL_SIZE];
        readData(data);
        mGame = new LifeGame(data);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //mCanvas.drawColor(backgroundColor);
        //mCanvas.drawRect(0, 0, width, count, mPaint);
        Pair<HashSet<Integer>, HashSet<Integer>> q =
            mGame.getNewResult();

        mPaint.setColor(backgroundColor);
        for(Integer i: q.second) {
            Pair<Integer, Integer> p = LifeGame.getXY(i);
            int y = p.first, x = p.second;
            mCanvas.drawRect(x*PIXEL_SIZE, y*PIXEL_SIZE,
                    (x+1)*PIXEL_SIZE, (y+1)*PIXEL_SIZE, mPaint);
        }

        mPaint.setColor(foregroundColor);
        for(Integer i: q.first) {
            Pair<Integer, Integer> p = LifeGame.getXY(i);
            int y = p.first, x = p.second;
            mCanvas.drawRect(x*PIXEL_SIZE, y*PIXEL_SIZE,
                    (x+1)*PIXEL_SIZE, (y+1)*PIXEL_SIZE, mPaint);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d("LifeGame", "LifeGameInfo X = " + Float.valueOf(x).toString() + " Y = " + Float.valueOf(y).toString());
                //count = Math.round(y) - 10;
                mGame.add(Math.round(y/PIXEL_SIZE), Math.round(x/PIXEL_SIZE));
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}