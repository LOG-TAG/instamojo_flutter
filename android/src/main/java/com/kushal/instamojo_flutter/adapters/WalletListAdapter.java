package com.kushal.instamojo_flutter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kushal.instamojo_flutter.R;
import com.kushal.instamojo_flutter.models.Wallet;

import java.util.List;

public class WalletListAdapter extends ArrayAdapter<Wallet> {

    private static class ViewHolder {
        TextView itemName;
    }

    public WalletListAdapter(@NonNull Context context, List<Wallet> wallets) {
        super(context, 0, wallets);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_view_instamojo, parent, false);
            viewHolder.itemName = convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Wallet item = getItem(position);
        viewHolder.itemName.setText(item.getName());

        return convertView;
    }
}
