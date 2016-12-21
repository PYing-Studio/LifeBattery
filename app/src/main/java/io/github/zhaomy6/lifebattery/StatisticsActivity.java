package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setTitle("使用统计");

        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        String userName = sp.getString("username", "");
        String registerTime = sp.getString("registerTime", "1996-03-01");

        TextView uv = (TextView) findViewById(R.id.statistic_user_name);
        uv.setText(userName);

        //  TODO: set use time

        findViewById(R.id.statistic_detail).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.statistic_detail:
                Intent intent = new Intent(StatisticsActivity.this, CalendarActivity.class);
                //  传入数据
                Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
