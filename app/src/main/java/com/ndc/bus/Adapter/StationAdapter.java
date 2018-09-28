package com.ndc.bus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ndc.bus.Arrival.ArrivalItemList;
import com.ndc.bus.Common.BaseApplication;
import com.ndc.bus.Listener.StationRecyclerViewClickListener;
import com.ndc.bus.R;
import com.ndc.bus.Station.StationModel;
import com.ndc.bus.Utils.Dlog;
import com.ndc.bus.Utils.VectorDrawableUtils;
import com.ndc.bus.databinding.StationRowBinding;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MyViewHolder> {
    private final StationRecyclerViewClickListener listener;
    private List<StationModel> stationModelList;
    private List<ArrivalItemList> arrivalItemLists;
    private ArrayList<Integer> busPosList;
    private Context context;

    public StationAdapter(List<StationModel> stationModelList, List<ArrivalItemList> arrivalItemLists, StationRecyclerViewClickListener listener) {
        this.stationModelList = stationModelList;
        this.arrivalItemLists = arrivalItemLists;
        this.listener = listener;
        retrieveBusPos();
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
        holder.bind(stationModel, position+1);
        holder.binding.stationMarker.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_active, R.color.colorPrimary));

        //holder.bind(stationModel, listener);
        //Dlog.e(String.valueOf(position));
        /*
        if (stationModel.getStatus() == StationStatus.INACTIVE) {
            holder.binding.stationMarker.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if (stationModel.getStatus() == StationStatus.ACTIVE) {
            holder.binding.stationMarker.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.binding.stationMarker.setMarker(ContextCompat.getDrawable(context, R.drawable.ic_marker), ContextCompat.getColor(context, R.color.colorPrimary));
        }

        if (!stationModel.getDate().isEmpty() && busPosList.contains(position+1)) {
            holder.binding.stationDateTv.setVisibility(View.VISIBLE);
            int index = busPosList.indexOf(position+1);
            int nextStTm = arrivalItemLists.get(index).getNextStTm();
            Dlog.e(String.valueOf(nextStTm));
            int seconds = nextStTm % 60;
            int minutes = nextStTm / 60;
            holder.binding.stationDateTv.setText("도착 " + minutes + "s분 " + seconds + "초 전");
        } else {
            //holder.binding.stationDateTv.setVisibility(View.GONE);

        }
        if (BaseApplication.LAN_MODE.compareTo("KR") == 0)
            holder.binding.stationNameTv.setText(stationModel.getStation().getStNm());
        else
            holder.binding.stationNameTv.setText(stationModel.getStation().getStEngNm() + "(" + stationModel.getStation().getStNm() + ")");

        if (busPosList.contains(position)) {
            holder.binding.vehIv.setVisibility(View.VISIBLE);
            int height = holder.binding.rowLl.getHeight();
        }
        */

        /*
        if(busPosList.contains(position+1)){
            Dlog.e(String.valueOf(position+1) + busPosList.toString());
            holder.binding.vehIv.setVisibility(View.VISIBLE);
            int index = busPosList.indexOf(position+1);
            int nextStTm = arrivalItemLists.get(index).getNextStTm();
            //Dlog.e(String.valueOf(nextStTm));
            int seconds = nextStTm % 60;
            int minutes = nextStTm / 60;
            holder.binding.stationDateTv.setText("도착 " + minutes + "분 " + seconds + "초 전");
        }
        */
    }


    private void retrieveBusPos() {
        busPosList = new ArrayList<>();
        for (int i = 0; i < arrivalItemLists.size(); i++) {
            int busPos = arrivalItemLists.get(i).getSectOrd();
            busPosList.add(busPos);
        }
    }

    @Override
    public int getItemCount() {
        return stationModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public StationRowBinding binding;

        MyViewHolder(StationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(StationModel stationModel, int position) {
            if (BaseApplication.LAN_MODE.compareTo("KR") == 0) {
                binding.stationNameTv.setText(stationModel.getStation().getStNm());
            } else {
                binding.stationNameTv.setText(stationModel.getStation().getStEngNm() + "(" + stationModel.getStation().getStNm() + ")");
            }

            if(busPosList.contains(position)){
                Dlog.e(String.valueOf(position) + busPosList.toString());
                binding.vehIv.setVisibility(View.VISIBLE);
                int index = busPosList.indexOf(position);
                int nextStTm = arrivalItemLists.get(index).getNextStTm();
                //Dlog.e(String.valueOf(nextStTm));
                int seconds = nextStTm % 60;
                int minutes = nextStTm / 60;
                binding.stationDateTv.setText("도착 " + minutes + "분 " + seconds + "초 전");
            }

        }
    }

        /*
        void bind(final StationModel stationModel, final StationRecyclerViewClickListener listener) {
            binding.setVariable(BR.stationModel, stationModel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(stationModel);
                }
            });
        }

    }
    */
}