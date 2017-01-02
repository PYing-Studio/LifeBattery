package io.github.zhaomy6.lifebattery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlanCursorAdapter extends SimpleCursorAdapter {
    Context context;

    public PlanCursorAdapter(Context c, Cursor listItems) {
        super(c, R.layout.plans_item,
                listItems, new String[] {"title", "DDL"},
                new int[]{R.id.planTitle, R.id.planDDL},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.context = c;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View convertView;
        ImageView state;
        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.plans_item, null);
        } else {
            convertView = view;
        }
        View tmpView = super.getView(position, convertView, parent);
        String ddlStr = ((TextView) tmpView.findViewById(R.id.planDDL)).getText().toString();
        state = (ImageView) convertView.findViewById(R.id.battery_state);
        //  计算时间间隔并动态更换电池图标
        int days = timeDistance(new Date(), ddlStr);
//        Toast.makeText(context, days + "", Toast.LENGTH_SHORT).show();
        if (days > 5) {
            state.setImageResource(R.drawable.state1);
        } else if (days > 3) {
            state.setImageResource(R.drawable.state2);
        } else if (days > 1) {
            state.setImageResource(R.drawable.state3);
        } else {
            state.setImageResource(R.drawable.state4);
        }

        return super.getView(position, convertView, parent);
    }

    private int timeDistance(Date fromWhen, String to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        int days = 7;
//        String dstr="2008-4-24";  demo
        try {
            Date toWhen = sdf.parse(to);
            long diff = toWhen.getTime() - fromWhen.getTime();
            days = (int) diff / 1000 / 60 / 60 / 24;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }
}
