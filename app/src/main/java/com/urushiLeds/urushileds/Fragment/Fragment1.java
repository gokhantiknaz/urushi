package com.urushiLeds.urushileds.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.urushiLeds.urushileds.Class.DateTime;
import com.urushiLeds.urushileds.Class.LocalDataManager;
import com.urushi.urushileds.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Fragment1 extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener, AdapterView.OnItemSelectedListener{

    private static final String TAG = "Fragment1";

    private SeekBar seekBar1,seekBar2,seekBar3,seekBar4;
    private Spinner sp_channel;
    LineDataSet lDataSet1,lDataSet2,lDataSet3,lDataSet4;
    LineData chartData = new LineData();
    String selectedChannel;
    private TextView tv_seekBar1,tv_seekBar2,tv_seekBar3,tv_seekBar4,tv_sb1title,tv_sb2title,tv_sb3title,tv_sb4title;
    private Button btn_gd,btn_gb,btn_g,btn_a;

    final static int MODEL_DEFAULT = 0;
    final static int MODEL_FMAJOR = 1;
    final static int MODEL_SMAJOR = 2;
    final static int MODEL_FMAX = 3;
    final static int MODEL_SMAX = 4;
    int modelNo = MODEL_DEFAULT;

    int seekbar1,seekbar2,seekbar3,seekbar4;
    int gdh=8,gdm=0,gh=12,gm=0,gbh=16,gbm=0,ah=20,am=0;

    private LineChart mChart;
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> entries2 = new ArrayList<>();
    private ArrayList<Entry> entries3 = new ArrayList<>();
    private ArrayList<Entry> entries4 = new ArrayList<>();

    ArrayList<String > channels = new ArrayList<>();
    final String[] weekdays = {"00:00-08:00", "08:00-16:00", "16:00-23:59"};

    private String model;

    private LocalDataManager localDataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1,container,false);

        init(view);

        chartInit();

        setBundle();

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar1 = progress;
                localDataManager.setSharedPreference(getContext(),model+selectedChannel+"f1",""+progress);
                setSeekBar(1,seekbar1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar2 = progress;
                setSeekBar(2,seekbar2);
                localDataManager.setSharedPreference(getContext(),model+selectedChannel+"f2",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar3 = progress;
                setSeekBar(3,seekbar3);
                localDataManager.setSharedPreference(getContext(),model+selectedChannel+"f3",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar4 = progress;
                setSeekBar(4,progress);
                localDataManager.setSharedPreference(getContext(),model+selectedChannel+"f4",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_gd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("Gün Doğumu");
            }
        });

        btn_g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("Güneş");
            }
        });

        btn_gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("Gün Batımı");
            }
        });

        btn_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("Akşam");
            }
        });

        return view;
    }

    public void init(View view){
        model = "manual";
        channels.add("Channel 1");
        channels.add("Channel 2");
        channels.add("Channel 3");
        channels.add("Channel 4");
        seekBar1 = view.findViewById(R.id.seekBar1);
        seekBar2 = view.findViewById(R.id.seekBar2);
        seekBar3 = view.findViewById(R.id.seekBar3);
        seekBar4 = view.findViewById(R.id.seekBar4);
        mChart = view.findViewById(R.id.linechart);
        sp_channel = view.findViewById(R.id.sp_channels);
        sp_channel.setOnItemSelectedListener(Fragment1.this);
        tv_seekBar1 = view.findViewById(R.id.tv_seekBar1);
        tv_seekBar2 = view.findViewById(R.id.tv_seekBar2);
        tv_seekBar3 = view.findViewById(R.id.tv_seekBar3);
        tv_seekBar4 = view.findViewById(R.id.tv_seekBar4);
        tv_sb1title = view.findViewById(R.id.tv_testsb1title);
        tv_sb2title = view.findViewById(R.id.tv_sb2title);
        tv_sb3title = view.findViewById(R.id.tv_sb3title);
        tv_sb4title = view.findViewById(R.id.tv_sb4title);

        btn_gd = view.findViewById(R.id.btn_gd);
        btn_g = view.findViewById(R.id.btn_g);
        btn_gb = view.findViewById(R.id.btn_gb);
        btn_a = view.findViewById(R.id.btn_a);
        ArrayAdapter adapter = new ArrayAdapter(getContext(),R.layout.spinner_item,channels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp_channel.setAdapter(adapter);
        localDataManager = new LocalDataManager();
    }

    public void chartInit(){

        model = localDataManager.getSharedPreference(getContext(),"model","manual");
        /*
         * Seekbar ların set edilmesi
         * fixme Bu bölümü retrieveMemorizedData kısmında yapabilirsin
         */
        String val = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f1","");
        if (!val.isEmpty()){
            seekBar1.setProgress(Integer.parseInt(val));
        }
        String val2 = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f2","");
        if (!val2.isEmpty()){
            seekBar2.setProgress(Integer.parseInt(val2));
        }
        String val3 = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f3","");
        if (!val3.isEmpty()){
            seekBar3.setProgress(Integer.parseInt(val3));
        }
        String val4 = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f4","");
        if (!val4.isEmpty()){
            seekBar4.setProgress(Integer.parseInt(val4));
        }
        /*
         ************************************************************************
         */

        /*
         * Chart ayarları
         */
        mChart.getXAxis().setDrawGridLines(true);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisRight().setDrawGridLines(true);
        mChart.getLegend().setEnabled(false);
        mChart.getDescription().setEnabled(false);

        YAxis yAxisLeft = mChart.getAxisLeft();
        YAxis yAxisRight = mChart.getAxisRight();
        yAxisLeft.setAxisMaximum(100f); // the axis maximum is 100
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);

        //to hide right Y and top X border
        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(true);
        YAxis leftYAxis = mChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        XAxis topXAxis = mChart.getXAxis();
        topXAxis.setEnabled(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        entries.add(new Entry(0, 0));
        entries2.add(new Entry(1, 0));
        entries3.add(new Entry(2, 0));
        entries4.add(new Entry(3, 0));

        entries.add(new Entry(0, 0));
        entries2.add(new Entry(1, 0));
        entries3.add(new Entry(2, 0));
        entries4.add(new Entry(3, 0));

        entries.add(new Entry(0, 0));
        entries2.add(new Entry(1, 0));
        entries3.add(new Entry(2, 0));
        entries4.add(new Entry(3, 0));

        entries.add(new Entry(0, 0));
        entries2.add(new Entry(1, 0));
        entries3.add(new Entry(2, 0));
        entries4.add(new Entry(3, 0));


        lDataSet1 = new LineDataSet(entries, "Kanal 1");
        lDataSet1.setDrawFilled(false);
        lDataSet1.setDrawValues(false);
        lDataSet1.setDrawCircles(false);
        lDataSet1.setLineWidth(5);
        chartData.addDataSet(lDataSet1);

        lDataSet2 = new LineDataSet(entries2, "Kanal 2");
        lDataSet2.setDrawFilled(false);
        lDataSet2.setDrawValues(false);
        lDataSet2.setDrawCircles(false);
        lDataSet2.setLineWidth(5);
        chartData.addDataSet(lDataSet2);

        lDataSet3 = new LineDataSet(entries3, "Kanal 3");
        lDataSet3.setDrawFilled(false);
        lDataSet3.setDrawValues(false);
        lDataSet3.setDrawCircles(false);
        lDataSet3.setLineWidth(5);
        chartData.addDataSet(lDataSet3);

        lDataSet4 = new LineDataSet(entries4, "Kanal 4");
        lDataSet4.setDrawFilled(false);
        lDataSet4.setDrawValues(false);
        lDataSet4.setLineWidth(5);
        lDataSet4.setDrawCircles(false);
        chartData.addDataSet(lDataSet4);

        //String setter in x-Axis
        mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(weekdays));

        if (model.equals("manual")){
            channels.clear();
            channels.add("Channel 1");
            channels.add("Channel 2");
            channels.add("Channel 3");
            channels.add("Channel 4");
        }else if (model.equals("fmajor")){
            lDataSet1.setLabel("Cool White");
            lDataSet2.setLabel("Wide Spectrum");
            channels.clear();
            channels.add("Cool White");
            channels.add("Wide Spectrum");
            setSpinner();
        }else if (model.equals("smajor")){
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            channels.clear();
            channels.add("Deep Blue");
            channels.add("Aqua Sun");
            setSpinner();
        }else if (model.equals("fmax")){
            lDataSet1.setLabel("Cool White");
            lDataSet2.setLabel("Full Spectrum");
            lDataSet3.setLabel("Reddish White");
            lDataSet4.setLabel("Blueish White");
            channels.clear();
            channels.add("Cool White");
            channels.add("Full Spectrum");
            channels.add("Reddish White");
            channels.add("Blueish White");
            setSpinner();
        }else if (model.equals("smax")){
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            lDataSet3.setLabel("Magenta");
            lDataSet4.setLabel("Sky Blue");
            channels.clear();
            channels.add("Deep Blue");
            channels.add("Aqua Sun");
            channels.add("Magenta");
            channels.add("Sky Blue");
            setSpinner();
        }

        mChart.setData(chartData);
        mChart.animateY(5000);


    }

    /*                                    SPINNER SETTINGS START                                   */
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedChannel = sp_channel.getSelectedItem().toString();

        String gdh = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gdh","07");
        String gdm = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gdm","00");
        String gh = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gh","12");
        String gm = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gm","00");
        String gbh = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gbh","17");
        String gbm = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gbm","00");
        String ah = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"ah","22");
        String am = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"am","00");

        btn_gd.setText("Gün Doğumu "+gdh+":"+gdm);
        btn_g.setText("Güneş "+gh+":"+gm);
        btn_gb.setText("Gün Batımı "+gbh+":"+gbm);
        btn_a.setText("Akşam "+ah+":"+am);

        String val  = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f1","");
        String val2 = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f2","");
        String val3 = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f3","");
        String val4 = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"f4","");

        retrieveMemorizedDatas();

        if (model.equals("fmajor") || model.equals("smajor")){
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            seekBar1.setProgress(0);
            seekBar4.setProgress(0);
        }else if (!val.isEmpty()){
            seekBar1.setProgress(Integer.parseInt(val));
        }else{
            seekBar1.setProgress(0);
        }
        if (!val2.isEmpty()){
            seekBar2.setProgress(Integer.parseInt(val2));
        }else{
            seekBar2.setProgress(0);
        }
        if (!val3.isEmpty()){
            seekBar3.setProgress(Integer.parseInt(val3));
        }else{
            seekBar3.setProgress(0);
        }
        if (!val4.isEmpty()){
            seekBar4.setProgress(Integer.parseInt(val4));
        }else{
            seekBar4.setProgress(0);
        }

        if (model.equals("manual")){
            lDataSet1.setLabel("Kanal 1");
            lDataSet2.setLabel("Kanal 2");
            lDataSet3.setLabel("Kanal 3");
            lDataSet4.setLabel("Kanal 4");

            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);

            if (selectedChannel.equals("Channel 1")){
                setDatasetSettings(1,"Kanal 1");
            }else if (selectedChannel.equals("Channel 2")){
                setDatasetSettings(2,"Kanal 2");
            }else if (selectedChannel.equals("Channel 3")){
                setDatasetSettings(3,"Kanal 3");
            }else if (selectedChannel.equals("Channel 4")){
                setDatasetSettings(4,"Kanal 4");
            }
        }else if (model.equals("fmajor")){
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("Cool White");
            lDataSet2.setLabel("Wide Spectrum");
            if (selectedChannel.equals("Cool White")){
                setDatasetSettings(1,"Cool White");
            }else if (selectedChannel.equals("Wide Spectrum")){
                setDatasetSettings(2,"Wide Spectrum");
            }
        }else if (model.equals("smajor")){
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            if (selectedChannel.equals("Deep Blue")){
                setDatasetSettings(1,"Deep Blue");
            }else if (selectedChannel.equals("Aqua Sun")){
                setDatasetSettings(2,"Aqua Sun");
            }
        }else if (model.equals("fmax")){
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("Cool White");
            lDataSet2.setLabel("Full Spectrum");
            lDataSet3.setLabel("Reddish White");
            lDataSet4.setLabel("Blueish White");
            if (selectedChannel.equals("Cool White")){
                setDatasetSettings(1,"Cool White");
            }else if (selectedChannel.equals("Full Spectrum")){
                setDatasetSettings(2,"Full Spectrum");
            }else if (selectedChannel.equals("Reddish White")){
                setDatasetSettings(3,"Reddish White");
            }else if (selectedChannel.equals("Blueish White")){
                setDatasetSettings(4,"Blueish White");
            }

        }else if (model.equals("smax")){
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            lDataSet3.setLabel("Magenta");
            lDataSet4.setLabel("Sky Blue");
            if (selectedChannel.equals("Deep Blue")){
                setDatasetSettings(1,"Deep Blue");
            }else if (selectedChannel.equals("Aqua Sun")){
                setDatasetSettings(2,"Aqua Sun");
            }else if (selectedChannel.equals("Magenta")){
                setDatasetSettings(3,"Magenta");
            }else if (selectedChannel.equals("Sky Blue")){
                setDatasetSettings(4,"Sky Blue");
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showTimeDialog(String timename){
        // Dialog nesnesi oluştur layout dosyasıne bağlan
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_settime);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_setTime = dialog.findViewById(R.id.btn_timeset);
        NumberPicker np1 = dialog.findViewById(R.id.np1);
        NumberPicker np2 = dialog.findViewById(R.id.npd2);
        TextView tv_timeTitle = dialog.findViewById(R.id.tv_timeTitle);

        float mgdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gdh","7"));
        float mgdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gdm","0"));
        float mgh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gh","12"));
        float mgm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gm","0"));
        float mgbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gbh","17"));
        float mgbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gbm","0"));
        float mah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"ah","22"));
        float mam = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+selectedChannel+"am","0"));

        tv_timeTitle.setText(timename);

        np1.setMaxValue(DateTime.hournp1.length-1);
        np1.setMinValue(0);
        np1.setDisplayedValues(DateTime.hournp1);
        np1.setWrapSelectorWheel(true);

        np2.setMaxValue(DateTime.minutenp2.length-1);
        np2.setMinValue(0);
        np2.setDisplayedValues(DateTime.minutenp2);
        np2.setWrapSelectorWheel(true);

        btn_setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String alarm1_hour = DateTime.hournp1[np1.getValue()];
                String alarm1_minute = DateTime.minutenp2[np2.getValue()];

                if (timename.equals("Gün Doğumu")){
                    gdh = Integer.parseInt(alarm1_hour);
                    gdm = Integer.parseInt(alarm1_minute);
                    if (gdh < mgh && gdh < mgbh && gdh < mah){
                        btn_gd.setText("Gün Doğumu "+alarm1_hour+":" + alarm1_minute);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gdh",alarm1_hour);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gdm",alarm1_minute);
                    }else {
                        Toast.makeText(getContext(),"Yanlış değer girdiniz gün doğumu vakti diğer vakitlerden küçük olmalıdır!",Toast.LENGTH_LONG).show();
                    }

                }else if (timename.equals("Güneş")){
                    gh = Integer.parseInt(alarm1_hour);
                    gm = Integer.parseInt(alarm1_minute);
                    Log.d(TAG, "güneş saat:"+gh+" güneş dakika : "+gm);
                    if (gh > mgdh && gh < mgbh && gh < mah){
                        btn_g.setText("Güneş "+alarm1_hour+":"+alarm1_minute);
                        Log.d(TAG, "Güneş "+alarm1_hour+":"+alarm1_minute);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gh",alarm1_hour);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gm",alarm1_minute);
                    }else {
                        Toast.makeText(getContext(),"Yanlış değer girdiniz güneş vakti gün doğumundan büyük diğer vakitlerden küçük olmalıdır!",Toast.LENGTH_LONG).show();
                    }

                }else if (timename.equals("Gün Batımı")){
                    gbh = Integer.parseInt(alarm1_hour);
                    gbm = Integer.parseInt(alarm1_minute);
                    if (gbh > mgdh && gbh > mgh && gbh < mah){
                        btn_gb.setText("Gün Batımı "+alarm1_hour+":"+ alarm1_minute);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gbh",alarm1_hour);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gbm",alarm1_minute);
                    }else {
                        Toast.makeText(getContext(),"Yanlış değer girdiniz gün batımı vakti gün doğumundan, güneşten büyük akşam vaktinden küçük olmalıdır!",Toast.LENGTH_LONG).show();
                    }
                }else if (timename.equals("Akşam")){
                    ah = Integer.parseInt(alarm1_hour);
                    am = Integer.parseInt(alarm1_minute);
                    if (ah > mgdh && ah > mgh && ah > mgbh){
                        btn_a.setText("Akşam "+alarm1_hour+":" + alarm1_minute);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"ah",alarm1_hour);
                        localDataManager.setSharedPreference(getContext(),model+selectedChannel+"am",alarm1_minute);
                    }else {
                        Toast.makeText(getContext(),"Yanlış değer girdiniz akşam vakti diğer vakitlerden büyük olmalıdır!",Toast.LENGTH_LONG).show();
                    }
                }
                retrieveMemorizedDatas();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            model = getArguments().getString("model", "");
            modelNo = MODEL_DEFAULT;

            if (model.equals("fmajor")){
                lDataSet1.setLabel("Cool White");
                lDataSet2.setLabel("Wide Spectrum");
                lDataSet3.setLabel("");
                lDataSet4.setLabel("");
                seekBar1.setProgress(0);
                seekBar4.setProgress(0);
                channels.clear();
                channels.add("Cool White");
                channels.add("Wide Spectrum");
                setSpinner();
                modelNo = MODEL_FMAJOR;
                tv_sb1title.setText("Gün Doğumu");
                tv_sb2title.setText("Güneş");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            }else if (model.equals("smajor")){
                lDataSet1.setLabel("Deep Blue");
                lDataSet2.setLabel("Aqua Sun");
                lDataSet3.setLabel("");
                lDataSet4.setLabel("");
                channels.clear();
                channels.add("Deep Blue");
                channels.add("Aqua Sun");
                setSpinner();

                modelNo = MODEL_SMAJOR;
                tv_sb1title.setText("Gün Doğumu");
                tv_sb2title.setText("Güneş");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            }else if (model.equals("fmax")){
                lDataSet1.setLabel("Cool White");
                lDataSet2.setLabel("Full Spectrum");
                lDataSet3.setLabel("Reddish White");
                lDataSet4.setLabel("Blueish White");
                channels.clear();
                channels.add("Cool White");
                channels.add("Full Spectrum");
                channels.add("Reddish White");
                channels.add("Blueish White");
                setSpinner();

                modelNo = MODEL_FMAX;
                tv_sb1title.setText("Gün Doğumu");
                tv_sb2title.setText("Güneş");
                tv_sb3title.setText("Gün Batımı");
                tv_sb4title.setText("Akşam");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            }else if (model.equals("smax")){
                lDataSet1.setLabel("Deep Blue");
                lDataSet2.setLabel("Aqua Sun");
                lDataSet3.setLabel("Magenta");
                lDataSet4.setLabel("Sky Blue");

                channels.clear();
                channels.add("Deep Blue");
                channels.add("Aqua Sun");
                channels.add("Magenta");
                channels.add("Sky Blue");
                setSpinner();

                modelNo = MODEL_SMAX;
                tv_sb1title.setText("Gün Doğumu");
                tv_sb2title.setText("Güneş");
                tv_sb3title.setText("Gün Batımı");
                tv_sb4title.setText("Akşam");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setDataset(LineDataSet dataset1,int lineWidth,int color,String label){
        dataset1.setColor(color);
        dataset1.setLineWidth(lineWidth);
        dataset1.setLabel(label);
    }

    public void setDatasetSettings(int dataset,String label){
        switch (dataset){
            case 1:
                lDataSet1.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(true);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(false);

                setDataset(lDataSet1,5,R.color.purple_500,label);
                setDataset(lDataSet2,2,R.color.lighgray,label);
                setDataset(lDataSet3,2,R.color.lighgray,label);
                setDataset(lDataSet4,2,R.color.lighgray,label);
                break;
            case 2:
                lDataSet2.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(true);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(false);

                setDataset(lDataSet1,2,R.color.lighgray,label);
                setDataset(lDataSet2,5,R.color.purple_500,label);
                setDataset(lDataSet3,2,R.color.lighgray,label);
                setDataset(lDataSet4,2,R.color.lighgray,label);
                break;
            case 3:
                lDataSet3.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(true);
                lDataSet4.setDrawFilled(false);

                setDataset(lDataSet1,2,R.color.lighgray,label);
                setDataset(lDataSet2,2,R.color.lighgray,label);
                setDataset(lDataSet3,5,R.color.purple_500,label);
                setDataset(lDataSet4,2,R.color.lighgray,label);
                break;
            case 4:
                lDataSet4.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(true);

                setDataset(lDataSet1,2,R.color.lighgray,label);
                setDataset(lDataSet2,2,R.color.lighgray,label);
                setDataset(lDataSet3,2,R.color.lighgray,label);
                setDataset(lDataSet4,5,R.color.purple_500,label);
                break;
            default:
                break;
        }
        mChart.invalidate();
    }

    public void setSeekBar(int seekBarNo,int progress){
        switch (seekBarNo) {
            case 1:
                tv_seekBar1.setText("%"+progress);
                break;
            case 2:
                tv_seekBar2.setText("%"+progress);
                break;
            case 3:
                tv_seekBar3.setText("%"+progress);
                break;
            case 4:
                tv_seekBar4.setText("%"+progress);
                break;
            default:
                break;
        }
        if (model.equals("manual")){
            if (selectedChannel.equals("Channel 1")){
                setSwitch(entries,seekBarNo,progress,lDataSet1,"Kanal 1");
            }else if (selectedChannel.equals("Channel 2")){
                setSwitch(entries2,seekBarNo,progress,lDataSet2,"Kanal 2");
            }else if (selectedChannel.equals("Channel 3")){
                setSwitch(entries3,seekBarNo,progress,lDataSet3,"Kanal 3");
            }else if (selectedChannel.equals("Channel 4")){
                setSwitch(entries4,seekBarNo,progress,lDataSet4,"Kanal 4");
            }
        }else if (model.equals("fmajor")){
            if (selectedChannel.equals("Cool White")){
                setSwitch(entries,seekBarNo,progress,lDataSet1,"Cool White");
            }else if (selectedChannel.equals("Wide Spectrum")){
                setSwitch(entries2,seekBarNo,progress,lDataSet2,"Wide Spectrum");
            }
        }else if (model.equals("smajor")){
            if (selectedChannel.equals("Deep Blue")){
                setSwitch(entries,seekBarNo,progress,lDataSet1,"Deep Blue");
            }else if (selectedChannel.equals("Aqua Sun")){
                setSwitch(entries2,seekBarNo,progress,lDataSet2,"Aqua Sun");
            }

        }else if (model.equals("fmax")){
            if (selectedChannel.equals("Cool White")){
                setSwitch(entries,seekBarNo,progress,lDataSet1,"Cool White");
            }else if (selectedChannel.equals("Full Spectrum")){
                setSwitch(entries2,seekBarNo,progress,lDataSet2,"Full Spectrum");
            }else if (selectedChannel.equals("Reddish White")){
                setSwitch(entries3,seekBarNo,progress,lDataSet3,"Reddish White");
            }else if (selectedChannel.equals("Blueish White")){
                setSwitch(entries4,seekBarNo,progress,lDataSet4,"Blueish White");
            }

        }else if (model.equals("smax")){
            if (selectedChannel.equals("Deep Blue")){
                setSwitch(entries,seekBarNo,progress,lDataSet1,"Deep Blue");
            }else if (selectedChannel.equals("Aqua Sun")){
                setSwitch(entries2,seekBarNo,progress,lDataSet2,"Aqua Sun");
            }else if (selectedChannel.equals("Magenta")){
                setSwitch(entries3,seekBarNo,progress,lDataSet3,"Magenta");
            }else if (selectedChannel.equals("Sky Blue")){
                setSwitch(entries4,seekBarNo,progress,lDataSet4,"Sky Blue");
            }

        }
        chartData.clearValues();
        chartData.addDataSet(lDataSet1);
        chartData.addDataSet(lDataSet2);
        chartData.addDataSet(lDataSet3);
        chartData.addDataSet(lDataSet4);

        mChart.setData(chartData);
        mChart.invalidate();

    }

    public void setSwitch(ArrayList entry,int seekBarNo,int progress,LineDataSet lDataSet,String label){
        switch (modelNo){
            case MODEL_DEFAULT:
                try {
                    //entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                    retrieveMemorizedDatas();
                }catch (Exception e){

                }

                break;
            case MODEL_FMAJOR:
               try {
                   //entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                   lDataSet.setLabel(label);
                   retrieveMemorizedDatas();
               }catch (Exception e){
                   Log.e(TAG,e.getLocalizedMessage());
               }
                break;
            case MODEL_FMAX:
                try {
                    //entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                    retrieveMemorizedDatas();
                }catch (Exception e){


                }
                break;
            case MODEL_SMAJOR:
                try {
                    //entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                    retrieveMemorizedDatas();
                }catch (Exception e){

                }
                break;
            case MODEL_SMAX:
                try {
                    //entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                    retrieveMemorizedDatas();
                }catch (Exception e){

                }
                break;
            default:
                break;
        }
    }

    public void setSpinner(){
        ArrayAdapter adapter = new ArrayAdapter(getContext(),R.layout.spinner_item,channels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp_channel.setAdapter(adapter);
    }

    public void retrieveMemorizedDatas(){
        entries.clear();
        entries2.clear();
        entries3.clear();
        entries4.clear();
        String model = localDataManager.getSharedPreference(getContext(),"model","manual");
        if (model.equals("manual")){
            String mChannel = "Channel 1";
            String c1f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c1f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c1f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");

            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));
            mChannel = "Channel 2";
            String c2f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c2f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c2f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");

            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float  c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float  c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float  c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float  c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));
            mChannel = "Channel 3";
            String c3f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c3f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c3f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c3f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");

            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float  c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float  c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float  c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float  c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));
            mChannel = "Channel 4";
            String c4f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c4f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c4f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c4f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float  c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float  c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float  c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float  c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));

            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4), c1gdh/8f+c1gdm/1000f,c1gh/8f+c1gm/1000f,c1gbh/8f+c1gbm/1000f,c1ah/8f+c1am/1000f,lDataSet1,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8f+c2gdm/1000f,c2gh/8f+c2gm/1000f,c2gbh/8f+c2gbm/1000f,c2ah/8f+c2am/1000f,lDataSet2,2,"Kanal 2",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8f+c3gdm/1000f,c3gh/8f+c3gm/1000f,c3gbh/8f+c3gbm/1000f,c3ah/8f+c3am/1000f,lDataSet3,2,"Kanal 3",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8f+c4gdm/1000f,c4gh/8f+c4gm/1000f,c4gbh/8f+c4gbm/1000f,c4ah/8f+c4am/1000f,lDataSet4,2,"Kanal 4",R.color.lighgray);

        }else if (model.equals("fmajor")){
            String mChannel = "Cool White";
            String c1f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"ah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"am","00"));
            mChannel = "Wide Spectrum";
            String c2f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"ah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"am","00"));

            refreshChart(entries,0,Integer.parseInt(c1f1),Integer.parseInt(c1f2),0,c1gdh/8f + c1gdm/1000f,c1gh/8f + c1gm/1000f,c1gbh/8f + c1gbm/1000f,c1ah/8f + c1am/1000f,lDataSet1,2,"Cool White",R.color.lighgray);
            refreshChart(entries2,0,Integer.parseInt(c2f1),Integer.parseInt(c2f2),0,c2gdh/8f + c2gdm/1000f,c2gh/8f + c2gm/1000f,c2gbh/8f + c2gbm/1000f,c2ah/8f + c2am/1000f,lDataSet1,2,"Wide Spectrum",R.color.lighgray);


        }else if (model.equals("smajor")){
            String mChannel = "Deep Blue";
            String c1f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"ah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"am","0"));
            mChannel = "Aqua Sun";
            String c2f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","0"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"ah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"am","0"));

            refreshChart(entries,0,Integer.parseInt(c1f1),Integer.parseInt(c1f2),0,c1gdh/8f + c1gdm/1000f,c1gh/8f + c1gm/1000f,c1gbh/8f + c1gbm/1000f,c1ah/8f + c1am/1000f,lDataSet1,2,"Deep Blue",R.color.lighgray);
            refreshChart(entries2,0,Integer.parseInt(c2f1),Integer.parseInt(c2f2),0,c2gdh/8f + c2gdm/1000f,c2gh/8f + c2gm/1000f,c2gbh/8f + c2gbm/1000f,c2ah/8f + c2am/1000f,lDataSet1,2,"Aqua Sun",R.color.lighgray);

        }else if (model.equals("fmax")){
            String mChannel = "Cool White";
            String c1f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c1f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c1f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","0"));
            mChannel = "Full Spectrum";
            String c2f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c2f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c2f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","0"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","0"));
            mChannel = "Reddish White";
            String c3f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c3f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c3f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c3f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","0"));
            float c3gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c3gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","0"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","0"));
            mChannel = "Blueish White";
            String c4f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c4f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c4f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c4f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","0"));
            float c4gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c4gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","0"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","0"));

            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4),c1gdh/8f + c1gdm/1000f,c1gh/8f + c1gm/1000f,c1gbh/8f + c1gbm/1000f,c1ah/8f + c1am/1000f,lDataSet1,2,"Cool White",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8f + c2gdm/1000f,c2gh/8f + c2gm/1000f,c2gbh/8f + c2gbm/1000f,c2ah/8f + c2am/1000f,lDataSet2,2,"Full Spectrum",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8f + c3gdm/1000f,c3gh/8f + c3gm/1000f,c3gbh/8f + c3gbm/1000f,c3ah/8f + c3am/1000f,lDataSet3,2,"Reddish White",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8f + c4gdm/1000f,c4gh/8f + c4gm/1000f,c4gbh/8f + c4gbm/1000f,c4ah/8f + c4am/1000f,lDataSet4,2,"Blueish White",R.color.lighgray);
        }else if (model.equals("smax")){
            String mChannel = "Deep Blue";
            String c1f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c1f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c1f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));
            mChannel = "Aqua Sun";
            String c2f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c2f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c2f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));
            mChannel = "Magenta";
            String c3f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c3f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c3f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c3f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c3gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c3gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));
            mChannel = "Sky Blue";
            String c4f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c4f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c4f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c4f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float c4gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float c4gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"ah","22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model+mChannel+"am","00"));

            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4),c1gdh/8f + c1gdm/1000f,c1gh/8f + c1gm/1000f,c1gbh/8f  + c1gbm/1000f,c1ah/8f+ c1am/1000f,lDataSet1,2,"Deep Blue",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8f + c2gdm/1000f,c2gh/8f + c2gm/1000f,c2gbh/8f + c2gbm/1000f,c2ah/8f + c2am/1000f,lDataSet2,2,"Aqua Sun",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8f + c3gdm/1000f,c3gh/8f + c3gm/1000f,c3gbh/8f + c3gbm/1000f,c3ah/8f + c3am/1000f,lDataSet3,2,"Magenta",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8f + c4gdm/1000f,c4gh/8f + c4gm/1000f,c4gbh/8f + c4gbm/1000f,c4ah/8f + c4am/1000f,lDataSet4,2,"Sky Blue",R.color.lighgray);
        }
    }

    public void refreshChart(ArrayList entry,int pivot1,int pivot2,int pivot3,int pivot4,float time1,float time2,float time3,float time4,LineDataSet lineDataSet,int width,String channel,int color){

        entry.add(new Entry(time1, pivot1));
        entry.add(new Entry(time2, pivot2));
        entry.add(new Entry(time3, pivot3));
        entry.add(new Entry(time4, pivot4));

        //lineDataSet = (LineDataSet) chartData.getDataSetByLabel(channel,false);
        lineDataSet = new LineDataSet(entry, channel);
        lineDataSet.setLineWidth(width);
        chartData.addDataSet(lineDataSet);
        lineDataSet.setColor(color);

       mChart.setData(chartData);
       mChart.invalidate();

    }









    /////////////////////////////////////// MP-ANDROID CHART IMPLEMENTATIONS ///////////////////////////////////////////////////
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
