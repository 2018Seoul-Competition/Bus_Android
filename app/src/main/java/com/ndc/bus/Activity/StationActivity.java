package com.ndc.bus.Activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ndc.bus.Activity.Adapter.StationAdapter;
import com.ndc.bus.Database.BusDatabase;
import com.ndc.bus.Database.BusDatabaseClient;
import com.ndc.bus.Database.BusDatabaseModule;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.R;
import com.ndc.bus.Service.ArrivalNotificationService;
import com.ndc.bus.Station.Station;

import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityStationBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.ContributesAndroidInjector;

public class StationActivity extends BaseActivity {
    @Inject
    BusDatabaseClient busDatabaseClient;
    private ActivityStationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initSettings() {
        super.initSettings();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);

        SelectDatabaseTask selectTask = new SelectDatabaseTask();
        selectTask.execute(getApplicationContext());

    }

    private void setDestStation(Station station) {
        Dlog.i(station.getStNm());
        //FIXME : need to make ArrivalNotificationService and give gps data of dest station.
 /*       Intent intent = new Intent(
                getApplicationContext(),
                ArrivalNotificationService.class);
        startService(intent);*/
    }

    private class SelectDatabaseTask extends AsyncTask<Context, Void, List<Station>> {
        private Context context;

        @Override
        protected List<Station> doInBackground(Context... contexts) {
            this.context = contexts[0];
            List<Station> stationList = busDatabaseClient.getBusDatabase().stationDAO().getAllStations();
            return stationList;
        }

        @Override
        protected void onPostExecute(List<Station> stationList) {
            super.onPostExecute(stationList);
            StationAdapter stationAdapter = new StationAdapter(context, stationList, new StationRecyclerViewClickListener() {
                @Override
                public void onItemClick(Station station) {
                    setDestStation(station);
                }
            });
            binding.stationRv.setAdapter(stationAdapter);
        }

    }


}
