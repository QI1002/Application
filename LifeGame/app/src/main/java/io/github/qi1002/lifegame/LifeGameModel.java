package io.github.qi1002.lifegame;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LifeGameModel {

    private int mWidth;
    private int mHeight;
    private HandlerThread mThread;
    private Handler mHandler;
    private HashSet<Integer> mData = new HashSet<>();
    private HashSet<Integer> mAdd = new HashSet<>();
    private HashSet<Integer> mRemove = new HashSet<>();
    private HashSet<Integer> mAppend = new HashSet<>();

    public static int putXY(int y, int x) {
        return y << 16 | x;
    }
    public static Pair<Integer, Integer> getXY(int i) {
        return new Pair<Integer, Integer>(i >> 16, i & 0xffff);
    }

    public LifeGameModel (int[][] first) {
        mHeight = first.length-1;
        mWidth = first[0].length-1;
        if (first != null) init(first);

        mThread = new HandlerThread("LifeOfGame");
        mThread.start();

        mHandler = new Handler(mThread.getLooper()){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case 1:
                        compute();
                        break;
                }
            }
        };
    }

    private void init(int[][] first) {
        int h = first.length;
        int w = first[0].length;
        for(int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (first[i][j] == 1)
                    mAdd.add(putXY(i, j));
            }
        }

        mData = mAdd;
    }

    private void compute() {
        HashMap<Integer, Integer> check = new HashMap<>();
        HashSet<Integer> newData = new HashSet<>();

        for(Integer i: mData) {
            ArrayList<Integer> list = new ArrayList<>();
            Pair<Integer, Integer> p = getXY(i);
            int y = p.first, x = p.second, k = 0;

            if (y != 0) list.add(putXY(y - 1, x));
            if (y != mHeight) list.add(putXY(y + 1, x));
            if (x != 0) list.add(putXY(y, x - 1));
            if (x != mWidth) list.add(putXY(y, x + 1));
            if (y != 0 && x != 0) list.add(putXY(y - 1, x - 1));
            if (y != mHeight && x != 0) list.add(putXY(y + 1, x - 1));
            if (y != 0 && x != mWidth) list.add(putXY(y - 1, x + 1));
            if (y != mHeight && x != mWidth) list.add(putXY(y + 1, x + 1));

            for(Integer j: list) {
                if (mData.contains(j)) k++;
                else {
                    Integer g = check.get(j);
                    if (g == null) g = 1; else ++g;
                    check.put(j, g);
                }
            }
            if (k == 2 || k == 3) newData.add(i); else mRemove.add(i);
        }

        mData = newData;
        for(Integer t: check.keySet()) {
            if (check.get(t) != 3) continue;
            mData.add(t); mAdd.add(t);
        }
    }

    public Pair<HashSet<Integer>, HashSet<Integer>> getNewResult() {
        mAdd.addAll(mAppend);
        mData.addAll(mAppend);
        mAppend.clear();
        Pair<HashSet<Integer>, HashSet<Integer>> p =
                new Pair<HashSet<Integer>, HashSet<Integer>>(mAdd, mRemove);
        mAdd = new HashSet<>();
        mRemove = new HashSet<>();
        mHandler.sendEmptyMessage(1);
        return p;
    }

    public void add(int y, int x) {
        mAppend.add(putXY(y, x));
    }
}
