package com.ndc.bus.Activity;

import android.databinding.DataBindingUtil;

import com.ndc.bus.Arrival.ArrivalServiceResult;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    public void initVariable(){
        super.initVariable();
        retrieveBusInfo();

/*        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);*/
    }

    public void retrieveBusInfo(){

        /*BaseApplication baseApplication = (BaseApplication)getApplication();
        String serviceKey = baseApplication.getKey();
        String vehId = binding.vehId.getText().toString();*/

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

}
