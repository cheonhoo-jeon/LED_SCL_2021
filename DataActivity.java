package kr.co.biomed.Activity;

import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.biomed.Adapter.DataAdapter;
import kr.co.biomed.Data.Data;
import kr.co.biomed.Data.MachineData;
import kr.co.biomed.Interface.DataDeviceFinish;
import kr.co.biomed.R;
import kr.co.biomed.View.DataView;

public class DataActivity extends AppCompatActivity implements View.OnClickListener, DataDeviceFinish {
    View xButton, mainLayout, lensOptionSetButton, frequencyButton, powerSetButton, clearButton;
    CheckBox iopResToggle, led_on, time_0, time_1, ddsSel_0, ddsSel_1, ddsSel_2;
    RadioButton mhz315, mhz345, mhz372, mhz390, mhz418, mhz433_62, mhz433_92;
    RadioButton pmax0, pmax3, pmax6, pmax10;
    ScrollView scrollView;
    DataView dataView;
    ArrayList<MachineData> receviceData = new ArrayList<>();
    RecyclerView dataRecyclerView;
    DataAdapter dataAdapter;
    TextView rfRMSText, deviceNameText;
    int rf_rms = 0;

    String folderName, fileName;
    final String TAG = "DATA_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        
        getID();
        setListener();
        setRecycler();
        setUI();
        setBle();
        setSave();
        setWakeUp();
    }

    private void setWakeUp() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setSave() {
        String now = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        fileName = now + ".txt";
        folderName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/BiomedLog";

        String date  =new SimpleDateFormat("HH:mm:ss").format(new Date());
        String content = String.format("연결 - %s -> %s\n",date, Data.bleDevice.getName());
        writeTextFile(folderName, fileName, content);
    }

    private void writeTextFile(String folderName, String fileName, String data){
        File dir = new File(folderName);

        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            FileOutputStream fos = new FileOutputStream(folderName+"/"+fileName, true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dataAdapter = new DataAdapter();
        dataAdapter.setList(receviceData);
        dataRecyclerView.setLayoutManager(layoutManager);
        dataRecyclerView.setAdapter(dataAdapter);
    }

    private void setBle() {
        Data.bleWriteCallback = new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {

            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        };

        Data.bleNotifyCallback = new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                if(data.length == 4){
                    if(data[0] == (byte)0x2f && data[1] == (byte)0xff){
                        int num = 0;
                        int mask = 0xff;
                        num = mask & data[3];
                        num += (mask & data[2]) << 8;
                        String date  =new SimpleDateFormat("HH:mm:ss").format(new Date());
                        receviceData.add(0, new MachineData(num, date));
                        dataView.setDatas(receviceData);
                        dataView.invalidate();
                        dataAdapter.setList(receviceData);
                        dataAdapter.notifyDataSetChanged();
                        String content = String.format("RX - %s -> %02X %02X %02X %02X\n",date, data[0], data[1], data[2], data[3]);
                        writeTextFile(folderName, fileName, content);
                    }else if(data[0] == (byte)0xc6 && data[0] == data[1]){
                        int num = 0;
                        int mask = 0xff;
                        num = mask & data[3];
                        num += (mask & data[2]) << 8;
                        String date  =new SimpleDateFormat("HH:mm:ss").format(new Date());
                        rf_rms = num;
                        rfRMSText.setText(num + "");
                        String content = String.format("RX - %s -> %02X %02X %02X %02X\n",date, data[0], data[1], data[2], data[3]);
                        writeTextFile(folderName, fileName, content);
                    }
                }
            }
        };

        BleManager.getInstance().notify(Data.bleDevice, Data.device_uuid_service, Data.device_uuid_character, Data.bleNotifyCallback);
    }

    private void setUI() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, scrollView.getWidth() + " ; " + scrollView.getHeight());

                ViewGroup.LayoutParams layoutParams = mainLayout.getLayoutParams();
                layoutParams.width = scrollView.getWidth();
                layoutParams.height = scrollView.getHeight();

                mainLayout.setLayoutParams(layoutParams);
            }
        });

        deviceNameText.setText(Data.bleDevice.getName());
    }

    private void setListener() {
        xButton.setOnClickListener(this);
        lensOptionSetButton.setOnClickListener(this);
        frequencyButton.setOnClickListener(this);
        powerSetButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    private void getID() {
        xButton = findViewById(R.id.x_button);
        scrollView = findViewById(R.id.scroll_view);
        mainLayout = findViewById(R.id.main_layout);

        lensOptionSetButton = findViewById(R.id.lens_option_set_button);

        iopResToggle = findViewById(R.id.iop_res_toggle);
        led_on = findViewById(R.id.led_on);
        time_0 = findViewById(R.id.time_0);
        time_1 = findViewById(R.id.time_1);
        ddsSel_0 = findViewById(R.id.dds_sel_0);
        ddsSel_1 = findViewById(R.id.dds_sel_1);
        ddsSel_2 = findViewById(R.id.dds_sel_2);

        frequencyButton = findViewById(R.id.frequency_selection_button);

        mhz315 = findViewById(R.id.mhz_315);
        mhz345 = findViewById(R.id.mhz_345);
        mhz372 = findViewById(R.id.mhz_372);
        mhz390 = findViewById(R.id.mhz_390);
        mhz418 = findViewById(R.id.mhz_418);
        mhz433_62 = findViewById(R.id.mhz_433_62);
        mhz433_92 = findViewById(R.id.mhz_433_92);

        powerSetButton = findViewById(R.id.power_set_button);

        pmax0 = findViewById(R.id.pmax_0);
        pmax3 = findViewById(R.id.pmax_3);
        pmax6 = findViewById(R.id.pmax_6);
        pmax10 = findViewById(R.id.pmax_10);

        dataView = findViewById(R.id.data_view);
        dataRecyclerView = findViewById(R.id.recycler_view);

        rfRMSText = findViewById(R.id.rf_rms_text);
        clearButton = findViewById(R.id.clear_button);

        deviceNameText = findViewById(R.id.device_name);

        Data.dataActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnect(Data.bleDevice);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.x_button :
                clickXButton();
                break;
            case R.id.lens_option_set_button :
                clickLensOptionSetButton();
                break;
            case R.id.frequency_selection_button :
                clickFrequencyButton();
                break;
            case R.id.power_set_button :
                clickPowerSetButton();
                break;
            case R.id.clear_button :
                clickClearButton();
                break;
        }
    }

    private void clickClearButton() {
        receviceData = new ArrayList<>();
        dataView.setDatas(receviceData);
        dataView.invalidate();
        dataAdapter.setList(receviceData);
        dataAdapter.notifyDataSetChanged();
    }

    private void clickPowerSetButton() {
        if(pmax0.isChecked()){
            Data.powerData[1] = 0x00;
        }else if(pmax3.isChecked()){
            Data.powerData[1] = 0x01;
        }else if(pmax6.isChecked()){
            Data.powerData[1] = 0x02;
        }else if(pmax10.isChecked()){
            Data.powerData[1] = 0x03;
        }

        BleManager.getInstance().write(Data.bleDevice, Data.device_uuid_service, Data.device_uuid_character, Data.powerData, Data.bleWriteCallback);
        String date  =new SimpleDateFormat("HH:mm:ss").format(new Date());
        String content = String.format("TX - %s -> %02X %02X\n",date, Data.powerData[0], Data.powerData[1]);
        writeTextFile(folderName, fileName, content);
    }

    private void clickFrequencyButton() {
        if(mhz315.isChecked()){
            Data.frequencyData[1] = 0x00;
        }else if(mhz345.isChecked()){
            Data.frequencyData[1] = 0x01;
        }else if(mhz372.isChecked()){
            Data.frequencyData[1] = 0x02;
        }else if(mhz390.isChecked()){
            Data.frequencyData[1] = 0x03;
        }else if(mhz418.isChecked()){
            Data.frequencyData[1] = 0x04;
        }else if(mhz433_62.isChecked()){
            Data.frequencyData[1] = 0x05;
        }else if(mhz433_92.isChecked()){
            Data.frequencyData[1] = 0x06;
        }

        BleManager.getInstance().write(Data.bleDevice, Data.device_uuid_service, Data.device_uuid_character, Data.frequencyData, Data.bleWriteCallback);
        String date  =new SimpleDateFormat("HH:mm:ss").format(new Date());
        String content = String.format("TX - %s -> %02X %02X\n",date, Data.frequencyData[0], Data.frequencyData[1]);
        writeTextFile(folderName, fileName, content);
    }

    private void clickLensOptionSetButton() {
        int data = 0;
        if(iopResToggle.isChecked()){
            data += Data.IOP_RES_TOGGLE;
        }

        if(led_on.isChecked()){
            data += Data.LED_ON;
        }

        if(time_0.isChecked()){
            data += Data.TIME_0;
        }

        if(time_1.isChecked()){
            data += Data.TIME_1;
        }

        if(ddsSel_0.isChecked()){
            data += Data.DDS_SEL_0;
        }

        if(ddsSel_1.isChecked()){
            data += Data.DDS_SEL_1;
        }

        if(ddsSel_2.isChecked()){
            data += Data.DDS_SEL_2;
        }

        Data.lensOptionData[1] = (byte)data;
        BleManager.getInstance().write(Data.bleDevice, Data.device_uuid_service, Data.device_uuid_character, Data.lensOptionData, Data.bleWriteCallback);
        Log.d(TAG, "Lens Option" + Integer.toBinaryString(data));
        String date  =new SimpleDateFormat("HH:mm:ss").format(new Date());
        String content = String.format("TX - %s -> %02X %02X\n",date, Data.lensOptionData[0], Data.lensOptionData[1]);
        writeTextFile(folderName, fileName, content);
    }

    private void clickXButton() {
        finish();
    }

    @Override
    public void activityFinish() {
        clickXButton();
    }
}
