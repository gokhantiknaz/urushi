
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
import com.urushiLeds.urushileds.Fragment.Fragment1;
import com.urushiLeds.urushileds.Fragment.Fragment2;
import com.urushiLeds.urushileds.Fragment.Fragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.urushi.urushileds.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigationView;

    private FloatingActionButton fab_bottom;

    private static final String TAG = "MainActivity";
    private Fragment fragmentTemp;
    private Toolbar tb_main;
    private TextView tv_status;
    private String device_id,msgg;
    private boolean isMessageReceived = false;
    private int i = 0;
    private byte[] txData = new byte[55];
    private int trial = 0;

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
    static final int STATE_MESSAGE_NEXTCONNECTION_CONNECTED = 5;
    static final int STATE_MESSAGE_ACK_WAIT = 6;
    static final int STATE_MESSAGE_WRONG_ACK_RECEIVED = 7;

    private String tempMsg = null;

    private static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;
    private BluetoothSocket socket;

    ClientClass clientClass;

    ProgressDialog progress;

    LocalDataManager localDataManager = new LocalDataManager();

    private long timeStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String model = localDataManager.getSharedPreference(getApplicationContext(),"model","manual");
        if (model.equals("test")){
            if (findViewById(R.id.frame) != null) {
                if (savedInstanceState != null){
                    return;
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame,new Fragment3()).commit();
            }
        }else {
            if (findViewById(R.id.frame) != null) {
                if (savedInstanceState != null){
                    return;
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame,new Fragment1()).commit();
            }
        }

        init();

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
                        localDataManager.setSharedPreference(getApplicationContext(),"model","test");
                        fragmentTemp = new Fragment3();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragmentTemp,""+fragmentTemp).commit();
                        break;
                    case R.id.action_home:
                        String model = localDataManager.getSharedPreference(getApplicationContext(),"model","manual");
                        if (model.equals("test")){
                            if (findViewById(R.id.frame) != null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.frame,new Fragment3()).commit();
                            }
                        }else {
                            if (findViewById(R.id.frame) != null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.frame,new Fragment1()).commit();
                            }
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
        tb_main = findViewById(R.id.tb_main);
        tv_status = findViewById(R.id.tv_status);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        fab_bottom = findViewById(R.id.fab_bottom);

        bottomNavigationView.setBackground(null);

        //toolbarSettings();
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
                        if(socket.isConnected()){
                            sendReceive.write(txData);
                            Log.e(TAG,"Diğer cihaza aynı veriler gönderildi");
                        }
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_ACK_WAIT;
                        handler.sendMessage(message);
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
                    /*byte[] readBuffer = (byte[]) msg.obj;
                    tempMsg = new String(readBuffer,0,msg.arg1);
                    Log.e(TAG,"Mesaj Alındı Mesaj : "+tempMsg);*/
                    tempMsg = getMessage(msg);

                    if (tempMsg.equals(DATA_ACK)){
                        Log.e(TAG,"Mesaj Doğru Alındı");

                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);

                    }else{
                        Log.e(TAG,"Yanlış doğrulama kodu alındı");
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_WRONG_ACK_RECEIVED;
                        handler.sendMessage(message);
                    }
                    break;
                case STATE_MESSAGE_NEXTCONNECTION_WAIT:
                    Log.e(TAG,"Diğer cihaza bağlanıyor ...");
                    progress = ProgressDialog.show(MainActivity.this, "Diğer Cihaza Baglanıyor...", "Lütfen Bekleyin");

                    i++;
                    if (i<bleList.size()){
                        // Bluetooth bağlantısını kes.
                        if (socket.isConnected()){
                            closeBluetooth();
                        }

                        device_id = bleList.get(i);
                        Log.e(TAG,"Diğer cihazın bluetooth id si alınıyor. device _id : "+device_id);
                        Log.e(TAG,"Diğer cihazın bluetooth id si ile yeni client class oluşturuldu başlatıldı.");

                        clientClass = new ClientClass(device_id);
                        clientClass.start();

                    }else{
                        Log.e(TAG,"Tüm cihazlara veriler gönderildi.");
                        closeBluetooth();
                    }

                    break;
                case STATE_MESSAGE_ACK_WAIT :
                    Log.e(TAG,"Doğrulama kodu bekleniyor ...");
                    trial ++;
                    progress = ProgressDialog.show(MainActivity.this, "ACK bekleniyor ...", "Lütfen Bekleyin");
                    // 30.sn ACK gelmesini bekle
                    //fixme yorum satırı yapıldı
                    //timerHandler.postDelayed(timerRunnable, 30000);
                    break;
                case STATE_MESSAGE_WRONG_ACK_RECEIVED:
                    Log.e(TAG,"Yanlış Doğrulama kodu alındı.");
                    //todo yanlış ACK geldiğinde burası yapılacak !!!
                    progress.dismiss();
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
                progress.dismiss();
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
                        Log.e(TAG,"399. Diğer cihaza veri gönderildi.");

                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_ACK_WAIT;
                        handler.sendMessage(message);

                    }catch (Exception e){
                        Log.e(TAG,e.getLocalizedMessage());
                    }
                }
                //timerHandler.removeCallbacks(timerRunnable);
                progress.dismiss();
            }
        }
    };

    public void fab_bottom(View view) {
        // Gönderilecek değerlerin hafızadan çekilmesi işlemi
        try {
            socket.getOutputStream().write(0x55);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String model = localDataManager.getSharedPreference(getApplicationContext(),"model","");
        if (model.equals("fmajor")){

            txData[0] = 0x65;
            txData[1] = 0x01;
            txData[2] = 0x01;

            String fmajor_cw_gd_f = "0";
            String fmajor_cw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegdh","0");
            String fmajor_cw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegdm","0");
            txData[3] = (byte) Integer.parseInt(fmajor_cw_gd_f);
            txData[4] = (byte) Integer.parseInt(fmajor_cw_gd_h);
            txData[5] = (byte) Integer.parseInt(fmajor_cw_gd_m);

            String fmajor_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitef2","0");
            String fmajor_cw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegh","0");
            String fmajor_cw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegm","0");
            txData[6] = (byte) Integer.parseInt(fmajor_cw_g_f);
            txData[7] = (byte) Integer.parseInt(fmajor_cw_g_h);
            txData[8] = (byte) Integer.parseInt(fmajor_cw_g_m);

            String fmajor_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitef3","0");
            String fmajor_cw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegbh","0");
            String fmajor_cw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whitegbm","0");
            txData[9] = (byte) Integer.parseInt(fmajor_cw_gb_f);
            txData[10] = (byte) Integer.parseInt(fmajor_cw_gb_h);
            txData[11] = (byte) Integer.parseInt(fmajor_cw_gb_m);

            String fmajor_cw_a_f = "0";
            String fmajor_cw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whiteah","0");
            String fmajor_cw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorCool Whiteam","0");
            txData[12] = (byte) Integer.parseInt(fmajor_cw_a_f);
            txData[13] = (byte) Integer.parseInt(fmajor_cw_a_h);
            txData[14] = (byte) Integer.parseInt(fmajor_cw_a_m);

            txData[15] = 0x02;
            String fmajor_ws_gd_f = "0";
            String fmajor_ws_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgdh","0");
            String fmajor_ws_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgdm","0");
            txData[16] = (byte) Integer.parseInt(fmajor_ws_gd_f);
            txData[17] = (byte) Integer.parseInt(fmajor_ws_gd_h);
            txData[18] = (byte) Integer.parseInt(fmajor_ws_gd_m);

            String fmajor_ws_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumf2","0");
            String fmajor_ws_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgh","0");
            String fmajor_ws_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgm","0");
            txData[19] = (byte) Integer.parseInt(fmajor_ws_g_f);
            txData[20] = (byte) Integer.parseInt(fmajor_ws_g_h);
            txData[21] = (byte) Integer.parseInt(fmajor_ws_g_m);

            String fmajor_ws_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumf3","0");
            String fmajor_ws_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgbh","0");
            String fmajor_ws_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumgbm","0");
            txData[22] = (byte) Integer.parseInt(fmajor_ws_gb_f);
            txData[23] = (byte) Integer.parseInt(fmajor_ws_gb_h);
            txData[24] = (byte) Integer.parseInt(fmajor_ws_gb_m);

            String fmajor_ws_a_f = "0";
            String fmajor_ws_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumah","0");
            String fmajor_ws_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmajorWide Spectrumam","0");
            txData[25] = (byte) Integer.parseInt(fmajor_ws_a_f);
            txData[26] = (byte) Integer.parseInt(fmajor_ws_a_h);
            txData[27] = (byte) Integer.parseInt(fmajor_ws_a_m);

            for (int i = 28; i <54; i++){
                txData[i] = 0;
            }

            txData[54] = 0x66;

        }else if (model.equals("smajor")){
            txData[0] = 0x65;
            txData[1] = 0x02;
            txData[2] = 0x01;
            String smajor_db_gd_f = "0";
            String smajor_db_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegdh","0");
            String smajor_db_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegdm","0");
            txData[3] = (byte) Integer.parseInt(smajor_db_gd_f);
            txData[4] = (byte) Integer.parseInt(smajor_db_gd_h);
            txData[5] = (byte) Integer.parseInt(smajor_db_gd_m);
            String smajor_db_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluef2","0");
            String smajor_db_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegh","0");
            String smajor_db_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegm","0");
            txData[6] = (byte) Integer.parseInt(smajor_db_g_f);
            txData[7] = (byte) Integer.parseInt(smajor_db_g_h);
            txData[8] = (byte) Integer.parseInt(smajor_db_g_m);
            String smajor_db_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluef3","0");
            String smajor_db_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegbh","0");
            String smajor_db_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Bluegbm","0");
            txData[9] = (byte) Integer.parseInt(smajor_db_gb_f);
            txData[10] = (byte) Integer.parseInt(smajor_db_gb_h);
            txData[11] = (byte) Integer.parseInt(smajor_db_gb_m);
            String smajor_db_a_f = "0";
            String smajor_db_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Blueah","0");
            String smajor_db_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorDeep Blueam","0");
            txData[12] = (byte) Integer.parseInt(smajor_db_a_f);
            txData[13] = (byte) Integer.parseInt(smajor_db_a_h);
            txData[14] = (byte) Integer.parseInt(smajor_db_a_m);

            txData[15] = 0x02;
            String smajor_as_gd_f = "0";
            String smajor_as_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungdh","0");
            String smajor_as_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungdm","0");
            txData[16] = (byte) Integer.parseInt(smajor_as_gd_f);
            txData[17] = (byte) Integer.parseInt(smajor_as_gd_h);
            txData[18] = (byte) Integer.parseInt(smajor_as_gd_m);
            String smajor_as_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunf2","0");
            String smajor_as_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungh","0");
            String smajor_as_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungm","0");
            txData[19] = (byte) Integer.parseInt(smajor_as_g_f);
            txData[20] = (byte) Integer.parseInt(smajor_as_g_h);
            txData[21] = (byte) Integer.parseInt(smajor_as_g_m);
            String smajor_as_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunf3","0");
            String smajor_as_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungbh","0");
            String smajor_as_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sungbm","0");
            txData[22] = (byte) Integer.parseInt(smajor_as_gb_f);
            txData[23] = (byte) Integer.parseInt(smajor_as_gb_h);
            txData[24] = (byte) Integer.parseInt(smajor_as_gb_m);
            String smajor_as_a_f = "0";
            String smajor_as_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunah","0");
            String smajor_as_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smajorAqua Sunam","0");
            txData[25] = (byte) Integer.parseInt(smajor_as_a_f);
            txData[26] = (byte) Integer.parseInt(smajor_as_a_h);
            txData[27] = (byte) Integer.parseInt(smajor_as_a_m);

            for (int i = 28; i <54; i++){
                txData[i] = 0;
            }

            txData[54] = 0x66;

        }else if (model.equals("fmax")){
            txData[0] = 0x65;
            txData[1] = 0x03;
            txData[2] = 0x01;

            String fmax_cw_gd_f = "0";
            String fmax_cw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegdh","0");
            String fmax_cw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegdm","0");
            txData[3] = (byte) Integer.parseInt(fmax_cw_gd_f);
            txData[4] = (byte) Integer.parseInt(fmax_cw_gd_h);
            txData[5] = (byte) Integer.parseInt(fmax_cw_gd_m);
            String fmax_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitef2","0");
            String fmax_cw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegh","0");
            String fmax_cw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegm","0");
            txData[6] = (byte) Integer.parseInt(fmax_cw_g_f);
            txData[7] = (byte) Integer.parseInt(fmax_cw_g_h);
            txData[8] = (byte) Integer.parseInt(fmax_cw_g_m);
            String fmax_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitef3","0");
            String fmax_cw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegbh","0");
            String fmax_cw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whitegbm","0");
            txData[9] = (byte) Integer.parseInt(fmax_cw_gb_f);
            txData[10] = (byte) Integer.parseInt(fmax_cw_gb_h);
            txData[11] = (byte) Integer.parseInt(fmax_cw_gb_m);
            String fmax_cw_a_f = "0";
            String fmax_cw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whiteah","0");
            String fmax_cw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxCool Whiteam","0");
            txData[12] = (byte) Integer.parseInt(fmax_cw_a_f);
            txData[13] = (byte) Integer.parseInt(fmax_cw_a_h);
            txData[14] = (byte) Integer.parseInt(fmax_cw_a_m);

            txData[15] = 0x02;
            String fmax_fs_gd_f = "0";
            String fmax_fs_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgdh","0");
            String fmax_fs_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgdm","0");
            txData[16] = (byte) Integer.parseInt(fmax_fs_gd_f);
            txData[17] = (byte) Integer.parseInt(fmax_fs_gd_h);
            txData[18] = (byte) Integer.parseInt(fmax_fs_gd_m);
            String fmax_fs_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumf2","0");
            String fmax_fs_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgh","0");
            String fmax_fs_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgm","0");
            txData[19] = (byte) Integer.parseInt(fmax_fs_g_f);
            txData[20] = (byte) Integer.parseInt(fmax_fs_g_h);
            txData[21] = (byte) Integer.parseInt(fmax_fs_g_m);
            String fmax_fs_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumf3","0");
            String fmax_fs_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgbh","0");
            String fmax_fs_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumgbm","0");
            txData[22] = (byte) Integer.parseInt(fmax_fs_gb_f);
            txData[23] = (byte) Integer.parseInt(fmax_fs_gb_h);
            txData[24] = (byte) Integer.parseInt(fmax_fs_gb_m);
            String fmax_fs_a_f = "0";
            String fmax_fs_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumah","0");
            String fmax_fs_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxFull Spectrumam","0");
            txData[25] = (byte) Integer.parseInt(fmax_fs_a_f);
            txData[26] = (byte) Integer.parseInt(fmax_fs_a_h);
            txData[27] = (byte) Integer.parseInt(fmax_fs_a_m);

            txData[28] = 0x03;
            String fmax_rw_gd_f = "0";
            String fmax_rw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegdh","0");
            String fmax_rw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegdm","0");
            txData[29] = (byte) Integer.parseInt(fmax_rw_gd_f);
            txData[30] = (byte) Integer.parseInt(fmax_rw_gd_h);
            txData[31] = (byte) Integer.parseInt(fmax_rw_gd_m);
            String fmax_rw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitef2","0");
            String fmax_rw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegh","0");
            String fmax_rw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegm","0");
            txData[32] = (byte) Integer.parseInt(fmax_rw_g_f);
            txData[33] = (byte) Integer.parseInt(fmax_rw_g_h);
            txData[34] = (byte) Integer.parseInt(fmax_rw_g_m);
            String fmax_rw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitef3","0");
            String fmax_rw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegbh","0");
            String fmax_rw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whitegbm","0");
            txData[35] = (byte) Integer.parseInt(fmax_rw_gb_f);
            txData[36] = (byte) Integer.parseInt(fmax_rw_gb_h);
            txData[37] = (byte) Integer.parseInt(fmax_rw_gb_m);
            String fmax_rw_a_f = "0";
            String fmax_rw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whiteah","0");
            String fmax_rw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxReddish Whiteam","0");
            txData[38] = (byte) Integer.parseInt(fmax_rw_a_f);
            txData[39] = (byte) Integer.parseInt(fmax_rw_a_h);
            txData[40] = (byte) Integer.parseInt(fmax_rw_a_m);

            txData[41] = 0x04;
            String fmax_bw_gd_f = "0";
            String fmax_bw_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegdh","0");
            String fmax_bw_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegdm","0");
            txData[42] = (byte) Integer.parseInt(fmax_bw_gd_f);
            txData[43] = (byte) Integer.parseInt(fmax_bw_gd_h);
            txData[44] = (byte) Integer.parseInt(fmax_bw_gd_m);
            String fmax_bw_g_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitef2","0");
            String fmax_bw_g_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegh","0");
            String fmax_bw_g_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegm","0");
            txData[45] = (byte) Integer.parseInt(fmax_bw_g_f);
            txData[46] = (byte) Integer.parseInt(fmax_bw_g_h);
            txData[47] = (byte) Integer.parseInt(fmax_bw_g_m);
            String fmax_bw_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitef3","0");
            String fmax_bw_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegbh","0");
            String fmax_bw_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whitegbm","0");
            txData[48] = (byte) Integer.parseInt(fmax_bw_gb_f);
            txData[49] = (byte) Integer.parseInt(fmax_bw_gb_h);
            txData[50] = (byte) Integer.parseInt(fmax_bw_gb_m);
            String fmax_bw_a_f = "0";
            String fmax_bw_a_h =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whiteah","0");
            String fmax_bw_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxBlueish Whiteam","0");
            txData[51] = (byte) Integer.parseInt(fmax_bw_a_f);
            txData[52] = (byte) Integer.parseInt(fmax_bw_a_h);
            txData[53] = (byte) Integer.parseInt(fmax_bw_a_m);

            txData[54] = 0x66;
        }else if (model.equals("smax")){
            txData[0] = 0x65;
            txData[1] = 0x04;
            txData[2] = 0x01;

            String smax_db_gd_f = "0";
            String smax_db_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegdh","0");
            String smax_db_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegdm","0");
            txData[3] = (byte) Integer.parseInt(smax_db_gd_f);
            txData[4] = (byte) Integer.parseInt(smax_db_gd_h);
            txData[5] = (byte) Integer.parseInt(smax_db_gd_m);
            String smax_db_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluef2","0");
            String smax_db_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegh","0");
            String smax_db_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegm","0");
            txData[6] = (byte) Integer.parseInt(smax_db_g_f);
            txData[7] = (byte) Integer.parseInt(smax_db_g_h);
            txData[8] = (byte) Integer.parseInt(smax_db_g_m);
            String smax_db_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluef3","0");
            String smax_db_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegbh","0");
            String smax_db_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Bluegbm","0");
            txData[9] = (byte) Integer.parseInt(smax_db_gb_f);
            txData[10] = (byte) Integer.parseInt(smax_db_gb_h);
            txData[11] = (byte) Integer.parseInt(smax_db_gb_m);
            String smax_db_a_f = "0";
            String smax_db_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Blueah","0");
            String smax_db_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxDeep Blueam","0");
            txData[12] = (byte) Integer.parseInt(smax_db_a_f);
            txData[13] = (byte) Integer.parseInt(smax_db_a_h);
            txData[14] = (byte) Integer.parseInt(smax_db_a_m);

            txData[15] = 0x02;
            String smax_as_gd_f = "0";
            String smax_as_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungdh","0");
            String smax_as_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungdm","0");
            txData[16] = (byte) Integer.parseInt(smax_as_gd_f);
            txData[17] = (byte) Integer.parseInt(smax_as_gd_h);
            txData[18] = (byte) Integer.parseInt(smax_as_gd_m);
            String smax_as_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunf2","0");
            String smax_as_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungh","0");
            String smax_as_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungm","0");
            txData[19] = (byte) Integer.parseInt(smax_as_g_f);
            txData[20] = (byte) Integer.parseInt(smax_as_g_h);
            txData[21] = (byte) Integer.parseInt(smax_as_g_m);
            String smax_as_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunf3","0");
            String smax_as_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungbh","0");
            String smax_as_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sungbm","0");
            txData[22] = (byte) Integer.parseInt(smax_as_gb_f);
            txData[23] = (byte) Integer.parseInt(smax_as_gb_h);
            txData[24] = (byte) Integer.parseInt(smax_as_gb_m);
            String smax_as_a_f = "0";
            String smax_as_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunah","0");
            String smax_as_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxAqua Sunam","0");
            txData[25] = (byte) Integer.parseInt(smax_as_a_f);
            txData[26] = (byte) Integer.parseInt(smax_as_a_h);
            txData[27] = (byte) Integer.parseInt(smax_as_a_m);

            txData[28] = 0x03;
            String smax_m_gd_f = "0";
            String smax_m_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagdh","0");
            String smax_m_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagdm","0");
            txData[29] = (byte) Integer.parseInt(smax_m_gd_f);
            txData[30] = (byte) Integer.parseInt(smax_m_gd_h);
            txData[31] = (byte) Integer.parseInt(smax_m_gd_m);
            String smax_m_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentaf2","0");
            String smax_m_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagh","0");
            String smax_m_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagm","0");
            txData[32] = (byte) Integer.parseInt(smax_m_g_f);
            txData[33] = (byte) Integer.parseInt(smax_m_g_h);
            txData[34] = (byte) Integer.parseInt(smax_m_g_m);
            String smax_m_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentaf3","0");
            String smax_m_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagbh","0");
            String smax_m_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentagbm","0");
            txData[35] = (byte) Integer.parseInt(smax_m_gb_f);
            txData[36] = (byte) Integer.parseInt(smax_m_gb_h);
            txData[37] = (byte) Integer.parseInt(smax_m_gb_m);
            String smax_m_a_f = "0";
            String smax_m_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxMagentaah","0");
            String smax_m_a_m =localDataManager.getSharedPreference(getApplicationContext(),"fmaxMagentaam","0");
            txData[38] = (byte) Integer.parseInt(smax_m_a_f);
            txData[39] = (byte) Integer.parseInt(smax_m_a_h);
            txData[40] = (byte) Integer.parseInt(smax_m_a_m);

            txData[41] = 0x04;
            String smax_sb_gd_f = "0";
            String smax_sb_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegdh","0");
            String smax_sb_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegdm","0");
            txData[42] = (byte) Integer.parseInt(smax_sb_gd_f);
            txData[43] = (byte) Integer.parseInt(smax_sb_gd_h);
            txData[44] = (byte) Integer.parseInt(smax_sb_gd_m);
            String smax_sb_g_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluef2","0");
            String smax_sb_g_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegh","0");
            String smax_sb_g_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegm","0");
            txData[45] = (byte) Integer.parseInt(smax_sb_g_f);
            txData[46] = (byte) Integer.parseInt(smax_sb_g_h);
            txData[47] = (byte) Integer.parseInt(smax_sb_g_m);
            String smax_sb_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluef3","0");
            String smax_sb_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegbh","0");
            String smax_sb_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Bluegbm","0");
            txData[48] = (byte) Integer.parseInt(smax_sb_gb_f);
            txData[49] = (byte) Integer.parseInt(smax_sb_gb_h);
            txData[50] = (byte) Integer.parseInt(smax_sb_gb_m);
            String smax_sb_a_f = "0";
            String smax_sb_a_h =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Blueah","0");
            String smax_sb_a_m =localDataManager.getSharedPreference(getApplicationContext(),"smaxSky Blueam","0");
            txData[51] = (byte) Integer.parseInt(smax_sb_a_f);
            txData[52] = (byte) Integer.parseInt(smax_sb_a_h);
            txData[53] = (byte) Integer.parseInt(smax_sb_a_m);

            txData[54] = 0x66;
        }else if (model.equals("manual")){
            txData[0] = 0x65;
            txData[1] = 0x05;
            txData[2] = 0x01;

            String manual_c1_gd_f = "0";
            String manual_c1_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gdh","0");
            String manual_c1_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gdm","0");
            txData[3] = (byte) Integer.parseInt(manual_c1_gd_f);
            txData[4] = (byte) Integer.parseInt(manual_c1_gd_h);
            txData[5] = (byte) Integer.parseInt(manual_c1_gd_m);
            String manual_c1_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1f2","0");
            String manual_c1_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gh","0");
            String manual_c1_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gm","0");
            txData[6] = (byte) Integer.parseInt(manual_c1_g_f);
            txData[7] = (byte) Integer.parseInt(manual_c1_g_h);
            txData[8] = (byte) Integer.parseInt(manual_c1_g_m);
            String manual_c1_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1f3","0");
            String manual_c1_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gbh","0");
            String manual_c1_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1gbm","0");
            txData[9] = (byte) Integer.parseInt(manual_c1_gb_f);
            txData[10] = (byte) Integer.parseInt(manual_c1_gb_h);
            txData[11] = (byte) Integer.parseInt(manual_c1_gb_m);
            String manual_c1_a_f = "0";
            String manual_c1_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1ah","0");
            String manual_c1_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 1am","0");
            txData[12] = (byte) Integer.parseInt(manual_c1_a_f);
            txData[13] = (byte) Integer.parseInt(manual_c1_a_h);
            txData[14] = (byte) Integer.parseInt(manual_c1_a_m);

            txData[15] = 0x02;
            String manual_c2_gd_f = "0";
            String manual_c2_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gdh","0");
            String manual_c2_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gdm","0");
            txData[16] = (byte) Integer.parseInt(manual_c2_gd_f);
            txData[17] = (byte) Integer.parseInt(manual_c2_gd_h);
            txData[18] = (byte) Integer.parseInt(manual_c2_gd_m);
            String manual_c2_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2f2","0");
            String manual_c2_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gh","0");
            String manual_c2_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gm","0");
            txData[19] = (byte) Integer.parseInt(manual_c2_g_f);
            txData[20] = (byte) Integer.parseInt(manual_c2_g_h);
            txData[21] = (byte) Integer.parseInt(manual_c2_g_m);
            String manual_c2_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2f3","0");
            String manual_c2_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gbh","0");
            String manual_c2_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gbm","0");
            txData[22] = (byte) Integer.parseInt(manual_c2_gb_f);
            txData[23] = (byte) Integer.parseInt(manual_c2_gb_h);
            txData[24] = (byte) Integer.parseInt(manual_c2_gb_m);
            String manual_c2_a_f = "0";
            String manual_c2_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2ah","0");
            String manual_c2_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2am","0");
            txData[25] = (byte) Integer.parseInt(manual_c2_a_f);
            txData[26] = (byte) Integer.parseInt(manual_c2_a_h);
            txData[27] = (byte) Integer.parseInt(manual_c2_a_m);

            txData[28] = 0x03;
            String manual_c3_gd_f = "0";
            String manual_c3_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gdh","0");
            String manual_c3_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 2gdm","0");
            txData[29] = (byte) Integer.parseInt(manual_c3_gd_f);
            txData[30] = (byte) Integer.parseInt(manual_c3_gd_h);
            txData[31] = (byte) Integer.parseInt(manual_c3_gd_m);
            String manual_c3_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3f2","0");
            String manual_c3_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gh","0");
            String manual_c3_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gm","0");
            txData[32] = (byte) Integer.parseInt(manual_c3_g_f);
            txData[33] = (byte) Integer.parseInt(manual_c3_g_h);
            txData[34] = (byte) Integer.parseInt(manual_c3_g_m);
            String manual_c3_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3f3","0");
            String manual_c3_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gbh","0");
            String manual_c3_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3gbm","0");
            txData[35] = (byte) Integer.parseInt(manual_c3_gb_f);
            txData[36] = (byte) Integer.parseInt(manual_c3_gb_h);
            txData[37] = (byte) Integer.parseInt(manual_c3_gb_m);
            String manual_c3_a_f = "0";
            String manual_c3_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3ah","0");
            String manual_c3_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 3am","0");
            txData[38] = (byte) Integer.parseInt(manual_c3_a_f);
            txData[39] = (byte) Integer.parseInt(manual_c3_a_h);
            txData[40] = (byte) Integer.parseInt(manual_c3_a_m);

            txData[41] = 0x04;
            String manual_c4_gd_f = "0";
            String manual_c4_gd_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gdh","0");
            String manual_c4_gd_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gdm","0");
            txData[42] = (byte) Integer.parseInt(manual_c4_gd_f);
            txData[43] = (byte) Integer.parseInt(manual_c4_gd_h);
            txData[44] = (byte) Integer.parseInt(manual_c4_gd_m);
            String manual_c4_g_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4f2","0");
            String manual_c4_g_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gh","0");
            String manual_c4_g_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gm","0");
            txData[45] = (byte) Integer.parseInt(manual_c4_g_f);
            txData[46] = (byte) Integer.parseInt(manual_c4_g_h);
            txData[47] = (byte) Integer.parseInt(manual_c4_g_m);
            String manual_c4_gb_f = localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4f3","0");
            String manual_c4_gb_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gbh","0");
            String manual_c4_gb_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4gbm","0");
            txData[48] = (byte) Integer.parseInt(manual_c4_gb_f);
            txData[49] = (byte) Integer.parseInt(manual_c4_gb_h);
            txData[50] = (byte) Integer.parseInt(manual_c4_gb_m);
            String manual_c4_a_f = "0";
            String manual_c4_a_h =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4ah","0");
            String manual_c4_a_m =localDataManager.getSharedPreference(getApplicationContext(),"manualChannel 4am","0");
            txData[51] = (byte) Integer.parseInt(manual_c4_a_f);
            txData[52] = (byte) Integer.parseInt(manual_c4_a_h);
            txData[53] = (byte) Integer.parseInt(manual_c4_a_m);

            txData[54] = 0x66;
        }else if (model.equals("test")){
            txData[0] = 0x65;
            txData[1] = 0x06;
            txData[2] = 0x05;
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

            txData[54] = 0x66;

        }

        // Datalar gönderiliyor
        try {
            sendReceive.write(txData);
            Toast.makeText(getApplicationContext(),"Veriler gönderildi",Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Veriler gönderildi.");
            Message message = Message.obtain();
            message.what = STATE_MESSAGE_ACK_WAIT;
            handler.sendMessage(message);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }


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

        Log.e(TAG,"Bluetooth soket kapatıldı");
    }

    private String getMessage(Message msg){
        byte[] readBuffer = (byte[]) msg.obj;
        return new String(readBuffer,0,msg.arg1);
    }

}
