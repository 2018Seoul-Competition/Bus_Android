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

    public StationAdapter(ArrayList<StationModel> stationModelList, StationRecyclerViewClickListener listener) {
        this.stationModelList = stationModelList;
        this.arrivalItemLists = new ArrayList<>();
        this.listener = listener;
        this.busPosList = new ArrayList<>();
        //retrieveBusPos();
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
        holder.binding.stationMarker.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_active, R.color.colorPrimary));
        holder.bind(stationModel, position+1, listener);
    }

    public List<ArrivalItemList> getArrivalItemLists() {
        return arrivalItemLists;
    }

    public void setArrivalItemLists(List<ArrivalItemList> arrivalItemLists) {
        this.arrivalItemLists = arrivalItemLists;
        retrieveBusPos();
    }

    private void retrieveBusPos() {
        for (int i = 0; i < arrivalItemLists.size(); i++) {
            int busPos = arrivalItemLists.get(i).getSectOrd();
            busPosList.add(busPos);
        }
    }

    @Override
    public int getItemCount() {
        return stationModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private StationRowBinding binding;

        MyViewHolder(StationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(final StationModel stationModel, int position, final StationRecyclerViewClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(stationModel);
                }
            });

            if (BaseApplication.LAN_MODE.compareTo("KR") == 0) {
                binding.stationNameTv.setText(stationModel.getStation().getStNm());
            } else {
                binding.stationNameTv.setText(stationModel.getStation().getStEngNm() + "(" + stationModel.getStation().getStNm() + ")");
            }

            if(busPosList.contains(position)){
                binding.vehIv.setVisibility(View.VISIBLE);
                int index = busPosList.indexOf(position);
                int nextStTm = arrivalItemLists.get(index).getNextStTm();
                int seconds = nextStTm % 60;
                int minutes = nextStTm / 60;
                if(BaseApplication.LAN_MODE.compareTo("EN") == 0){
                    binding.stationDateTv.setText("Arrive after " + minutes + "minutes " + seconds + "seconds");
                }
                else
                    binding.stationDateTv.setText("도착 " + minutes + "분 " + seconds + "초 전");
            }else{
                binding.vehIv.setVisibility(View.INVISIBLE);
                binding.stationDateTv.setText("");
            }

        }
    }

}