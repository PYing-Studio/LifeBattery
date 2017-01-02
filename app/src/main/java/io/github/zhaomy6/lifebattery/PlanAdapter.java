package io.github.zhaomy6.lifebattery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PlanAdapter extends BaseAdapter {
    private List<Plan> list;
    private Context context;

    //  仅用在无DDL的plan中
    private static HashMap<Integer, Boolean> isSelected;
    private boolean isLongPlan;

    public PlanAdapter(Context context, List<Plan> list) {
        this.context = context;
        this.list = list;
        this.isLongPlan = false;
    }

    public PlanAdapter(Context context, List<Plan> list, boolean lp) {
        this.context = context;
        this.list = list;
        if (lp && list != null) {
            isSelected  = new HashMap<Integer, Boolean>();
            initSelected(list.size());
        }
        this.isLongPlan = true;
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
    public View getView(final int position, View view, ViewGroup parent) {
        View convertView;
        ViewHolder viewHolder;

        if (!this.isLongPlan) {
            //  作为有DDL任务的adapter
            if (view == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.plans_item, null);
                viewHolder = new ViewHolder();
                viewHolder.Title = (TextView) convertView.findViewById(R.id.planTitle);
                viewHolder.DDL = (TextView) convertView.findViewById(R.id.planDDL);
                viewHolder.state = (ImageView) convertView.findViewById(R.id.battery_state);
                convertView.setTag(viewHolder);
            } else {
                convertView = view;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String ddlStr = list.get(position).getDDL().split("\n")[0];
            viewHolder.Title.setText(list.get(position).getTitle());
            viewHolder.DDL.setText(ddlStr);
        } else {
            //  作为无DDL任务的adapter
            if (view == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.long_plans_item, null);
                viewHolder = new ViewHolder();
                viewHolder.Title = (TextView) convertView.findViewById(R.id.longPlanTitle);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.long_plan_cb);
                convertView.setTag(viewHolder);
            } else {
                convertView = view;
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.cb.setOnClickListener(new View.OnClickListener() {
                final int p = position;
                public void onClick(View v) {
                    if (isSelected.get(p)) {
                        isSelected.put(p, false);
                        setIsSelected(isSelected);
                    } else {
                        isSelected.put(position, true);
                        setIsSelected(isSelected);
                    }
                }
            });

            viewHolder.Title.setText(list.get(position).getTitle());

            if (isSelected == null) {
                Log.d("PlanAdapter", "Error");
            }
            viewHolder.cb.setChecked(isSelected.get(position));
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        PlanAdapter.isSelected = isSelected;
    }

    public ArrayList<Plan> getSelectedPlans() {
        ArrayList<Plan> res = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i) {
            if (isSelected.get(i)) {
                res.add(list.get(i));
            }
        }
        return res;
    }

    private void initSelected(int s) {
        for (int i = 0; i < s; i++) {
            PlanAdapter.isSelected.put(i, false);
        }
    }

    private class ViewHolder {
        TextView Title;
        TextView DDL;
        CheckBox cb;
        ImageView state;
    }
}

/**
 */
class Plan {
    private String title;
    private String DDL;
    private String planType;
    private String planDetail;
    private String finished;
    private String color;

    public Plan(String title, String DDL, String planType, String planDetail, String finished) {
        this.title = title;
        this.DDL = DDL;
        this.planDetail = planDetail;
        this.planType = planType;
        this.finished = finished;
    }

    public String getTitle() {
        return title;
    }

    public String getDDL() {
        return DDL;
    }


    public String getPlanDetail() {
        return planDetail;
    }

    public String getFinished() {
        return finished;
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

    public void setPlanDetail(String planDetail) {
        this.planDetail = planDetail;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }
}
