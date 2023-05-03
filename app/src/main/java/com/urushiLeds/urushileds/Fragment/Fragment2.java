package com.urushiLeds.urushileds.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.urushiLeds.urushileds.Class.LocalDataManager;
import com.urushi.urushileds.R;

public class Fragment2 extends Fragment {

    private Button btn_fmajor,btn_smajor;
    private ImageButton ib_back;
    LocalDataManager localDataManager;

    public static final String MODEL_FMAJOR = "fmajor";
    public static final String MODEL_SMAJOR = "smajor";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2,container,false);

        init(view);

        localDataManager = new LocalDataManager();
        String model = localDataManager.getSharedPreference(getContext(),"model","manual");
       if (model.equals(MODEL_FMAJOR)){
            btn_fmajor.setText("Seçili");
            btn_fmajor.setBackgroundColor(getResources().getColor(R.color.accent));
        }else if (model.equals(MODEL_SMAJOR)){
            btn_smajor.setText("Seçili");
            btn_smajor.setBackgroundColor(getResources().getColor(R.color.accent));
        }


        btn_smajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                btn_fmajor.setText("Seç");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Seçili");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_SMAJOR);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_SMAJOR);
            }
        });

        btn_fmajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_fmajor.setText("Seçili");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Seç");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_FMAJOR);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_FMAJOR);
            }
        });

        return view;
    }

    public void init(View view){

        btn_fmajor = view.findViewById(R.id.btn_modelFmajor);
        btn_smajor = view.findViewById(R.id.btn_modelSmajor);

        ib_back = view.findViewById(R.id.ib_back);

        ib_click();
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
