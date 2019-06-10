package com.bosch.pai.ipsadminapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.models.SignalAdapterModel;

import java.util.List;
import java.util.Objects;

public class SignalAdapter extends BaseAdapter {

    private Context context;
    private List<SignalAdapterModel> signalAdapterModels;
    private ISelectedAll isSelectedAll;


    public SignalAdapter(Context context, List<SignalAdapterModel> signalAdapterModels, ISelectedAll isSelectedAll) {
        this.context = context;
        this.signalAdapterModels = signalAdapterModels; //NOSONAR
        this.isSelectedAll = isSelectedAll;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.signal_item, null, true);

            holder.signalcheckbox = convertView.findViewById(R.id.signalcheckbox);
            holder.signal = convertView.findViewById(R.id.signal);

            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object setValueForTime as a tag to the view
            holder = (ViewHolder) convertView.getTag();
        }

        final String signalName =
                signalAdapterModels.get(position).getSnapshotItemWithSensorType().getSnapshotItem().getCustomField()[0];
        holder.signal.setText(signalName);

        holder.signalcheckbox.setChecked(signalAdapterModels.get(position).isSelected());

        holder.signalcheckbox.setTag(position);
        holder.signalcheckbox.setOnClickListener((v) -> {

            final Integer pos = (Integer) holder.signalcheckbox.getTag();

            if (pos > -1 && pos < signalAdapterModels.size()) {
                SignalAdapterModel adapterModel = signalAdapterModels.get(pos);
                if (adapterModel.isSelected()) {
                    adapterModel.setSelected(false);
                } else {
                    adapterModel.setSelected(true);
                }

                boolean isAllSelected = true;
                for (SignalAdapterModel signalAdapterModel : signalAdapterModels) {
                    if (!signalAdapterModel.isSelected()) {
                        isAllSelected = false;
                        break;
                    }
                }
                isSelectedAll.selectedAll(isAllSelected);
            }
        });

        return convertView;
    }

    private class ViewHolder {

        private CheckBox signalcheckbox;
        private TextView signal;

    }

    @FunctionalInterface
    public interface ISelectedAll {
        void selectedAll(boolean isSelectedAll);
    }

}