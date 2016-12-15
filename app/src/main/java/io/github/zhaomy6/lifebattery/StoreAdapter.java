package io.github.zhaomy6.lifebattery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StoreAdapter extends ArrayAdapter<String> {
    private int resourceId;

    public StoreAdapter(Context context, int resource, ArrayList<String> data) {
        super(context, resource, data);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        TextView pos = (TextView) view.findViewById(R.id.store_pos);
        String posStr = position + "";
        pos.setText(posStr);
        TextView tv = (TextView) view.findViewById(R.id.store_description);
        tv.setText(getItem(position));

        return view;
    }
}
