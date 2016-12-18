package io.github.zhaomy6.lifebattery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangsht on 2016/12/18.
 */

public class SearchAdapter extends BaseAdapter {
    private List<Plan> list;
    private Context context;

    public SearchAdapter(Context context, List<Plan> list) {
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
        SearchAdapter.ViewHolder viewHolder;

        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_item, null);
            viewHolder = new SearchAdapter.ViewHolder();
            viewHolder.Title = (TextView) convertView.findViewById(R.id.s_title);
            viewHolder.DDL = (TextView) convertView.findViewById(R.id.s_DDL);
            viewHolder.search_match = (TextView)convertView.findViewById(R.id.search_Match);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (SearchAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.Title.setText(list.get(position).getTitle());
        viewHolder.DDL.setText(list.get(position).getDDL());
//        viewHolder.search_match.setText(list.get(position));
        return convertView;
    }

    public class ViewHolder {
        TextView Title;
        TextView DDL;
        TextView search_match;
    }
}

