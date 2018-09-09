package com.ndc.bus.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.R;
import com.ndc.bus.Route.Route;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

public class QrScanActivity extends BaseActivity {

    @Inject
    BusDatabaseClient busDatabaseClient;

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
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    String appUrl = (String) obj.get("url");
                    String vehNm = (String)obj.get("vehNm");
                    retrieveBusData(vehNm);
                    //String strDistBusId= (String) obj.get("vehNm");
                    //retrieveBusInfo(strDistBusId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void retrieveBusData(String vehNm) throws ExecutionException, InterruptedException {
        RetrieveRouteTask retrieveRouteTask = new RetrieveRouteTask();
        Route route = retrieveRouteTask.execute(vehNm).get();
        if(route != null){
            Intent intent = new Intent(this, StationActivity.class);
            intent.putExtra(BaseApplication.VEH_NM, vehNm);
            startActivity(intent);
        }else{
            Toast.makeText(this,"존재하지 않는 버스입니다!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    public void retrieveBusInfo(String vehId){
        //get vehId from QrScanActivity
        BaseApplication baseApplication = (BaseApplication)getApplication();
        String serviceKey = baseApplication.getKey();

        Call<ArrivalServiceResult> call =  RetrofitClient.getInstance().getService().getBusPosByVehId(serviceKey, vehId);
        call.enqueue(new Callback<ArrivalServiceResult>() {
            @Override
            public void onResponse(Call<ArrivalServiceResult> call, Response<ArrivalServiceResult> response) {
                // you  will get the reponse in the response parameter
                if(response.isSuccessful()) {
                    Dlog.i(response.body().getArrivalMsgHeader().getHeaderMsg());
                    Intent intent = new Intent(QrScanActivity.this, StationActivity.class);
                    intent.putExtra(BaseApplication.VEH_NM, response.body().);
                    startActivity(intent);
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
    */

    private class RetrieveRouteTask extends AsyncTask<String, Void, Route> {
        private String routeNm;

        @Override
        protected Route doInBackground(String... strings) {
            routeNm = strings[0];
            Route route = busDatabaseClient.getBusDatabase().routeDAO().retrieveRouteNmByNm(routeNm);
            return route;
        }

        @Override
        protected void onPostExecute(Route route) {
            super.onPostExecute(route);
        }

    }



}