package com.ndc.bus.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;

import com.ndc.bus.Arrival.ArrivalServiceResult;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Service.ArrivalNotificationService;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityMainBinding;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.speech.tts.TextToSpeech.ERROR;


public class MainActivity extends BaseActivity implements TextToSpeech.OnInitListener{

    //private ActivityMainBinding binding;
    private TextToSpeech tts;

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
        //this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //binding.setActivity(this);

        //FIXME : origin code
//        retrieveBusInfo();

        //FIXME : test code for GPS
        ArrivalNotificationService service = new ArrivalNotificationService();
        Intent intent = new Intent(
                getApplicationContext(),
                ArrivalNotificationService.class);
        startService(intent);
    }

    public void retrieveBusInfo(){

        //get vehId from QrScanActivity
        BaseApplication baseApplication = (BaseApplication)getApplication();
        String serviceKey = baseApplication.getKey();
        String vehId = getIntent().getStringExtra("vehId");

        Call<ArrivalServiceResult> call =  RetrofitClient.getInstance().getService().getBusPosByVehId(serviceKey, vehId);
        call.enqueue(new Callback<ArrivalServiceResult>() {
            @Override
            public void onResponse(Call<ArrivalServiceResult> call, Response<ArrivalServiceResult> response) {
                // you  will get the reponse in the response parameter
                if(response.isSuccessful()) {
                    Dlog.i(response.body().getArrivalMsgHeader().getHeaderMsg());
                }else {
                    int statusCode  = response.code();
                }
            }

            @Override
            public void onFailure(Call<ArrivalServiceResult> call, Throwable t) {
                Dlog.e(t.getMessage());
            }
        });

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
