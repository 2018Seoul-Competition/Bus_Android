package com.ndc.bus.Activity.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ndc.bus.BR;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.Station.Station;
import com.ndc.bus.databinding.StationRowBinding;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MyViewHolder> {
    private final StationRecyclerViewClickListener listener;
    private ArrayList<Station> stationList;
    private LayoutInflater inflater;
    private Activity activity;

    public StationAdapter(Activity activity, ArrayList<Station> stationList, StationRecyclerViewClickListener listener) {
        this.activity = activity;
        this.stationList = stationList;
        this.listener = listener;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StationRowBinding binding = StationRowBinding.
                inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(StationAdapter.MyViewHolder holder, final int position) {
        holder.bind(stationList.get(position), listener);
    }

    public void setItem(ArrayList<Station> stationList) {
        if (stationList == null) {
            return;
        }
        this.stationList = stationList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private StationRowBinding binding;

        public MyViewHolder(StationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Station station, final StationRecyclerViewClickListener listener) {
            binding.setVariable(BR.station, station);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(station);
                }
            });
        }
    }
}