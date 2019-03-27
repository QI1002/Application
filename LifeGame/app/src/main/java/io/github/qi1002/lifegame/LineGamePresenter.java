package io.github.qi1002.lifegame;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.InputStream;

public class LineGamePresenter implements IGamePresenter {

    private int foregroundColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private int count = 0;
    private int mWidth, mHeight;

    @Override
    public void init(int width, int height, Paint paint, Resources res) {
        mWidth = width;
        mHeight = height;
        paint.setColor(foregroundColor);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        count += 10;
        if (count > mHeight) count = 0;
        canvas.drawColor(backgroundColor);
        canvas.drawRect(0, 0, mWidth, count, paint);
    }

    @Override
    public void touch(float x, float y) {
        // draw will add 10, so reduce 10 first
        count = Math.round(y) - 10;
    }
}
