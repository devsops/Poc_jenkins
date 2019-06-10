package com.bosch.pai.ipsadminapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sjn8kor on 1/21/2018.
 */

public class DetectionAdapter extends RecyclerView.Adapter<DetectionAdapter.DetectionViewHolder> {

    private List<String> detectionInfoList;

    public DetectionAdapter(List<String> detectionInfoList) {
        this.detectionInfoList = detectionInfoList; //NOSONAR
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

        holder.detectionItem.setText(item);
    }

    @Override
    public int getItemCount() {
        return detectionInfoList.size();
    }

    public class DetectionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.detection_item)
        protected TextView detectionItem;

        private DetectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
