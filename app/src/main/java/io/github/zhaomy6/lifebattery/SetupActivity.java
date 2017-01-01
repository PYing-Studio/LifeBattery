package io.github.zhaomy6.lifebattery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SetupActivity extends Activity {
    private final int DISPLAY_LENGTH = 1700;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ImageView imageView = (ImageView) findViewById(R.id.setup_anim);
        imageView.setBackgroundResource(R.drawable.loading_anim);
        AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
        anim.start();

        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        final boolean hasLoginBefore = sp.getBoolean("hasLoginBefore", false);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = null;
                if (hasLoginBefore) {
                    intent = new Intent(SetupActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SetupActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.my_fade_in, R.anim.my_fade_out);
            }
        }, DISPLAY_LENGTH);
    }
}
