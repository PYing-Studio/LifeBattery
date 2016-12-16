package io.github.zhaomy6.lifebattery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;

public class SetupActivity extends Activity {
    private final int DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setup);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                overridePendingTransition(R.anim.my_fade_in, R.anim.my_fade_out);
            }
        }, DISPLAY_LENGTH);
    }
}
