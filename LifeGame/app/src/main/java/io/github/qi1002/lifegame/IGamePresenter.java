package io.github.qi1002.lifegame;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.InputStream;

public interface IGamePresenter {
    public void init(int width, int height, Paint paint, Resources res);
    public void draw(Canvas canvas, Paint paint);
    public void touch(float x, float y);
}
