package com.bosch.pai.ipsadminapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.models.SiteAdapterModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<SiteAdapterModel> siteAdapterModelList;
    private OnSiteItemClickListener listener;

    public StoreAdapter(List<SiteAdapterModel> siteAdapterModels, OnSiteItemClickListener listener) {
        this.siteAdapterModelList = siteAdapterModels; //NOSONAR
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.site_rv_item, parent, false);

        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        final SiteAdapterModel siteAdapterModel = siteAdapterModelList.get(position);

        holder.siteNameTextview.setText(siteAdapterModel.getSiteName());

        holder.locationCount.setText(String.valueOf(siteAdapterModel.getLocationCount()));
    }

    @Override
    public int getItemCount() {
        return siteAdapterModelList.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sitename)
        protected TextView siteNameTextview;

        @BindView(R.id.locationcount)
        protected TextView locationCount;

        @BindView(R.id.gotositelocations)
        protected ImageButton gotositelocations;

        private StoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.findViewById(R.id.merge_signal).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.edit_rssi).setVisibility(View.GONE);
            itemView.findViewById(R.id.uploadsite).setVisibility(View.GONE);
            itemView.findViewById(R.id.siteconfiguration).setVisibility(View.GONE);
            itemView.findViewById(R.id.store_config).setVisibility(View.GONE);
            itemView.findViewById(R.id.deletesite).setVisibility(View.GONE);
        }

        @OnClick(R.id.merge_signal)
        public void onSiteSignalMergeClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onSiteSignalMergeClick(position);
            }
        }


        @OnClick(R.id.uploadsite)
        public void onSiteUploadsiteClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onSiteUploadsiteClick(position);
            }
        }


        @OnClick(R.id.siteconfiguration)
        public void onSiteConfigurationClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onSiteConfigurationClick(position);
            }
        }

        @OnClick(R.id.store_config)
        public void onStoreConfigurationClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onStoreConfigurationClick(position);
            }
        }

        @OnClick(R.id.deletesite)
        public void onSiteDeleteClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onSiteDeleteClick(position);
            }
        }

        @OnClick(R.id.gotositelocations)
        public void onSiteClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onSiteClick(position);
            }
        }

        @OnClick(R.id.edit_rssi)
        public void onEditRssiClick() {
            final int position = getAdapterPosition();
            if (position > -1 && position < getItemCount()) {
                listener.onEditRssiClick(position);
            }
        }
    }

    public interface OnSiteItemClickListener {

        void onSiteUploadsiteClick(int position);

        void onSiteConfigurationClick(int position);

        void onSiteSignalMergeClick(int position);

        void onSiteDeleteClick(int position);

        void onSiteClick(int position);

        void onStoreConfigurationClick(int position);

        void onEditRssiClick(int position);
    }
}
