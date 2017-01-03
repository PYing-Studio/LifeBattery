package io.github.zhaomy6.lifebattery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 关于界面
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("关于 LifeBattery");
    }
}
