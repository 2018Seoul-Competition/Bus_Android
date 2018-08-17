package com.ndc.bus.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.ndc.bus.R;

import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class QrScanActivity extends AppCompatActivity {
    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //scan option
        qrScan.setPrompt("QR코드를 찍어주세요");
        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
        qrScan.setOrientationLocked(false);

        qrScan.initiateScan();
    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(QrScanActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(QrScanActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}