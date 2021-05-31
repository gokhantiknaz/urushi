package com.urushiLeds.urushileds.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.urushi.urushileds.R;
import com.urushiLeds.urushileds.Class.LocalDataManager;

public class Fragment3 extends Fragment {
    private TextView tv_testSeekBar1,tv_testSeekBar2,tv_testSeekBar3,tv_testSeekBar4;
    private SeekBar test_seekBar1,test_seekBar2,test_seekBar3,test_seekBar4;
    private String model = "test";
    private LocalDataManager localDataManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment3,container,false);
        init(view);

        test_seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar1.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),model+"f1",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        test_seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar2.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),model+"f2",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        test_seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar3.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),model+"f3",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        test_seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar4.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),model+"f4",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    public void init(View view){
        tv_testSeekBar1 = view.findViewById(R.id.tv_testSeekBar1);
        tv_testSeekBar2 = view.findViewById(R.id.tv_testSeekBar2);
        tv_testSeekBar3 = view.findViewById(R.id.tv_testSeekBar3);
        tv_testSeekBar4 = view.findViewById(R.id.tv_testSeekBar4);

        test_seekBar1 = view.findViewById(R.id.test_seekBar1);
        test_seekBar2 = view.findViewById(R.id.test_seekBar2);
        test_seekBar3 = view.findViewById(R.id.test_seekBar3);
        test_seekBar4 = view.findViewById(R.id.test_seekBar4);

        localDataManager = new LocalDataManager();

        String prg1 = localDataManager.getSharedPreference(getContext(),"testf1","0");
        String prg2 = localDataManager.getSharedPreference(getContext(),"testf2","0");
        String prg3 = localDataManager.getSharedPreference(getContext(),"testf3","0");
        String prg4 = localDataManager.getSharedPreference(getContext(),"testf4","0");

        tv_testSeekBar1.setText("% "+prg1);
        tv_testSeekBar2.setText("% "+prg2);
        tv_testSeekBar3.setText("% "+prg3);
        tv_testSeekBar4.setText("% "+prg4);

        test_seekBar1.setProgress(Integer.parseInt(prg1));
        test_seekBar2.setProgress(Integer.parseInt(prg2));
        test_seekBar3.setProgress(Integer.parseInt(prg3));
        test_seekBar4.setProgress(Integer.parseInt(prg4));
    }
}
