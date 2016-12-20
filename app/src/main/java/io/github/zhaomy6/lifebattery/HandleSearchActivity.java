package io.github.zhaomy6.lifebattery;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HandleSearchActivity extends AppCompatActivity {
    private ListView listView;
    private MyDB myDB;
    private SimpleCursorAdapter sca;
    private TextView g_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_search);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        myDB = new MyDB(this);
        listView = (ListView) findViewById(R.id.groundList);
        g_count = (TextView)findViewById(R.id.g_count);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String query = extras.getString("query");
            Cursor listItems = myDB.queryWithKeyword(query);
            sca = new SimpleCursorAdapter(getApplicationContext(), R.layout.plans_item,
                    listItems, new String[] {"title", "DDL"},
                    new int[]{R.id.planTitle, R.id.planDDL}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            listView.setAdapter(sca);
            int count = listView.getCount();
            g_count.setText("已完成: " + count);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater factory = LayoutInflater.from(HandleSearchActivity.this);
                View views = factory.inflate(R.layout.dialoglayout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(HandleSearchActivity.this);
                builder.setView(views);

                TextView d_planType = (TextView)views.findViewById(R.id.d_planType);
                TextView d_planDDL = (TextView)views.findViewById(R.id.d_planDDL);
                TextView d_planDetail = (TextView)views.findViewById(R.id.d_planDetail);

                Cursor cursor = (Cursor)sca.getItem(position);
                final String titleText = cursor.getString(cursor.getColumnIndex("title"));
                final String DDLText = cursor.getString(cursor.getColumnIndex("DDL"));
                d_planDDL.setText(DDLText);

                Cursor cursor1 = myDB.getWithTitle(titleText);
                cursor1.moveToFirst();

                final String detailText = cursor1.getString(cursor1.getColumnIndex("detail"));
                d_planDetail.setText(detailText);

                String typeText = "任务类型类型：" + cursor1.getString(cursor1.getColumnIndex("type"));
                d_planType.setText(typeText);

                builder.setTitle(titleText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //  do nothing
                    }
                });

                d_planDDL.setText(DDLText);
                builder.create().show();
            }

        });
    }
}
