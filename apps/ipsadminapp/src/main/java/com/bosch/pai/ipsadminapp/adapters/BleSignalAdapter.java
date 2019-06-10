package com.bosch.pai.ipsadminapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.models.BleAdapterModel;

import java.util.List;

public class BleSignalAdapter extends BaseAdapter {

    private Context context;
    private List<BleAdapterModel> signalAdapterModels;
    private ISelectedAll isSelectedAll;
    private boolean showRssi;


    public BleSignalAdapter(Context context, List<BleAdapterModel> signalAdapterModels, ISelectedAll isSelectedAll,boolean showRssi) {
        this.context = context;
        this.signalAdapterModels = signalAdapterModels; //NOSONAR
        this.isSelectedAll = isSelectedAll;
        this.showRssi = showRssi;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return signalAdapterModels.size();
    }

    @Override
    public Object getItem(int position) {
        return signalAdapterModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BleSignalAdapter.ViewHolder holder;

        if (convertView == null) {
            holder = new BleSignalAdapter.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.signal_item, null, true);

            holder.signalcheckbox = convertView.findViewById(R.id.signalcheckbox);
            holder.signal = convertView.findViewById(R.id.signal);
            holder.signalImageview = convertView.findViewById(R.id.signalimage);
            holder.signalrssi = convertView.findViewById(R.id.signalrssi);

            if (showRssi) {
                holder.signalrssi.setVisibility(View.VISIBLE);
            }

            holder.signalImageview.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bluetooth_black_24dp));

            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object setValueForTime as a tag to the view
            holder = (BleSignalAdapter.ViewHolder) convertView.getTag();
        }

        final BleAdapterModel signalName =
                signalAdapterModels.get(position);
        holder.signal.setText(signalName.getSourceId());
        holder.signalcheckbox.setChecked(signalName.isSelected());
        holder.signalrssi.setText("" + signalName.getRssi());

        holder.signalcheckbox.setTag(position);
        holder.signalcheckbox.setOnClickListener((v) -> {

            final Integer pos = (Integer) holder.signalcheckbox.getTag();

            if (pos > -1 && pos < signalAdapterModels.size()) {

                if (isSelectedAll != null) {
                    BleAdapterModel adapterModel = signalAdapterModels.get(pos);
                    if (adapterModel.isSelected()) {
                        adapterModel.setSelected(false);
                    } else {
                        adapterModel.setSelected(true);
                    }

                    boolean isAllSelected = true;
                    for (BleAdapterModel signalAdapterModel : signalAdapterModels) {
                        if (!signalAdapterModel.isSelected()) {
                            isAllSelected = false;
                            break;
                        }
                    }
                    isSelectedAll.selectedAll(isAllSelected);
                } else {
                    for (BleAdapterModel adapterModel1 : signalAdapterModels) {
                        adapterModel1.setSelected(false);
                    }

                    BleAdapterModel adapterModel = signalAdapterModels.get(pos);
                    if (adapterModel.isSelected()) {
                        adapterModel.setSelected(false);
                    } else {
                        adapterModel.setSelected(true);
                    }


                   /* boolean isAllSelected = true;
                    for (BleAdapterModel signalAdapterModel : signalAdapterModels) {
                        if (!signalAdapterModel.isSelected()) {
                            isAllSelected = false;
                            break;
                        }
                    }
                    isSelectedAll.selectedAll(isAllSelected);*/
                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class ViewHolder {

        private CheckBox signalcheckbox;
        private TextView signal;
        private ImageView signalImageview;
        private TextView signalrssi;

    }

    @FunctionalInterface
    public interface ISelectedAll {
        void selectedAll(boolean isSelectedAll);
    }

}