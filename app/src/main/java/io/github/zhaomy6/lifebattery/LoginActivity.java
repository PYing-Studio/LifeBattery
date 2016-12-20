package io.github.zhaomy6.lifebattery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LoginActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((Button) findViewById(R.id.login_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                TextInputLayout userLayout = (TextInputLayout) findViewById(R.id.login_input_username_layout);
                TextInputLayout birthLayout = (TextInputLayout) findViewById(R.id.login_input_birth_layout);
                String username = ((TextInputEditText) findViewById(R.id.login_input_username))
                        .getText().toString();
                String birthday = ((TextInputEditText) findViewById(R.id.login_input_birth))
                        .getText().toString();

                if (username.length() == 0) {
                    userLayout.setError("请输入用户名");
                    return;
                } else if (birthday.length() == 0) {
                    birthLayout.setError("请输入您的生日");
                    return;
                }

                SharedPreferences sp = getSharedPreferences("LieBatteryPre", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("hasLoginBefore", true);
                editor.putString("username", username);
                //  birthday format: yyyy/mm/dd
                //  ** need modify if the format is changed **
                editor.putString("birthday", birthday);
                String birthYear = birthday.split("/")[0];
                int totalWeeks = calculateTotalWeeks(birthYear);
                editor.putInt("totalWeeks", totalWeeks);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String registerTime = df.format(new Date());  //  year-month-day
                editor.putString("registerTime", registerTime);

                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    //  birthday
    //  return totalWeeks/leftWeeks
    private int calculateTotalWeeks(String birthYear) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String curYear = df.format(new Date()).split("-")[0];  //  year/month/day
        Log.d("calculate", curYear);
        int age = Integer.parseInt(curYear) - Integer.parseInt(birthYear);
        Random rand = new Random();
        if (age <= 0) age = 0;
        int acc = age > 80 ? age + 10 : 80;
        return (acc + rand.nextInt(20)) * 52;
    }
}
