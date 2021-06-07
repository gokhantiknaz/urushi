package com.urushiLeds.urushileds.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.urushiLeds.urushileds.Class.LocalDataManager;
import com.urushi.urushileds.R;

public class Fragment2 extends Fragment {

    private Switch mSwitch_fmajor,mSwitch_sMajor,mSwitch_sMax,mSwitch_fMax,mswitch_manual;
    private ImageButton ib_back;
    LocalDataManager localDataManager;

    public static final String MODEL_FMAJOR = "fmajor";
    public static final String MODEL_SMAJOR = "smajor";
    public static final String MODEL_FMAX = "fmax";
    public static final String MODEL_SMAX = "smax";
    public static final String MODEL_MANUAL = "manual";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2,container,false);

        init(view);

        localDataManager = new LocalDataManager();
        String model = localDataManager.getSharedPreference(getContext(),"model","manual");
        if (model.equals(MODEL_MANUAL)){
            mswitch_manual.setChecked(true);
            mSwitch_fmajor.setChecked(false);
            mSwitch_sMajor.setChecked(false);
            mSwitch_fMax.setChecked(false);
            mSwitch_sMax.setChecked(false);
        }else if (model.equals(MODEL_FMAJOR)){
            mswitch_manual.setChecked(false);
            mSwitch_fmajor.setChecked(true);
            mSwitch_sMajor.setChecked(false);
            mSwitch_fMax.setChecked(false);
            mSwitch_sMax.setChecked(false);
        }else if (model.equals(MODEL_SMAJOR)){
            mswitch_manual.setChecked(false);
            mSwitch_fmajor.setChecked(false);
            mSwitch_sMajor.setChecked(true);
            mSwitch_fMax.setChecked(false);
            mSwitch_sMax.setChecked(false);
        }else if (model.equals(MODEL_FMAX)){
            mswitch_manual.setChecked(false);
            mSwitch_fmajor.setChecked(false);
            mSwitch_sMajor.setChecked(false);
            mSwitch_fMax.setChecked(true);
            mSwitch_sMax.setChecked(false);
        }else if (model.equals(MODEL_SMAX)){
            mswitch_manual.setChecked(false);
            mSwitch_fmajor.setChecked(false);
            mSwitch_sMajor.setChecked(false);
            mSwitch_fMax.setChecked(false);
            mSwitch_sMax.setChecked(true);
        }

        switchSettings();

        return view;
    }

    public void init(View view){
        mSwitch_fmajor = view.findViewById(R.id.sw_fmajor);
        mSwitch_fMax = view.findViewById(R.id.sw_fmax);
        mSwitch_sMajor = view.findViewById(R.id.sw_smajor);
        mSwitch_sMax = view.findViewById(R.id.sw_smax);
        mswitch_manual = view.findViewById(R.id.sw_manual);
        ib_back = view.findViewById(R.id.ib_back);

        ib_click();
    }

    public void switchSettings(){
        mSwitch_sMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    mSwitch_sMax.setText("AKTİF");
                    localDataManager.setSharedPreference(getContext(),"model",MODEL_SMAX);
                    localDataManager.setSharedPreference(getContext(),"test_model","false");
                    sendDatatoFragment("model",MODEL_SMAX);
                }else{
                    mSwitch_sMax.setText("PASİF");
                }

            }
        });

        mSwitch_sMajor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model",MODEL_SMAJOR);
                    localDataManager.setSharedPreference(getContext(),"test_model","false");
                    mSwitch_sMajor.setText("AKTİF");
                    sendDatatoFragment("model",MODEL_SMAJOR);
                }else{
                    mSwitch_sMajor.setText("PASİF");
                }

            }
        });

        mSwitch_fMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model",MODEL_FMAX);
                    localDataManager.setSharedPreference(getContext(),"test_model","false");
                    mSwitch_fMax.setText("AKTİF");
                    sendDatatoFragment("model",MODEL_FMAX);
                }else{
                    mSwitch_fMax.setText("PASİF");
                }

            }
        });

        mSwitch_fmajor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model",MODEL_FMAJOR);
                    localDataManager.setSharedPreference(getContext(),"test_model","false");
                    mSwitch_fmajor.setText("AKTİF");
                    sendDatatoFragment("model",MODEL_FMAJOR);
                }else{
                    mSwitch_fmajor.setText("PASİF");
                }

            }
        });

        mswitch_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model",MODEL_MANUAL);
                    localDataManager.setSharedPreference(getContext(),"test_model","false");
                    mswitch_manual.setText("AKTİF");
                    sendDatatoFragment("model",MODEL_MANUAL);
                }else{
                    mswitch_manual.setText("PASİF");
                }
            }
        });
    }

    public void sendDatatoFragment(String key, String message){
        Bundle bundle = new Bundle();
        bundle.putString(key,message);
        Fragment1 fragment1 = new Fragment1();
        fragment1.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frame,fragment1).commit();
    }

    public void ib_click(){
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Fragment1 fragment1 = new Fragment1();
                fragment1.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frame,fragment1).commit();
            }
        });
    }
}
