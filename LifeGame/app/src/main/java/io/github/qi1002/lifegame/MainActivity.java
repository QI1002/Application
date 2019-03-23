package io.github.qi1002.lifegame;

import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int fpsHZ = 33;
    private PaintView mPaintView;
    private TextView mFpsText;
    private SeekBar mSeekBar;
    private String mFpsPrefix;
    private boolean mIsRun = false;
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaintView = findViewById(R.id.paintView);
        findViewById(R.id.op).setOnClickListener(this);

        mSeekBar = findViewById(R.id.fps);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mFpsText = findViewById(R.id.fps_text);
        mFpsPrefix = getString(R.string.fps);
        mFpsText.setText(mFpsPrefix + getProgress());
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mFpsText.setText(mFpsPrefix + getProgress());
            if (!mIsRun) return;
            stopTimer();
            startTimer();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (mIsRun) stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsRun) startTimer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.op: {
                Button btn = (Button) view.findViewById(R.id.op);
                mIsRun = !mIsRun;
                if (mIsRun) {
                    btn.setText(getString(R.string.pause));
                    startTimer();
                }else {
                    btn.setText(getString(R.string.play));
                    stopTimer();
                }
                break;
            }
        }
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
                mPaintView.update();
            }
        };

        mTimer.schedule(updateTask, 0, fpsHZ * getProgress());
    }

    private void stopTimer() {
        mTimer.cancel();
        mTimer = null;
    }
}


// TODO:
// have a interface to switch default animation and game animation
// consider frame not do on time
// consider MVVM
// consider how to test it ?
// find the display fps
// test diff size phone or tablet
// sync protection