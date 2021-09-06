
package com.urushiLeds.urushileds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.urushiLeds.urushileds.Class.LocalDataManager;
import com.urushiLeds.urushileds.Class.Models;
import com.urushiLeds.urushileds.Fragment.Fragment1;
import com.urushiLeds.urushileds.Fragment.Fragment2;
import com.urushiLeds.urushileds.Fragment.Fragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.urushi.urushileds.R;
import com.urushiLeds.urushileds.Fragment.Fragment4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    private ArrayList<Models> modelsArrayList = new ArrayList<>();

    BottomNavigationView bottomNavigationView;

    private FloatingActionButton fab_bottom;

    private static final String TAG = "MainActivity";
    private Fragment fragmentTemp;
    private TextView tv_status;
    private String device_id;
    private int i = 0;
    private byte[] txData = new byte[57];
    private int trial = 0,trial_ack=0;

    private boolean isTxFull = false;

    BluetoothAdapter bluetoothAdapter;
    SendReceive sendReceive;
    ArrayList<String> bleList = new ArrayList<>();

    private InputStream inputStream;
    private OutputStream outputStream;

    static final String DATA_ACK = "S";

    static final int STATE_CONNECTED = 1;
    static final int STATE_CONNECTION_FAILED = 2;
    static final int STATE_MESSAGE_RECEIVED = 3;
    static final int STATE_MESSAGE_NEXTCONNECTION_WAIT = 4;
    static final int STATE_MESSAGE_ACK_WAIT = 5;
    static final int STATE_MESSAGE_WRONG_ACK_RECEIVED = 6;

    private String tempMsg = null,hour,minute;

    private static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;
    private BluetoothSocket socket;

    ClientClass clientClass;

    ProgressDialog progress;

    LocalDataManager localDataManager = new LocalDataManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        modelsArrayList.add(new Models("CUSTOM","Channel 1","Channel 2","Channel 3","Channel 4",4));
        modelsArrayList.add(new Models("F-MAJOR","Cool White","Wide Spectrum",null,null,2));
        modelsArrayList.add(new Models("S-MAJOR","Deep Blue","Aqua Sun",null,null,2));
        modelsArrayList.add(new Models("F-MAX","Cool White","Full Spectrum","Reddish White","Blueish White",4));
        modelsArrayList.add(new Models("S-MAX","Deep Blue","Aqua Sun","Magenta","Sky Blue",4));

        localDataManager.setSharedPreference(getApplicationContext(),"test_model","false");

        if (findViewById(R.id.frame) != null) {
            if (savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.frame,new Fragment4()).commit();
           }

        progress = ProgressDialog.show(MainActivity.this, "Baglanıyor...", "Lütfen Bekleyin");

        // Gelen device id ile bluetooth bağlantısını kur.
        if (bleList.size()>0){
            device_id = bleList.get(0);
            clientClass = new ClientClass(device_id);
            clientClass.start();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_settings:
                        fragmentTemp = new Fragment2();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragmentTemp,""+fragmentTemp).commit();
                        break;
                    case R.id.action_back:
                        if (socket.isConnected()){
                            closeBluetooth();
                        }
                        startActivity(new Intent(MainActivity.this,BluetoothScanActivity.class));
                        finish();
                        break;
                    case R.id.action_test:
                        fragmentTemp = new Fragment3();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragmentTemp,""+fragmentTemp).commit();
                        break;
                    case R.id.action_home:
                        if (findViewById(R.id.frame) != null) {
                            getSupportFragmentManager().beginTransaction().add(R.id.frame,new Fragment1()).commit();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        bluetoothAdapter.disable();
    }

    public void init(){
        tv_status = findViewById(R.id.tv_status);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        fab_bottom = findViewById(R.id.fab_bottom);

        bottomNavigationView.setBackground(null);

        bleList = getIntent().getStringArrayListExtra("bleDevicesList");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                fragmentTemp = new Fragment2();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragmentTemp).commit();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case STATE_CONNECTED :
                    tv_status.setText("Bağlandı ... ");
                    tv_status.setTextColor(Color.GREEN);
                    Log.e(TAG,"Bağlandı");
                    if (i>0){
                        if(socket.isConnected() && isTxFull){
                            sendReceive.write(txData);
                            Log.e(TAG,"Diğer cihaza aynı veriler gönderildi");

                            Message message = Message.obtain();
                            message.what = STATE_MESSAGE_ACK_WAIT;
                            handler.sendMessage(message);
                        }
                    }else{
                        fab_bottom.setEnabled(true);
                    }
                    progress.dismiss();
                    break;
                case STATE_CONNECTION_FAILED :
                    tv_status.setText("Bağlantı Hatası ... ");
                    tv_status.setTextColor(Color.RED);
                    Log.e(TAG,"Bağlantı Hatası");
                    progress.dismiss();
                    if (socket.isConnected()){
                       closeBluetooth();
                    }
                    if (i<bleList.size()){
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);
                    }
                    break;
                case STATE_MESSAGE_RECEIVED :

                    tempMsg = getMessage(msg);
                    timerHandler.removeCallbacks(timerRunnable);
                    trial = 0;
                    if (tempMsg.equals(DATA_ACK)){
                        Log.e(TAG,"Mesaj Doğru Alındı");
                        trial_ack = 0;

                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);

                    }else{
                        Log.e(TAG,"Yanlış doğrulama kodu alındı");
                        trial_ack ++;
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_WRONG_ACK_RECEIVED;
                        handler.sendMessage(message);
                    }
                    break;
                case STATE_MESSAGE_NEXTCONNECTION_WAIT:

                    i++;
                    if (i<bleList.size()){
                        Log.e(TAG,"Diğer cihaza bağlanıyor ...");
                        tv_status.setText("Bağlanıyor");
                        tv_status.setTextColor(getResources().getColor(R.color.accent));
                        progress = ProgressDialog.show(MainActivity.this, "Diğer Cihaza Baglanıyor...", "Lütfen Bekleyin");
                        // Bluetooth bağlantısını kes.
                        if (socket.isConnected()){
                            closeBluetooth();
                        }

                        device_id = bleList.get(i);

                        clientClass = new ClientClass(device_id);
                        clientClass.start();

                    }else if (socket.isConnected()){
                        Log.e(TAG,"Tüm cihazlara veriler gönderildi.");
                        Toast.makeText(getApplicationContext(),"Tüm cihazlara verile gönderildi",Toast.LENGTH_LONG).show();
                        fab_bottom.setEnabled(true);
                        tv_status.setText("Bağlı");
                        tv_status.setTextColor(Color.GREEN);
                    }

                    break;
                case STATE_MESSAGE_ACK_WAIT :
                    Log.e(TAG,"Doğrulama kodu bekleniyor ...");
                    tv_status.setText("doğrulama bekleniyor");
                    tv_status.setTextColor(getResources().getColor(R.color.accent));
                    trial ++;
                    // 30.sn ACK gelmesini bekle
                    timerHandler.postDelayed(timerRunnable, 30000);
                    break;
                case STATE_MESSAGE_WRONG_ACK_RECEIVED:
                    Log.e(TAG,"Yanlış Doğrulama kodu alındı.");
                    //todo yanlış ACK geldiğinde burası yapılacak !!!
                    if (trial_ack<3){
                        sendReceive.write(txData);
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_ACK_WAIT;
                        handler.sendMessage(message);
                    }else {
                        trial_ack = 0;
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);
                    }
                    break;
            }
            return false;
        }
    });

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (trial < 3){
                try {
                    sendReceive.write(txData);
                    Message message = Message.obtain();
                    message.what = STATE_MESSAGE_ACK_WAIT;
                    handler.sendMessage(message);
                }catch (Exception e){
                    Log.e(TAG,e.getLocalizedMessage());
                }
            }else {
                trial = 0;
                if (socket.isConnected()){
                    closeBluetooth();
                }
                if (i<bleList.size()){
                    try {
                        i ++;
                        device_id = bleList.get(i);
                        clientClass = new ClientClass(device_id);
                        clientClass.start();

                        sendReceive.write(txData);

                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_ACK_WAIT;
                        handler.sendMessage(message);

                    }catch (Exception e){
                        Log.e(TAG,e.getLocalizedMessage());
                    }
                }
                //timerHandler.removeCallbacks(timerRunnable);
            }
        }
    };

    public void fab_bottom(View view) {
        // anlık saat ve dakika bilgisini al
        getDateTime();
        fab_bottom.setEnabled(false);
        if (!socket.isConnected()){
            startActivity(new Intent(MainActivity.this,BluetoothScanActivity.class));
            finish();
        }
        String model = localDataManager.getSharedPreference(getApplicationContext(),"model","");
        String test_model = localDataManager.getSharedPreference(getApplicationContext(),"test_model","false");
        Log.e(TAG, "fab_bottom: hour" +hour );
        Log.e(TAG, "fab_bottom: minute" +minute );
        txData[54] = Byte.parseByte(hour);
        txData[55] = Byte.parseByte(minute);
        if (test_model.equals("test")){
            txData[0] = 0x65;
            txData[1] = 0x06;
            txData[2] = 0xA;
            String test_f1 = localDataManager.getSharedPreference(getApplicationContext(),"testf1","0");
            String test_f2 = localDataManager.getSharedPreference(getApplicationContext(),"testf2","0");
            String test_f3 = localDataManager.getSharedPreference(getApplicationContext(),"testf3","0");
            String test_f4 = localDataManager.getSharedPreference(getApplicationContext(),"testf4","0");
            txData[3] = (byte) Integer.parseInt(test_f1);
            txData[4] = (byte) Integer.parseInt(test_f2);
            txData[5] = (byte) Integer.parseInt(test_f3);
            txData[6] = (byte) Integer.parseInt(test_f4);

            for (int i = 7; i < 54; i++){
                txData[i] = 0x00;
            }

            txData[56] = 0x66;
        }else{
            if (model.equals("fmajor")){

                txData[0] = 0x65;
                txData[1] = 0x01;
                txData[2] = 0x01;

                String fmajor_cw_gd_f = "0";
                String fmajor_cw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegdh","07");
                String fmajor_cw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegdm","00");
                txData[3] = (byte) Integer.parseInt(fmajor_cw_gd_f);
                txData[4] = (byte) Integer.parseInt(fmajor_cw_gd_h);
                txData[5] = (byte) Integer.parseInt(fmajor_cw_gd_m);

                String fmajor_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitef2","0");
                String fmajor_cw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegh","12");
                String fmajor_cw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegm","00");
                txData[6] = (byte) Integer.parseInt(fmajor_cw_g_f);
                txData[7] = (byte) Integer.parseInt(fmajor_cw_g_h);
                txData[8] = (byte) Integer.parseInt(fmajor_cw_g_m);

                String fmajor_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitef3","0");
                String fmajor_cw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegbh","17");
                String fmajor_cw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegbm","00");
                txData[9] = (byte) Integer.parseInt(fmajor_cw_gb_f);
                txData[10] = (byte) Integer.parseInt(fmajor_cw_gb_h);
                txData[11] = (byte) Integer.parseInt(fmajor_cw_gb_m);

                String fmajor_cw_a_f = "0";
                String fmajor_cw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whiteah","22");
                String fmajor_cw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whiteam","00");
                txData[12] = (byte) Integer.parseInt(fmajor_cw_a_f);
                txData[13] = (byte) Integer.parseInt(fmajor_cw_a_h);
                txData[14] = (byte) Integer.parseInt(fmajor_cw_a_m);

                txData[15] = 0x02;
                String fmajor_ws_gd_f = "0";
                String fmajor_ws_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgdh","07");
                String fmajor_ws_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgdm","00");
                txData[16] = (byte) Integer.parseInt(fmajor_ws_gd_f);
                txData[17] = (byte) Integer.parseInt(fmajor_ws_gd_h);
                txData[18] = (byte) Integer.parseInt(fmajor_ws_gd_m);

                String fmajor_ws_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumf2","0");
                String fmajor_ws_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgh","12");
                String fmajor_ws_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgm","00");
                txData[19] = (byte) Integer.parseInt(fmajor_ws_g_f);
                txData[20] = (byte) Integer.parseInt(fmajor_ws_g_h);
                txData[21] = (byte) Integer.parseInt(fmajor_ws_g_m);

                String fmajor_ws_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumf3","0");
                String fmajor_ws_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgbh","17");
                String fmajor_ws_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgbm","00");
                txData[22] = (byte) Integer.parseInt(fmajor_ws_gb_f);
                txData[23] = (byte) Integer.parseInt(fmajor_ws_gb_h);
                txData[24] = (byte) Integer.parseInt(fmajor_ws_gb_m);

                String fmajor_ws_a_f = "0";
                String fmajor_ws_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumah","22");
                String fmajor_ws_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumam","00");
                txData[25] = (byte) Integer.parseInt(fmajor_ws_a_f);
                txData[26] = (byte) Integer.parseInt(fmajor_ws_a_h);
                txData[27] = (byte) Integer.parseInt(fmajor_ws_a_m);

                for (int i = 28; i <54; i++){
                    txData[i] = 0;
                }

                txData[56] = 0x66;

            }else if (model.equals("smajor")){
                txData[0] = 0x65;
                txData[1] = 0x02;
                txData[2] = 0x01;
                String smajor_db_gd_f = "0";
                String smajor_db_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegdh","07");
                String smajor_db_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegdm","00");
                txData[3] = (byte) Integer.parseInt(smajor_db_gd_f);
                txData[4] = (byte) Integer.parseInt(smajor_db_gd_h);
                txData[5] = (byte) Integer.parseInt(smajor_db_gd_m);
                String smajor_db_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluef2","0");
                String smajor_db_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegh","12");
                String smajor_db_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegm","00");
                txData[6] = (byte) Integer.parseInt(smajor_db_g_f);
                txData[7] = (byte) Integer.parseInt(smajor_db_g_h);
                txData[8] = (byte) Integer.parseInt(smajor_db_g_m);
                String smajor_db_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluef3","0");
                String smajor_db_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegbh","17");
                String smajor_db_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegbm","00");
                txData[9] = (byte) Integer.parseInt(smajor_db_gb_f);
                txData[10] = (byte) Integer.parseInt(smajor_db_gb_h);
                txData[11] = (byte) Integer.parseInt(smajor_db_gb_m);
                String smajor_db_a_f = "0";
                String smajor_db_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Blueah","22");
                String smajor_db_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Blueam","00");
                txData[12] = (byte) Integer.parseInt(smajor_db_a_f);
                txData[13] = (byte) Integer.parseInt(smajor_db_a_h);
                txData[14] = (byte) Integer.parseInt(smajor_db_a_m);

                txData[15] = 0x02;
                String smajor_as_gd_f = "0";
                String smajor_as_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungdh","07");
                String smajor_as_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungdm","00");
                txData[16] = (byte) Integer.parseInt(smajor_as_gd_f);
                txData[17] = (byte) Integer.parseInt(smajor_as_gd_h);
                txData[18] = (byte) Integer.parseInt(smajor_as_gd_m);
                String smajor_as_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunf2","0");
                String smajor_as_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungh","12");
                String smajor_as_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungm","00");
                txData[19] = (byte) Integer.parseInt(smajor_as_g_f);
                txData[20] = (byte) Integer.parseInt(smajor_as_g_h);
                txData[21] = (byte) Integer.parseInt(smajor_as_g_m);
                String smajor_as_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunf3","0");
                String smajor_as_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungbh","17");
                String smajor_as_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungbm","00");
                txData[22] = (byte) Integer.parseInt(smajor_as_gb_f);
                txData[23] = (byte) Integer.parseInt(smajor_as_gb_h);
                txData[24] = (byte) Integer.parseInt(smajor_as_gb_m);
                String smajor_as_a_f = "0";
                String smajor_as_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunah","22");
                String smajor_as_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunam","00");
                txData[25] = (byte) Integer.parseInt(smajor_as_a_f);
                txData[26] = (byte) Integer.parseInt(smajor_as_a_h);
                txData[27] = (byte) Integer.parseInt(smajor_as_a_m);

                for (int i = 28; i <54; i++){
                    txData[i] = 0;
                }

                txData[56] = 0x66;

            }else if (model.equals("fmax")){
                txData[0] = 0x65;
                txData[1] = 0x03;
                txData[2] = 0x01;

                String fmax_cw_gd_f = "0";
                String fmax_cw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegdh","07");
                String fmax_cw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegdm","00");
                txData[3] = (byte) Integer.parseInt(fmax_cw_gd_f);
                txData[4] = (byte) Integer.parseInt(fmax_cw_gd_h);
                txData[5] = (byte) Integer.parseInt(fmax_cw_gd_m);
                String fmax_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitef2","0");
                String fmax_cw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegh","12");
                String fmax_cw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegm","00");
                txData[6] = (byte) Integer.parseInt(fmax_cw_g_f);
                txData[7] = (byte) Integer.parseInt(fmax_cw_g_h);
                txData[8] = (byte) Integer.parseInt(fmax_cw_g_m);
                String fmax_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitef3","0");
                String fmax_cw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegbh","17");
                String fmax_cw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegbm","00");
                txData[9] = (byte) Integer.parseInt(fmax_cw_gb_f);
                txData[10] = (byte) Integer.parseInt(fmax_cw_gb_h);
                txData[11] = (byte) Integer.parseInt(fmax_cw_gb_m);
                String fmax_cw_a_f = "0";
                String fmax_cw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whiteah","22");
                String fmax_cw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whiteam","00");
                txData[12] = (byte) Integer.parseInt(fmax_cw_a_f);
                txData[13] = (byte) Integer.parseInt(fmax_cw_a_h);
                txData[14] = (byte) Integer.parseInt(fmax_cw_a_m);

                txData[15] = 0x02;
                String fmax_fs_gd_f = "0";
                String fmax_fs_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgdh","07");
                String fmax_fs_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgdm","00");
                txData[16] = (byte) Integer.parseInt(fmax_fs_gd_f);
                txData[17] = (byte) Integer.parseInt(fmax_fs_gd_h);
                txData[18] = (byte) Integer.parseInt(fmax_fs_gd_m);
                String fmax_fs_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumf2","0");
                String fmax_fs_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgh","12");
                String fmax_fs_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgm","00");
                txData[19] = (byte) Integer.parseInt(fmax_fs_g_f);
                txData[20] = (byte) Integer.parseInt(fmax_fs_g_h);
                txData[21] = (byte) Integer.parseInt(fmax_fs_g_m);
                String fmax_fs_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumf3","0");
                String fmax_fs_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgbh","17");
                String fmax_fs_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgbm","00");
                txData[22] = (byte) Integer.parseInt(fmax_fs_gb_f);
                txData[23] = (byte) Integer.parseInt(fmax_fs_gb_h);
                txData[24] = (byte) Integer.parseInt(fmax_fs_gb_m);
                String fmax_fs_a_f = "0";
                String fmax_fs_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumah","22");
                String fmax_fs_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumam","00");
                txData[25] = (byte) Integer.parseInt(fmax_fs_a_f);
                txData[26] = (byte) Integer.parseInt(fmax_fs_a_h);
                txData[27] = (byte) Integer.parseInt(fmax_fs_a_m);

                txData[28] = 0x03;
                String fmax_rw_gd_f = "0";
                String fmax_rw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegdh","07");
                String fmax_rw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegdm","00");
                txData[29] = (byte) Integer.parseInt(fmax_rw_gd_f);
                txData[30] = (byte) Integer.parseInt(fmax_rw_gd_h);
                txData[31] = (byte) Integer.parseInt(fmax_rw_gd_m);
                String fmax_rw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitef2","0");
                String fmax_rw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegh","12");
                String fmax_rw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegm","00");
                txData[32] = (byte) Integer.parseInt(fmax_rw_g_f);
                txData[33] = (byte) Integer.parseInt(fmax_rw_g_h);
                txData[34] = (byte) Integer.parseInt(fmax_rw_g_m);
                String fmax_rw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitef3","0");
                String fmax_rw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegbh","17");
                String fmax_rw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegbm","00");
                txData[35] = (byte) Integer.parseInt(fmax_rw_gb_f);
                txData[36] = (byte) Integer.parseInt(fmax_rw_gb_h);
                txData[37] = (byte) Integer.parseInt(fmax_rw_gb_m);
                String fmax_rw_a_f = "0";
                String fmax_rw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whiteah","22");
                String fmax_rw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whiteam","00");
                txData[38] = (byte) Integer.parseInt(fmax_rw_a_f);
                txData[39] = (byte) Integer.parseInt(fmax_rw_a_h);
                txData[40] = (byte) Integer.parseInt(fmax_rw_a_m);

                txData[41] = 0x04;
                String fmax_bw_gd_f = "0";
                String fmax_bw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegdh","07");
                String fmax_bw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegdm","00");
                txData[42] = (byte) Integer.parseInt(fmax_bw_gd_f);
                txData[43] = (byte) Integer.parseInt(fmax_bw_gd_h);
                txData[44] = (byte) Integer.parseInt(fmax_bw_gd_m);
                String fmax_bw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitef2","0");
                String fmax_bw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegh","12");
                String fmax_bw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegm","00");
                txData[45] = (byte) Integer.parseInt(fmax_bw_g_f);
                txData[46] = (byte) Integer.parseInt(fmax_bw_g_h);
                txData[47] = (byte) Integer.parseInt(fmax_bw_g_m);
                String fmax_bw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitef3","0");
                String fmax_bw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegbh","17");
                String fmax_bw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegbm","00");
                txData[48] = (byte) Integer.parseInt(fmax_bw_gb_f);
                txData[49] = (byte) Integer.parseInt(fmax_bw_gb_h);
                txData[50] = (byte) Integer.parseInt(fmax_bw_gb_m);
                String fmax_bw_a_f = "0";
                String fmax_bw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whiteah","22");
                String fmax_bw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whiteam","00");
                txData[51] = (byte) Integer.parseInt(fmax_bw_a_f);
                txData[52] = (byte) Integer.parseInt(fmax_bw_a_h);
                txData[53] = (byte) Integer.parseInt(fmax_bw_a_m);

                txData[56] = 0x66;
            }else if (model.equals("smax")){
                txData[0] = 0x65;
                txData[1] = 0x04;
                txData[2] = 0x01;

                String smax_db_gd_f = "0";
                String smax_db_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegdh","07");
                String smax_db_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegdm","00");
                txData[3] = (byte) Integer.parseInt(smax_db_gd_f);
                txData[4] = (byte) Integer.parseInt(smax_db_gd_h);
                txData[5] = (byte) Integer.parseInt(smax_db_gd_m);
                String smax_db_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluef2","0");
                String smax_db_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegh","12");
                String smax_db_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegm","00");
                txData[6] = (byte) Integer.parseInt(smax_db_g_f);
                txData[7] = (byte) Integer.parseInt(smax_db_g_h);
                txData[8] = (byte) Integer.parseInt(smax_db_g_m);
                String smax_db_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluef3","0");
                String smax_db_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegbh","17");
                String smax_db_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegbm","00");
                txData[9] = (byte) Integer.parseInt(smax_db_gb_f);
                txData[10] = (byte) Integer.parseInt(smax_db_gb_h);
                txData[11] = (byte) Integer.parseInt(smax_db_gb_m);
                String smax_db_a_f = "0";
                String smax_db_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Blueah","22");
                String smax_db_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Blueam","00");
                txData[12] = (byte) Integer.parseInt(smax_db_a_f);
                txData[13] = (byte) Integer.parseInt(smax_db_a_h);
                txData[14] = (byte) Integer.parseInt(smax_db_a_m);

                txData[15] = 0x02;
                String smax_as_gd_f = "0";
                String smax_as_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungdh","07");
                String smax_as_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungdm","00");
                txData[16] = (byte) Integer.parseInt(smax_as_gd_f);
                txData[17] = (byte) Integer.parseInt(smax_as_gd_h);
                txData[18] = (byte) Integer.parseInt(smax_as_gd_m);
                String smax_as_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunf2","0");
                String smax_as_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungh","12");
                String smax_as_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungm","00");
                txData[19] = (byte) Integer.parseInt(smax_as_g_f);
                txData[20] = (byte) Integer.parseInt(smax_as_g_h);
                txData[21] = (byte) Integer.parseInt(smax_as_g_m);
                String smax_as_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunf3","0");
                String smax_as_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungbh","17");
                String smax_as_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungbm","00");
                txData[22] = (byte) Integer.parseInt(smax_as_gb_f);
                txData[23] = (byte) Integer.parseInt(smax_as_gb_h);
                txData[24] = (byte) Integer.parseInt(smax_as_gb_m);
                String smax_as_a_f = "0";
                String smax_as_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunah","22");
                String smax_as_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunam","00");
                txData[25] = (byte) Integer.parseInt(smax_as_a_f);
                txData[26] = (byte) Integer.parseInt(smax_as_a_h);
                txData[27] = (byte) Integer.parseInt(smax_as_a_m);

                txData[28] = 0x03;
                String smax_m_gd_f = "0";
                String smax_m_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagdh","07");
                String smax_m_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagdm","00");
                txData[29] = (byte) Integer.parseInt(smax_m_gd_f);
                txData[30] = (byte) Integer.parseInt(smax_m_gd_h);
                txData[31] = (byte) Integer.parseInt(smax_m_gd_m);
                String smax_m_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentaf2","0");
                String smax_m_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagh","12");
                String smax_m_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagm","00");
                txData[32] = (byte) Integer.parseInt(smax_m_g_f);
                txData[33] = (byte) Integer.parseInt(smax_m_g_h);
                txData[34] = (byte) Integer.parseInt(smax_m_g_m);
                String smax_m_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentaf3","0");
                String smax_m_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagbh","17");
                String smax_m_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagbm","00");
                txData[35] = (byte) Integer.parseInt(smax_m_gb_f);
                txData[36] = (byte) Integer.parseInt(smax_m_gb_h);
                txData[37] = (byte) Integer.parseInt(smax_m_gb_m);
                String smax_m_a_f = "0";
                String smax_m_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentaah","22");
                String smax_m_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxMagentaam","00");
                txData[38] = (byte) Integer.parseInt(smax_m_a_f);
                txData[39] = (byte) Integer.parseInt(smax_m_a_h);
                txData[40] = (byte) Integer.parseInt(smax_m_a_m);

                txData[41] = 0x04;
                String smax_sb_gd_f = "0";
                String smax_sb_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegdh","07");
                String smax_sb_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegdm","00");
                txData[42] = (byte) Integer.parseInt(smax_sb_gd_f);
                txData[43] = (byte) Integer.parseInt(smax_sb_gd_h);
                txData[44] = (byte) Integer.parseInt(smax_sb_gd_m);
                String smax_sb_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluef2","0");
                String smax_sb_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegh","12");
                String smax_sb_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegm","00");
                txData[45] = (byte) Integer.parseInt(smax_sb_g_f);
                txData[46] = (byte) Integer.parseInt(smax_sb_g_h);
                txData[47] = (byte) Integer.parseInt(smax_sb_g_m);
                String smax_sb_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluef3","0");
                String smax_sb_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegbh","17");
                String smax_sb_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegbm","00");
                txData[48] = (byte) Integer.parseInt(smax_sb_gb_f);
                txData[49] = (byte) Integer.parseInt(smax_sb_gb_h);
                txData[50] = (byte) Integer.parseInt(smax_sb_gb_m);
                String smax_sb_a_f = "0";
                String smax_sb_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Blueah","22");
                String smax_sb_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Blueam","00");
                txData[51] = (byte) Integer.parseInt(smax_sb_a_f);
                txData[52] = (byte) Integer.parseInt(smax_sb_a_h);
                txData[53] = (byte) Integer.parseInt(smax_sb_a_m);

                txData[56] = 0x66;
            }else{
                txData[0] = 0x65;
                txData[1] = 0x05;
                txData[2] = 0x01;

                String manual_c1_gd_f = "0";
                String manual_c1_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gdh","07");
                String manual_c1_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gdm","00");
                txData[3] = (byte) Integer.parseInt(manual_c1_gd_f);
                txData[4] = (byte) Integer.parseInt(manual_c1_gd_h);
                txData[5] = (byte) Integer.parseInt(manual_c1_gd_m);
                String manual_c1_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1f2","0");
                String manual_c1_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gh","12");
                String manual_c1_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gm","00");
                txData[6] = (byte) Integer.parseInt(manual_c1_g_f);
                txData[7] = (byte) Integer.parseInt(manual_c1_g_h);
                txData[8] = (byte) Integer.parseInt(manual_c1_g_m);
                String manual_c1_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1f3","0");
                String manual_c1_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gbh","17");
                String manual_c1_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gbm","00");
                txData[9] = (byte) Integer.parseInt(manual_c1_gb_f);
                txData[10] = (byte) Integer.parseInt(manual_c1_gb_h);
                txData[11] = (byte) Integer.parseInt(manual_c1_gb_m);
                String manual_c1_a_f = "0";
                String manual_c1_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1ah","22");
                String manual_c1_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1am","00");
                txData[12] = (byte) Integer.parseInt(manual_c1_a_f);
                txData[13] = (byte) Integer.parseInt(manual_c1_a_h);
                txData[14] = (byte) Integer.parseInt(manual_c1_a_m);

                txData[15] = 0x02;
                String manual_c2_gd_f = "0";
                String manual_c2_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gdh","07");
                String manual_c2_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gdm","00");
                txData[16] = (byte) Integer.parseInt(manual_c2_gd_f);
                txData[17] = (byte) Integer.parseInt(manual_c2_gd_h);
                txData[18] = (byte) Integer.parseInt(manual_c2_gd_m);
                String manual_c2_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2f2","0");
                String manual_c2_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gh","12");
                String manual_c2_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gm","00");
                txData[19] = (byte) Integer.parseInt(manual_c2_g_f);
                txData[20] = (byte) Integer.parseInt(manual_c2_g_h);
                txData[21] = (byte) Integer.parseInt(manual_c2_g_m);
                String manual_c2_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2f3","0");
                String manual_c2_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gbh","17");
                String manual_c2_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gbm","00");
                txData[22] = (byte) Integer.parseInt(manual_c2_gb_f);
                txData[23] = (byte) Integer.parseInt(manual_c2_gb_h);
                txData[24] = (byte) Integer.parseInt(manual_c2_gb_m);
                String manual_c2_a_f = "0";
                String manual_c2_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2ah","22");
                String manual_c2_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2am","00");
                txData[25] = (byte) Integer.parseInt(manual_c2_a_f);
                txData[26] = (byte) Integer.parseInt(manual_c2_a_h);
                txData[27] = (byte) Integer.parseInt(manual_c2_a_m);

                txData[28] = 0x03;
                String manual_c3_gd_f = "0";
                String manual_c3_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gdh","07");
                String manual_c3_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gdm","00");
                txData[29] = (byte) Integer.parseInt(manual_c3_gd_f);
                txData[30] = (byte) Integer.parseInt(manual_c3_gd_h);
                txData[31] = (byte) Integer.parseInt(manual_c3_gd_m);
                String manual_c3_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3f2","0");
                String manual_c3_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gh","12");
                String manual_c3_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gm","00");
                txData[32] = (byte) Integer.parseInt(manual_c3_g_f);
                txData[33] = (byte) Integer.parseInt(manual_c3_g_h);
                txData[34] = (byte) Integer.parseInt(manual_c3_g_m);
                String manual_c3_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3f3","0");
                String manual_c3_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gbh","17");
                String manual_c3_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gbm","00");
                txData[35] = (byte) Integer.parseInt(manual_c3_gb_f);
                txData[36] = (byte) Integer.parseInt(manual_c3_gb_h);
                txData[37] = (byte) Integer.parseInt(manual_c3_gb_m);
                String manual_c3_a_f = "0";
                String manual_c3_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3ah","22");
                String manual_c3_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3am","00");
                txData[38] = (byte) Integer.parseInt(manual_c3_a_f);
                txData[39] = (byte) Integer.parseInt(manual_c3_a_h);
                txData[40] = (byte) Integer.parseInt(manual_c3_a_m);

                txData[41] = 0x04;
                String manual_c4_gd_f = "0";
                String manual_c4_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gdh","07");
                String manual_c4_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gdm","00");
                txData[42] = (byte) Integer.parseInt(manual_c4_gd_f);
                txData[43] = (byte) Integer.parseInt(manual_c4_gd_h);
                txData[44] = (byte) Integer.parseInt(manual_c4_gd_m);
                String manual_c4_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4f2","0");
                String manual_c4_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gh","12");
                String manual_c4_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gm","00");
                txData[45] = (byte) Integer.parseInt(manual_c4_g_f);
                txData[46] = (byte) Integer.parseInt(manual_c4_g_h);
                txData[47] = (byte) Integer.parseInt(manual_c4_g_m);
                String manual_c4_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4f3","0");
                String manual_c4_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gbh","17");
                String manual_c4_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gbm","00");
                txData[48] = (byte) Integer.parseInt(manual_c4_gb_f);
                txData[49] = (byte) Integer.parseInt(manual_c4_gb_h);
                txData[50] = (byte) Integer.parseInt(manual_c4_gb_m);
                String manual_c4_a_f = "0";
                String manual_c4_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4ah","22");
                String manual_c4_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4am","00");
                txData[51] = (byte) Integer.parseInt(manual_c4_a_f);
                txData[52] = (byte) Integer.parseInt(manual_c4_a_h);
                txData[53] = (byte) Integer.parseInt(manual_c4_a_m);

                txData[56] = 0x66;
            }
        }

        // Datalar gönderiliyor
        for (int i = 0; i < txData.length;i++){
            Log.e(TAG, "tx data "+i+". data = "+txData[i] );
        }
        try {
            isTxFull = true;
            sendReceive.write(txData);
            Log.e(TAG,"Veriler gönderildi.");
            Message message = Message.obtain();
            message.what = STATE_MESSAGE_ACK_WAIT;
            handler.sendMessage(message);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }


    }

    public void btn_closeConnection(View view) {
        closeBluetooth();
    }

    private class ClientClass extends Thread{

        public ClientClass (String device_id){
            device = bluetoothAdapter.getRemoteDevice(device_id);
            try {
                socket = device.createRfcommSocketToServiceRecord(ESP32_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                // bağlantı kurulu ise önce kapat
                if (socket.isConnected()){
                   closeBluetooth();
                }
                socket.connect();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                Message message = Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();

                Message message = Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try {
                    bytes =  inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeBluetooth(){
        if (inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tv_status.setText("Bağlantı Kesildi");
        tv_status.setTextColor(Color.RED);
        Log.e(TAG,"Bluetooth soket kapatıldı");
    }

    private String getMessage(Message msg){
        byte[] readBuffer = (byte[]) msg.obj;
        return new String(readBuffer,0,msg.arg1);
    }

    private void getDateTime(){
        // getDateTime: 10-05
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
        String currentTime = sdf.format(new Date());
        hour = currentTime.substring(0,2);
        minute = currentTime.substring(3,5);
        Log.e(TAG, "getDateTime: hour : "+hour+" minute : "+minute);
    }

}
