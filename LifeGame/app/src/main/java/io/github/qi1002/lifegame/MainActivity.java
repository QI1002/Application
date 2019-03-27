package io.github.qi1002.lifegame;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.qi1002.lifegame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private GameViewModel mViewModel;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        mBinding.setViewModel(mViewModel);
        mViewModel.onCreate(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onResume();
    }
}


// TODO:
// consider frame not do on time
// consider how to test it ?
// find the display fps
// test diff size phone or tablet
// sync protection