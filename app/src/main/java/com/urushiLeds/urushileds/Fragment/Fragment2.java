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
    public final static String DATA_RECEIVE = "data_receive";
    LocalDataManager localDataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2,container,false);

        init(view);

        localDataManager = new LocalDataManager();

        return view;
    }

    public void init(View view){
        mSwitch_fmajor = view.findViewById(R.id.sw_fmajor);
        mSwitch_fMax = view.findViewById(R.id.sw_fmax);
        mSwitch_sMajor = view.findViewById(R.id.sw_smajor);
        mSwitch_sMax = view.findViewById(R.id.sw_smax);
        mswitch_manual = view.findViewById(R.id.sw_manual);
        ib_back = view.findViewById(R.id.ib_back);
        switchSettings();

        ib_click();
    }

    public void switchSettings(){
        mSwitch_sMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    mSwitch_sMax.setText("Açık");
                    localDataManager.setSharedPreference(getContext(),"model","smax");
                    sendDatatoFragment("model","smax");
                }else{
                    mSwitch_sMax.setText("Kapalı");
                }

            }
        });

        mSwitch_sMajor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model","smajor");
                    mSwitch_sMajor.setText("Açık");
                    sendDatatoFragment("model","smajor");
                }else{
                    mSwitch_sMajor.setText("Kapalı");
                }

            }
        });

        mSwitch_fMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model","fmax");
                    mSwitch_fMax.setText("Açık");
                    sendDatatoFragment("model","fmax");
                }else{
                    mSwitch_fMax.setText("Kapalı");
                }

            }
        });

        mSwitch_fmajor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model","fmajor");
                    mSwitch_fmajor.setText("Açık");
                    sendDatatoFragment("model","fmajor");
                }else{
                    mSwitch_fmajor.setText("Kapalı");
                }

            }
        });

        mswitch_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    localDataManager.setSharedPreference(getContext(),"model","manual");
                    mswitch_manual.setText("Açık");
                    sendDatatoFragment("model","manual");
                }else{
                    mswitch_manual.setText("Kapalı");
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
