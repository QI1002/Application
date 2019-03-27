package io.github.qi1002.lifegame;

import android.arch.lifecycle.ViewModel;
import android.content.res.Resources;
import android.databinding.ObservableField;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.min;

public class GameViewModel extends ViewModel {

    private static final int fpsHZ = 33;
    private boolean mIsRun = false;
    private Timer mTimer = null;
    private String mFpsPrefix, mRunPlay, mRunPause;

    private PaintView mPaintView;
    private SeekBar mSeekBar;
    private IGamePresenter mPresenter = new LifeGamePresenter();
    //private IGamePresenter mPresenter = new LineGamePresenter();

    public ObservableField<String> mRunText = new ObservableField<>("");
    public ObservableField<String> mFpsText = new ObservableField<>("");
    public void onCreate(AppCompatActivity activity) {
        mPaintView = activity.findViewById(R.id.paintView);
        mSeekBar = activity.findViewById(R.id.fps);
        mFpsPrefix = activity.getString(R.string.fps);
        mRunPlay = activity.getString(R.string.play);
        mRunPause = activity.getString(R.string.pause);
        mFpsText.set(mFpsPrefix + Integer.valueOf(getProgress()).toString());
        mRunText.set((mIsRun) ? mRunPause : mRunPlay);
        mPaintView.setViewModel(this);
    }

    public void onPause() {
        if (mIsRun) stopTimer();
    }

    public void onResume() {
        if (mIsRun) startTimer();
    }

    public void onLayout(int width, int height, Paint paint, Resources res) {
        mPresenter.init(width, height, paint, res);
    }

    public void onFpsChanged() {
        mFpsText.set(mFpsPrefix + Integer.valueOf(getProgress()).toString());
        if (!mIsRun) return;
        stopTimer();
        startTimer();
    }

    public void onRunChanged() {
        mIsRun = !mIsRun;
        if (mIsRun) {
            mRunText.set(mRunPause);;
            startTimer();
        }else {
            mRunText.set(mRunPlay);
            stopTimer();
        }
    }

    public void onDraw(Canvas canvas, Paint paint) {
        mPresenter.draw(canvas, paint);
    }

    public void onTouch(float x, float y) {
        mPresenter.touch(x, y);
    }

    private int getProgress() {
        int progress = mSeekBar.getProgress();
        if (progress == 0) progress = 1;
        return progress;
    }

    private void startTimer() {
        mTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                mPaintView.postInvalidate();
            }
        };

        mTimer.schedule(updateTask, 0, fpsHZ * getProgress());
    }

    private void stopTimer() {
        mTimer.cancel();
        mTimer = null;
    }
}