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
        tv_sb1title = view.findViewById(R.id.tv_sb1title);
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

        mChart.getXAxis().setDrawGridLines(true);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisRight().setDrawGridLines(true);

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
        lDataSet1.setLineWidth(5);
        chartData.addDataSet(lDataSet1);

        lDataSet2 = new LineDataSet(entries2, "Kanal 2");
        lDataSet2.setDrawFilled(false);
        lDataSet2.setLineWidth(5);
        chartData.addDataSet(lDataSet2);
        lDataSet2.setColor(R.color.design_default_color_on_secondary);

        lDataSet3 = new LineDataSet(entries3, "Kanal 3");
        lDataSet3.setDrawFilled(false);
        lDataSet3.setLineWidth(5);
        chartData.addDataSet(lDataSet3);
        lDataSet3.setColor(R.color.purple_500);

        lDataSet4 = new LineDataSet(entries4, "Kanal 4");
        lDataSet4.setDrawFilled(false);
        lDataSet4.setLineWidth(5);
        chartData.addDataSet(lDataSet4);
        lDataSet4.setColor(R.color.teal_200);

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

        String gdh = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gdh","7");
        String gdm = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gdm","0");
        String gh = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gh","12");
        String gm = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gm","0");
        String gbh = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gbh","17");
        String gbm = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"gbm","0");
        String ah = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"ah","22");
        String am = localDataManager.getSharedPreference(getContext(),model+selectedChannel+"am","0");

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
                    btn_gd.setText("Gün Doğumu "+alarm1_hour+" : " + alarm1_minute);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gdh",alarm1_hour);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gdm",alarm1_minute);
                }else if (timename.equals("Güneş")){
                    gh = Integer.parseInt(alarm1_hour);
                    gm = Integer.parseInt(alarm1_minute);
                    btn_g.setText("Güneş "+alarm1_hour+" : " + alarm1_minute);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gh",alarm1_hour);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gm",alarm1_minute);
                }else if (timename.equals("Gün Batımı")){
                    gbh = Integer.parseInt(alarm1_hour);
                    gbm = Integer.parseInt(alarm1_minute);
                    btn_gb.setText("Gün Batımı "+alarm1_hour+" : " + alarm1_minute);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gbh",alarm1_hour);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"gbm",alarm1_minute);
                }else if (timename.equals("Akşam")){
                    ah = Integer.parseInt(alarm1_hour);
                    am = Integer.parseInt(alarm1_minute);
                    btn_a.setText("Akşam "+alarm1_hour+" : " + alarm1_minute);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"ah",alarm1_hour);
                    localDataManager.setSharedPreference(getContext(),model+selectedChannel+"am",alarm1_minute);
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
                tv_sb1title.setText("Güneş");
                tv_sb2title.setText("Gün Batımı");
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
                tv_sb1title.setText("Güneş");
                tv_sb2title.setText("Gün Batımı");
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
                tv_sb2title.setText("Günüş");
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
                setDataset(lDataSet1,5,R.color.purple_500,label);
                setDataset(lDataSet2,2,R.color.lighgray,label);
                setDataset(lDataSet3,2,R.color.lighgray,label);
                setDataset(lDataSet4,2,R.color.lighgray,label);
                break;
            case 2:
                setDataset(lDataSet1,2,R.color.lighgray,label);
                setDataset(lDataSet2,5,R.color.purple_500,label);
                setDataset(lDataSet3,2,R.color.lighgray,label);
                setDataset(lDataSet4,2,R.color.lighgray,label);
                break;
            case 3:
                setDataset(lDataSet1,2,R.color.lighgray,label);
                setDataset(lDataSet2,2,R.color.lighgray,label);
                setDataset(lDataSet3,5,R.color.purple_500,label);
                setDataset(lDataSet4,2,R.color.lighgray,label);
                break;
            case 4:
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
                    entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                }catch (Exception e){

                }

                break;
            case MODEL_FMAJOR:
               try {
                   entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                   lDataSet.setLabel(label);
               }catch (Exception e){
                   Log.e(TAG,e.getLocalizedMessage());
               }
                break;
            case MODEL_FMAX:
                try {
                    entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                }catch (Exception e){


                }
                break;
            case MODEL_SMAJOR:
                try {
                    entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                }catch (Exception e){

                }
                break;
            case MODEL_SMAX:
                try {
                    entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
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
            String c1f1 = localDataManager.getSharedPreference(getContext(),"manualChannel 1f1","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),"manualChannel 1f2","0");
            String c1f3 = localDataManager.getSharedPreference(getContext(),"manualChannel 1f3","0");
            String c1f4 = localDataManager.getSharedPreference(getContext(),"manualChannel 1f4","0");

            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1gdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1gdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1gh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1gm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1gbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1gbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1ah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 1am","0"));

            String c2f1 = localDataManager.getSharedPreference(getContext(),"manualChannel 2f1","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),"manualChannel 2f2","0");
            String c2f3 = localDataManager.getSharedPreference(getContext(),"manualChannel 2f3","0");
            String c2f4 = localDataManager.getSharedPreference(getContext(),"manualChannel 2f4","0");

            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2gdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2gdm","0"));
            float  c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2gh","12"));
            float  c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2gm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2gbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2gbm","0"));
            float  c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2ah","22"));
            float  c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 2am","0"));

            String c3f1 = localDataManager.getSharedPreference(getContext(),"manualChannel 3f1","0");
            String c3f2 = localDataManager.getSharedPreference(getContext(),"manualChannel 3f2","0");
            String c3f3 = localDataManager.getSharedPreference(getContext(),"manualChannel 3f3","0");
            String c3f4 = localDataManager.getSharedPreference(getContext(),"manualChannel 3f4","0");

            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3gdh","7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3gdm","0"));
            float  c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3gh","12"));
            float  c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3gm","0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3gbh","17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3gbm","0"));
            float  c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3ah","22"));
            float  c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 3am","0"));

            String c4f1 = localDataManager.getSharedPreference(getContext(),"manualChannel 4f1","0");
            String c4f2 = localDataManager.getSharedPreference(getContext(),"manualChannel 4f2","0");
            String c4f3 = localDataManager.getSharedPreference(getContext(),"manualChannel 4f3","0");
            String c4f4 = localDataManager.getSharedPreference(getContext(),"manualChannel 4f4","0");

            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4gdh","7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4gdm","0"));
            float  c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4gh","12"));
            float  c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4gm","0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4gbh","17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4gbm","0"));
            float  c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4ah","22"));
            float  c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"manualChannel 4am","0"));

            Float c1p1 = (c1gdh/8f) + (c1gdm/100f);
            Float c1p2 = (c1gh/8f) + (c1gm/100f);
            Float c1p3 = (c1gbh/8f) + (c1gbm/100f);
            Float c1p4 = (c1ah/8f) + (c1am/100f);

            Float c2p1 = (c2gdh/8f) + (c1gdm/100f);
            Float c2p2 = (c2gh/8f) + (c1gm/100f);
            Float c2p3 = (c2gbh/8f) + (c1gbm/100f);
            Float c2p4 = (c2ah/8f) + (c1am/100f);

            Float c3p1 = (c3gdh/8f) + (c1gdm/100f);
            Float c3p2 = (c3gh/8f) + (c1gm/100f);
            Float c3p3 = (c3gbh/8f) + (c1gbm/100f);
            Float c3p4 = (c3ah/8f) + (c1am/100f);

            Float c4p1 = (c4gdh/8f) + (c1gdm/100f);
            Float c4p2 = (c4gh/8f) + (c1gm/100f);
            Float c4p3 = (c4gbh/8f) + (c1gbm/100f);
            Float c4p4 = (c4ah/8f) + (c1am/100f);

            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4),c1gdh/8+c1gdm/100,c1gh/8+c1gm/100,c1gbh/8+c1gbm/100,c1ah/8+c1am/100,lDataSet1,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8+c2gdm/100,c2gh/8+c2gm/100,c2gbh/8+c2gbm/100,c2ah/8+c2am/100,lDataSet2,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8+c3gdm/100,c3gh/8+c3gm/100,c3gbh/8+c3gbm/100,c3ah/8+c3am/100,lDataSet3,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8+c4gdm/100,c4gh/8+c4gm/100,c4gbh/8+c4gbm/100,c4ah/8+c4am/100,lDataSet4,2,"Kanal 1",R.color.lighgray);

        }else if (model.equals("fmajor")){

            String c1f1 = localDataManager.getSharedPreference(getContext(),"fmajorCool Whitef2","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),"fmajorCool Whitef3","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whitegdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whitegdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whitegh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whitegm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whitegbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whitegbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whiteah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorCool Whiteam","0"));

            String c2f1 = localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumf2","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumf3","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumgdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumgdm","0"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumgh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumgm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumgbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumgbm","0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmajorWide Spectrumam","0"));

            refreshChart(entries,0,Integer.parseInt(c1f1),Integer.parseInt(c1f2),0,c1gdh/8 + c1gdm/100,c1gh/8 + c1gm/100,c1gbh/8 + c1gbm/100,c1ah/8 + c1am/100,lDataSet1,2,"Cool White",R.color.lighgray);
            refreshChart(entries2,0,Integer.parseInt(c2f1),Integer.parseInt(c2f2),0,c2gdh/8 + c2gdm/100,c2gh/8 + c2gm/100,c2gbh/8 + c2gbm/100,c2gbh/8 + c2gbm/100,lDataSet1,2,"Wide Spectrum",R.color.lighgray);


        }else if (model.equals("smajor")){

            String c1f1 = localDataManager.getSharedPreference(getContext(),"smajorDeep Bluef2","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),"smajorDeep Bluef3","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Bluegdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Bluegdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Bluegh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Bluegm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Bluegbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Bluegbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Blueah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorDeep Blueam","0"));

            String c2f1 = localDataManager.getSharedPreference(getContext(),"smajorAqua Sunf2","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),"smajorAqua Sunf3","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sungdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sungdm","0"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sungh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sungm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sungbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sungbm","0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sunah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smajorAqua Sunam","0"));

            refreshChart(entries,0,Integer.parseInt(c1f1),Integer.parseInt(c1f2),0,c1gdh/8 + c1gdm/100,c1gh/8 + c1gm/100,c1gbh/8 + c1gbm/100,c1ah/8 + c1am/100,lDataSet1,2,"Deep Blue",R.color.lighgray);
            refreshChart(entries2,0,Integer.parseInt(c2f1),Integer.parseInt(c2f2),0,c2gdh/8 + c2gdm/100,c2gh/8 + c2gm/100,c2gbh/8 + c2gbm/100,c2ah/8 + c2am/100,lDataSet1,2,"Aqua Sun",R.color.lighgray);

        }else if (model.equals("fmax")){
            String mChannel = "Cool White";
            String c1f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c1f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c1f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whitegdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whitegdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whitegh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whitegm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whitegbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whitegbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whiteah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxCool Whiteam","0"));
            mChannel = "Full Spectrum";
            String c2f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c2f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c2f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumgdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumgdm","0"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumgh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumgm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumgbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumgbm","0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxFull Spectrumam","0"));
            mChannel = "Reddish White";
            String c3f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c3f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c3f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c3f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whitegdh","7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whitegdm","0"));
            float c3gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whitegh","12"));
            float c3gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whitegm","0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whitegbh","17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whitegbm","0"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whiteah","22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxReddish Whiteam","0"));
            mChannel = "Blueish White";
            String c4f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c4f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c4f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c4f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whitegdh","7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whitegdm","0"));
            float c4gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whitegh","12"));
            float c4gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whitegm","0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whitegbh","17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whitegbm","0"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whiteah","22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"fmaxBlueish Whiteam","0"));

            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4),c1gdh/8 + c1gdm/100,c1gh/8 + c1gm/100,c1gbh/8 + c1gbm/100,c1ah/8 + c1am/100,lDataSet1,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8 + c2gdm/100,c2gh/8 + c2gm/100,c2gbh/8 + c2gbm/100,c2ah/8 + c2am/100,lDataSet2,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8 + c3gdm/100,c3gh/8 + c3gm/100,c3gbh/8 + c3gbm/100,c3ah/8 + c3am/100,lDataSet3,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8 + c4gdm,c4gh/8 + c4gm,c4gbh/8 + c4gbm,c4ah/8 + c4am/100,lDataSet4,2,"Kanal 1",R.color.lighgray);
        }else if (model.equals("smax")){
            String c1f1 = localDataManager.getSharedPreference(getContext(),"smaxDeep Bluef1","0");
            String c1f2 = localDataManager.getSharedPreference(getContext(),"smaxDeep Bluef2","0");
            String c1f3 = localDataManager.getSharedPreference(getContext(),"smaxDeep Bluef3","0");
            String c1f4 = localDataManager.getSharedPreference(getContext(),"smaxDeep Bluef4","0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Bluegdh","7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Bluegdm","0"));
            float c1gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Bluegh","12"));
            float c1gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Bluegm","0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Bluegbh","17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Bluegbm","0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Blueah","22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxDeep Blueam","0"));

            String c2f1 = localDataManager.getSharedPreference(getContext(),"smaxAqua Sunf1","0");
            String c2f2 = localDataManager.getSharedPreference(getContext(),"smaxAqua Sunf2","0");
            String c2f3 = localDataManager.getSharedPreference(getContext(),"smaxAqua Sunf3","0");
            String c2f4 = localDataManager.getSharedPreference(getContext(),"smaxAqua Sunf4","0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sungdh","7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sungdm","0"));
            float c2gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sungh","12"));
            float c2gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sungm","0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sungbh","17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sungbm","0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sunah","22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxAqua Sunam","0"));

            String c3f1 = localDataManager.getSharedPreference(getContext(),"smaxMagentaf1","0");
            String c3f2 = localDataManager.getSharedPreference(getContext(),"smaxMagentaf2","0");
            String c3f3 = localDataManager.getSharedPreference(getContext(),"smaxMagentaf3","0");
            String c3f4 = localDataManager.getSharedPreference(getContext(),"smaxMagentaf4","0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentagdh","7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentagdm","0"));
            float c3gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentagh","12"));
            float c3gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentagm","0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentagbh","17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentagbm","0"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentaah","22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxMagentaam","0"));

            String c4f1 = localDataManager.getSharedPreference(getContext(),"smaxSky Bluef1","0");
            String c4f2 = localDataManager.getSharedPreference(getContext(),"smaxSky Bluef2","0");
            String c4f3 = localDataManager.getSharedPreference(getContext(),"smaxSky Bluef3","0");
            String c4f4 = localDataManager.getSharedPreference(getContext(),"smaxSky Bluef4","0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Bluegdh","7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Bluegdm","0"));
            float c4gh  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Bluegh","12"));
            float c4gm  = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Bluegm","0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Bluegbh","17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Bluegbm","0"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Blueah","22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),"smaxSky Blueam","0"));

            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4),c1gdh/8 + c1gdm/100,c1gh/8 + c1gm/100,c1gbh/8 + c1gbm/100,c1ah/8 + c1am/100,lDataSet1,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8 + c2gdm/100,c2gh/8 + c2gm/100,c2gbh/8 + c2gbm/100,c2ah/8 + c2am/100,lDataSet2,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8 + c3gdm/100,c3gh/8 + c3gm/100,c3gbh/8 + c3gbm/100,c3ah/8 + c3am/100,lDataSet3,2,"Kanal 1",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8 + c4gdm/100,c4gh/8 + c4gm/100,c4gbh/8 + c4gbm/100,c4ah/8 + c4am/100,lDataSet4,2,"Kanal 1",R.color.lighgray);
        }
    }

    public void refreshChart(ArrayList entry,int pivot1,int pivot2,int pivot3,int pivot4,float time1,float time2,float time3,float time4,LineDataSet lineDataSet,int width,String channel,int color){

        entry.add(new Entry(time1, pivot1));
        entry.add(new Entry(time2, pivot2));
        entry.add(new Entry(time3, pivot3));
        entry.add(new Entry(time4, pivot4));

        lineDataSet = new LineDataSet(entry, channel);
        lineDataSet.setDrawFilled(false);
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
