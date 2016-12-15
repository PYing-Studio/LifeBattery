package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {
    private MyDB myDB;
    private TextView title, DDL, progress;
    private Button planBotton, storeBotton, summaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.m_plan_button).setOnClickListener(this);
        findViewById(R.id.m_store_button).setOnClickListener(this);
        findViewById(R.id.m_summary_button).setOnClickListener(this);
//        storeBotton= (Button) findViewById(R.id.m_store_button);
//        summaryButton = (Button) findViewById(R.id.m_summary_button);
//        myDB = new MyDB(this);
//        title = (TextView)findViewById(R.id.m_plan_title);
//        DDL = (TextView)findViewById(R.id.m_plan_ddl);
//        progress = (TextView)findViewById(R.id.m_plan_progress);
//
//        Cursor cursor = myDB.getAll();
//        String titleContent = "", DDLContent= "", progressContent = "";
//        while (cursor.moveToNext()) {
//            String tmp = cursor.getString(cursor.getColumnIndex("DDL"));
//            if (tmp.compareTo(DDLContent) > 0) {
//                titleContent = cursor.getString(cursor.getColumnIndex("title"));
//                DDLContent = tmp;
//                progressContent = cursor.getString(cursor.getColumnIndex("progress"));
//            }
//        }
//        title.setText(titleContent);
//        DDL.setText(DDLContent);
//        progress.setText(progressContent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.m_store_button:
                intent = new Intent(MainActivity.this, StoreActivity.class);
                startActivity(intent);
                break;
            case R.id.m_summary_button:
                intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.m_plan_button:
                intent = new Intent(MainActivity.this, PlansActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "点击逻辑未处理", Toast.LENGTH_SHORT).show();
        }
    }
}
