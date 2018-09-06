package com.ndc.bus.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

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

    import javax.inject.Inject;

        public class StationActivity extends BaseActivity {
            @Inject
            BusDatabaseClient busDatabaseClient;
    private ActivityStationBinding binding;
    private ArrayList<Station> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusDatabase busDatabase = busDatabaseClient.getBusDatabase();
        stationList = busDatabase.stationDAO().getAllStations();
    }

    @Override
    public void initSettings(){
        super.initSettings();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);

        StationAdapter stationAdapter = new StationAdapter(this, stationList, new StationRecyclerViewClickListener() {
            @Override
            public void onItemClick(Station station) {
                setDestStation(station);
            }
        });

        binding.stationRv.setAdapter(stationAdapter);
    }

    private void setDestStation(Station station){
        Dlog.i(station.getStNm());
        //FIXME : need to make ArrivalNotificationService and give gps data of dest station.
 /*       Intent intent = new Intent(
                getApplicationContext(),
                ArrivalNotificationService.class);
        startService(intent);*/
    }
}
