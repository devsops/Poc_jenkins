package com.bosch.pai.ipsadminapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.models.LocationAdapterModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LocationAdapterModel> locationAdapterModels;
    private OnLocationItemClickListener listener;


    public LocationAdapter(List<LocationAdapterModel> siteAdapterModels,
                           OnLocationItemClickListener listener) {
        this.locationAdapterModels = siteAdapterModels; //NOSONAR
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_rv_item, parent, false);

        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        final LocationAdapterModel locationAdapterModel = locationAdapterModels.get(position);

        final String locationName = locationAdapterModel.getLocationName();

        holder.locationNameTv.setText(locationName);
        String temp = "" + locationAdapterModel.getSensorType().name();
        holder.sensortype.setText(temp);
    }

    @Override
    public int getItemCount() {
        return locationAdapterModels.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sensortype)
        protected TextView sensortype;

        @BindView(R.id.location_name_for_rv_item)
        protected TextView locationNameTv;

        @BindView(R.id.ssl_baymap_type_tv)
        protected TextView sslBaymapTypeTv;

        @BindView(R.id.spar_baymap_tv)
        protected TextView sparBaymapTv;

        private LocationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.renamelocation)
        public void onRenameLocationClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onLocationRenameItemClick(position);
            }
        }

        @OnClick(R.id.gotolocationBaymap)
        public void onLocationItemClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onLocationItemClick(position);
            }
        }

        @OnClick(R.id.deletelocation)
        public void onLocationDeleteItemClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onLocationDeleteItemClick(position);
            }
        }

        @OnClick(R.id.retainlocation)
        public void onLocationRetrainItemClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onLocationRetrainItemClick(position);
            }
        }

        @OnClick(R.id.uploadlocation)
        public void onLocationUploadItemClicked() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onLocationUploadItemClicked(position);
            }
        }
    }

    public interface OnLocationItemClickListener {

        void onLocationRenameItemClick(int position);

        void onLocationRetrainItemClick(int position);

        void onLocationDeleteItemClick(int position);

        void onLocationItemClick(int position);

        void onLocationUploadItemClicked(int position);

    }
}
