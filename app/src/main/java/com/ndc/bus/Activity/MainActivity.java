package com.ndc.bus.Activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Network.RetrofitClient;
import com.ndc.bus.R;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityMainBinding;

import java.io.IOException;

import retrofit2.Call;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dlog.i("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    */
    @Override
    public void initVariable(){
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
    }

    public void retrieveBusInfo(){

        BaseApplication baseApplication = (BaseApplication)getApplication();
        String serviceKey = baseApplication.getKey();
        String vehId = binding.vehId.getText().toString();

        Call<String> call =  RetrofitClient.getInstance().getService().getBusPosByVehId(serviceKey, vehId);
        try {
            String busInfo = call.execute().body();
            Dlog.i(busInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
