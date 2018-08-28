package com.ndc.bus.Activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;

import com.ndc.bus.Activity.Adapter.StationAdapter;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.R;
import com.ndc.bus.Station.Station;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.databinding.ActivityStationBinding;

import java.util.ArrayList;

public class StationActivity extends BaseActivity {

    private ActivityStationBinding binding;
    private ArrayList<Station> stationList;


    @Override
    public void initSettings(){
        super.initSettings();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_station);
        binding.setActivity(this);
        stationList = new ArrayList<>();

        Station station = new Station("버스 번호", "정류장 이름");
        stationList.add(station);
        stationList.add(station);
        stationList.add(station);
        stationList.add(station);

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
    }


}
