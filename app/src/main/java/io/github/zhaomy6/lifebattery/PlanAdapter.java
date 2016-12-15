package io.github.zhaomy6.lifebattery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangsht on 2016/12/10.
 */

public class PlanAdapter extends BaseAdapter {
    private List<Plan> list;
    private Context context;

    public PlanAdapter(Context context, List<Plan> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView;
        ViewHolder viewHolder;

        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.plans_item, null);
            viewHolder = new ViewHolder();
            viewHolder.Title = (TextView) convertView.findViewById(R.id.planTitle);
            viewHolder.DDL = (TextView) convertView.findViewById(R.id.planDDL);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.Title.setText(list.get(position).getTitle());
        viewHolder.DDL.setText(list.get(position).getDDL());
        return convertView;
    }

    public class ViewHolder {
        TextView Title;
        TextView DDL;
//        TextView Progress;
//        TextView Detail;
    }
}

class Plan {
    private String title;
    private String DDL;
    private String planType;
    private String currentProgress;
    private String planDetail;
    private String color;

    public Plan(String title, String DDL, String planType, String currentProgress, String planDetail) {
        this.title = title;
        this.DDL = DDL;
        this.currentProgress = currentProgress;
        this.planDetail = planDetail;
        this.planType = planType;
    }

    public String getTitle() {
        return title;
    }

    public String getDDL() {
        return DDL;
    }

    public String getCurrentProgress() {
        return currentProgress;
    }

    public String getPlanDetail() {
        return planDetail;
    }

//    public String getColor() {
//        return color;
//    }

    public String getPlanType() {
        return planType;
    }

    public void setDDL(String DDL) {
        this.DDL = DDL;
    }

    public void setCurrentProgress(String currentProgress) {
        this.currentProgress = currentProgress;
    }

    public void setPlanDetail(String planDetail) {
        this.planDetail = planDetail;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }
}
