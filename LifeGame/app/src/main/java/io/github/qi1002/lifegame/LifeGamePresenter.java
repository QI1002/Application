package io.github.qi1002.lifegame;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import static java.lang.Math.min;

public class LifeGamePresenter implements IGamePresenter {

    private int foregroundColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    public static int PIXEL_SIZE = 16;
    private LifeGameModel mGame;

    @Override
    public void init(int width, int height, Paint paint, Resources res) {
        int data[][] = new int[height/PIXEL_SIZE][width/PIXEL_SIZE];
        readData(data, res);
        mGame = new LifeGameModel(data);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Pair<HashSet<Integer>, HashSet<Integer>> q = mGame.getNewResult();

        paint.setColor(backgroundColor);
        for(Integer i: q.second) {
            Pair<Integer, Integer> p = LifeGameModel.getXY(i);
            int y = p.first, x = p.second;
            canvas.drawRect(x*PIXEL_SIZE, y*PIXEL_SIZE,
                    (x+1)*PIXEL_SIZE, (y+1)*PIXEL_SIZE, paint);
        }

        paint.setColor(foregroundColor);
        for(Integer i: q.first) {
            Pair<Integer, Integer> p = LifeGameModel.getXY(i);
            int y = p.first, x = p.second;
            canvas.drawRect(x*PIXEL_SIZE, y*PIXEL_SIZE,
                    (x+1)*PIXEL_SIZE, (y+1)*PIXEL_SIZE, paint);
        }
    }

    @Override
    public void touch(float x, float y) {
        mGame.add(Math.round(y/PIXEL_SIZE), Math.round(x/PIXEL_SIZE));
    }

    private void readData(int data[][], Resources res) {
        try {
            int h = data.length, w = data[0].length;
            // https://raw.githubusercontent.com/Peter-Slump/game-of-life/master/patterns/gosper-glider-gun.txt
            InputStream input = res.openRawResource(R.raw.gosper_glider_gun);
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
}
