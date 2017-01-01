package io.github.zhaomy6.lifebattery;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PlansActivity extends AppCompatActivity {
    private ListView listView;
    private MyDB myDB;
    private SimpleCursorAdapter sca;

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.addAction) {
                Intent intent = new Intent();
                intent.setClass(PlansActivity.this, AddActivity.class);
                startActivity(intent);
                return true;
            } else if (menuItem.getItemId() == R.id.sortAction) {
                Cursor cursors = myDB.sortWithTime();
                sca.swapCursor(cursors);
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.searchAction).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.setClass(PlansActivity.this, HandleSearchActivity.class);
                intent.putExtra("query", query);
                startActivityForResult(intent, 1);
//                Toast.makeText(PlansActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    //  TODO: Bug 已完成的任务仍然占有title，于是无法创建与已完成任务同名的任务
    public void updateListView() {
        Cursor cursors = myDB.getPart();
        sca.swapCursor(cursors);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                updateListView();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        toolbar.setTitle("LifeBattery");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        myDB = new MyDB(this);
        updateDBImmediately();

        Cursor listItems = myDB.getPart();
        sca = new SimpleCursorAdapter(getApplicationContext(), R.layout.plans_item,
                listItems, new String[] {"title", "DDL"},
                new int[]{R.id.planTitle, R.id.planDDL}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView = (ListView) findViewById(R.id.planList);
        listView.setAdapter(sca);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String titleText;
            private String DDLText;
            private String detailText;
            private String timeTextToShow;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater factory = LayoutInflater.from(PlansActivity.this);
                View views = factory.inflate(R.layout.dialoglayout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(PlansActivity.this);
                builder.setView(views);

                TextView d_planType = (TextView)views.findViewById(R.id.d_planType);
                TextView d_planDDL = (TextView)views.findViewById(R.id.d_planDDL);
                TextView d_planDetail = (TextView)views.findViewById(R.id.d_planDetail);

                Cursor cursor = (Cursor)sca.getItem(position);
                titleText = cursor.getString(cursor.getColumnIndex("title"));
                DDLText = cursor.getString(cursor.getColumnIndex("DDL"));
                timeTextToShow = "";

                Intent intent = new Intent("widgetReceiver");
                intent.setClass(PlansActivity.this, WidgetReceiver.class);
                intent.putExtra("DDL", DDLText);
                intent.putExtra("title", titleText);
                sendBroadcast(intent);

                String[] frag = DDLText.split("\n");
                timeTextToShow = "截止日期:\n" + frag[0] + " " + frag[1];

                d_planDDL.setText(timeTextToShow);

                Cursor cursor1 = myDB.getWithTitle(titleText);
                cursor1.moveToFirst();

                detailText = cursor1.getString(cursor1.getColumnIndex("detail"));
                d_planDetail.setText(detailText);

                String typeText = "任务类型类型: " + "近期计划";
                d_planType.setText(typeText);

                //  对话框属性
                builder.setTitle(titleText);
                builder.setNegativeButton("修改任务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(PlansActivity.this, AddActivity.class);
//                        intent.putExtra("intent", "modify added plan");
                        intent.putExtra("titleText", titleText);
                        intent.putExtra("DDLText", DDLText);
                        intent.putExtra("detailText", detailText);
                        startActivityForResult(intent, 1);
                        finish();
                    }
                });



                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.create().show();
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Cursor cursor = (Cursor)sca.getItem(position);
                final String title = cursor.getString(cursor.getColumnIndex("title"));
                final String DDL = cursor.getString(cursor.getColumnIndex("DDL"));
                AlertDialog.Builder builder = new AlertDialog.Builder(PlansActivity.this);
                builder.setTitle("任务管理");

                builder.setNegativeButton("取消任务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.deleteDB(title);
                        updateListView();
                    }
                });

                builder.setPositiveButton("完成任务",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.updateFinished(title, "完成");
                        updateListView();

                        //  将完成任务的时间写入
                        String finishDate = DDL.split("\n")[0];
                        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
                        String finishList = sp.getString("finishList", "");
                        SharedPreferences.Editor editor = sp.edit();
                        if (!"".equals(finishList)) {
                            finishList += ";";
                        }
                        finishList += finishDate;
                        editor.putString("finishList", finishList);
                        editor.apply();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    //  TODO: 设置超时提醒
    private void updateDBImmediately() {
        Date date = new Date();
        String dateFormat = "yyyy-MM-dd";
        String minuteFormat = "";
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            minuteFormat += "k:mm";
        } else {
            minuteFormat += "HH:mm a";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CHINA);
        String d_string = simpleDateFormat.format(date);

        simpleDateFormat = new SimpleDateFormat(minuteFormat, Locale.CHINA);
        String m_string = simpleDateFormat.format(date);
        myDB.updateTimeout(d_string + "\n" + m_string);

        int num = myDB.getOvertimeTaskNum();
        if (num > 0) {
            Toast.makeText(this, "提醒:\n又有 " + num + " 个计划超时", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sca.swapCursor(null);
    }
}
