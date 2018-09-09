package com.ndc.bus.Activity;

import android.content.Intent;

import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.R;

import android.databinding.DataBindingUtil;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.ndc.bus.databinding.ActivityMainBinding;
import java.util.Locale;

import javax.inject.Inject;


public class MainActivity extends BaseActivity implements TextToSpeech.OnInitListener{

    @Inject
    BusDatabaseClient busDatabaseClient;
    private ActivityMainBinding binding;
    private TextToSpeech tts;

    //for back press
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void initSettings(){
        super.initSettings();

        //for gps
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        this.tts = new TextToSpeech(this, this);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    public void retrieveBusData(){
        //busDatabaseClient.getBusDatabase().stationDAO().retrieveStationById();
        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(BaseApplication.VEH_ID, "value");
        startActivity(intent);
    }

    public void gotoQrScanActivity(){
        Intent intent = new Intent(this, StationActivity.class);
        startActivity(intent);
    }

    // 비동기로 speech 출력을 처리한다.
    synchronized private void speechBusInfo(String speechData){
        tts.setPitch(0.9f);         // 음성 톤은을 기본의 0.9로 설정
        tts.setSpeechRate(1.0f);    // 읽는 속도를 기본으로 설정

        // 버전에 따라서 함수를 달리 설정해주어야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(speechData,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(speechData, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onBackPressed(){
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime){
            finishAffinity();
            System.exit(0);
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
        }
    }
}
