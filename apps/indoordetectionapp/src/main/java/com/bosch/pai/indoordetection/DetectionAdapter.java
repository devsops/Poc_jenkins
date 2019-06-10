package com.bosch.pai.indoordetection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DetectionAdapter extends RecyclerView.Adapter<DetectionAdapter.DetectionViewHolder> {

    private List<String> detectionInfoList;

    public DetectionAdapter(List<String> detectionInfoList) {
        this.detectionInfoList = detectionInfoList;
    }

    @Override
    public DetectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detection_item_layout, parent, false);
        return new DetectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetectionViewHolder holder, int position) {
        final String item = detectionInfoList.get(position);

        holder.detectionItem.setText(item + " \n ");
    }

    @Override
    public int getItemCount() {
        return detectionInfoList.size();
    }

    public class DetectionViewHolder extends RecyclerView.ViewHolder {


        TextView detectionItem;

        private DetectionViewHolder(View itemView) {
            super(itemView);

            detectionItem = itemView.findViewById(R.id.detection_item);
        }
    }
}
