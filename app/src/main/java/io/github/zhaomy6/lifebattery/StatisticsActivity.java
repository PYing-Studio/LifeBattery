package io.github.zhaomy6.lifebattery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        String userName = sp.getString("username", "");
        String registerTime = sp.getString("registerTime", "1996-03-01");

        TextView uv = (TextView) findViewById(R.id.statistic_user_name);
        uv.setText(userName);
        //  TODO: set use time
    }
}
