package com.ndc.bus.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ndc.bus.BR;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.R;
import com.ndc.bus.Station.StationModel;
import com.ndc.bus.Station.StationStatus;
import com.ndc.bus.Utils.VectorDrawableUtils;
import com.ndc.bus.databinding.StationRowBinding;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MyViewHolder> {
    private final StationRecyclerViewClickListener listener;
    private List<StationModel> stationModelList;
    private Context context;

    public StationAdapter(List<StationModel> stationModelList, StationRecyclerViewClickListener listener) {
        this.stationModelList = stationModelList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        StationRowBinding binding = StationRowBinding.
                inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(StationAdapter.MyViewHolder holder, final int position) {
        StationModel stationModel = stationModelList.get(position);

        holder.bind(stationModel, listener);


        if(stationModel.getStatus() == StationStatus.INACTIVE) {
            holder.binding.stationMarker.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(stationModel.getStatus() == StationStatus.ACTIVE) {
            holder.binding.stationMarker.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.binding.stationMarker.setMarker(ContextCompat.getDrawable(context, R.drawable.ic_marker), ContextCompat.getColor(context, R.color.colorPrimary));
        }

        if(!stationModel.getDate().isEmpty()) {
            holder.binding.stationDateTv.setVisibility(View.VISIBLE);
            holder.binding.stationDateTv.setText(stationModel.getDate());
            //holder.binding.stationDateTv.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
        }
        else
            holder.binding.stationDateTv.setVisibility(View.GONE);

        if(BaseApplication.LAN_MODE.compareTo("KR") == 0)
            holder.binding.stationNameTv.setText(stationModel.getStation().getStNm());
        else
            holder.binding.stationNameTv.setText(stationModel.getStation().getStEngNm() + "(" + stationModel.getStation().getStNm() + ")");

    }

    @Override
    public int getItemCount() {
        return stationModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private StationRowBinding binding;

        MyViewHolder(StationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final StationModel stationModel, final StationRecyclerViewClickListener listener) {
            binding.setVariable(BR.stationModel, stationModel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(stationModel);
                }
            });
        }

        public StationRowBinding getBinding() {
            return binding;
        }
    }
}