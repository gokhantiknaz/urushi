package com.urushiLeds.urushileds.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.urushi.urushileds.R;
import com.urushiLeds.urushileds.Class.LocalDataManager;

public class Fragment3 extends Fragment {
    private TextView tv_testSeekBar1,tv_testSeekBar2,tv_testSeekBar3,tv_testSeekBar4;
    private SeekBar test_seekBar1,test_seekBar2,test_seekBar3,test_seekBar4;
    private String test_model = "test", model;
    private LocalDataManager localDataManager;
    private TextView tv_sb1title,tv_sb2title,tv_sb3title,tv_sb4title;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment3,container,false);
        init(view);

        localDataManager.setSharedPreference(getContext(),"test_model","test");

        model = localDataManager.getSharedPreference(getContext(),"model","manual");

        if (model.equals("manual")){

        }else if (model.equals("fmajor")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.INVISIBLE);
            tv_sb4title.setVisibility(View.INVISIBLE);

            tv_sb1title.setText("Cool White");
            tv_sb2title.setText("Wide Spectrum");

            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.INVISIBLE);
            tv_testSeekBar4.setVisibility(View.INVISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.INVISIBLE);
            test_seekBar4.setVisibility(View.INVISIBLE);
        }else if (model.equals("smajor")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.INVISIBLE);
            tv_sb4title.setVisibility(View.INVISIBLE);

            tv_sb1title.setText("Deep Blue");
            tv_sb2title.setText("Aqua Sun");

            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.INVISIBLE);
            tv_testSeekBar4.setVisibility(View.INVISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.INVISIBLE);
            test_seekBar4.setVisibility(View.INVISIBLE);
        }else if (model.equals("fmax")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.VISIBLE);
            tv_sb4title.setVisibility(View.VISIBLE);

            tv_sb1title.setText("Cool White");
            tv_sb2title.setText("Full Spectrum");
            tv_sb3title.setText("Reddish White");
            tv_sb4title.setText("Blueish Spectrum");

            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.VISIBLE);
            tv_testSeekBar4.setVisibility(View.VISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.VISIBLE);
            test_seekBar4.setVisibility(View.VISIBLE);
        }else if (model.equals("smax")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.VISIBLE);
            tv_sb4title.setVisibility(View.VISIBLE);

            tv_sb1title.setText("Deep Blue");
            tv_sb2title.setText("Aqua Sun");
            tv_sb3title.setText("Magenta");
            tv_sb4title.setText("Sky Blue");

            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.VISIBLE);
            tv_testSeekBar4.setVisibility(View.VISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.VISIBLE);
            test_seekBar4.setVisibility(View.VISIBLE);
        }

        test_seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar1.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f1",""+progress);
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
                localDataManager.setSharedPreference(getContext(),test_model+"f2",""+progress);
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
                localDataManager.setSharedPreference(getContext(),test_model+"f3",""+progress);
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
                localDataManager.setSharedPreference(getContext(),test_model+"f4",""+progress);
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
        tv_sb1title = view.findViewById(R.id.tv_testsb1title);
        tv_sb2title = view.findViewById(R.id.tv_testSb2title);
        tv_sb3title = view.findViewById(R.id.tv_testSb3title);
        tv_sb4title = view.findViewById(R.id.tv_testSb4title);

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
