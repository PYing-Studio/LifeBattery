package io.github.zhaomy6.lifebattery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LoginActivity extends Activity
        implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {
    EditText datePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_btn).setOnClickListener(this);

        datePicker = (EditText) findViewById(R.id.login_input_birth_);
        datePicker.setOnClickListener(this);
    }

    private void hideKeyboard(EditText et){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    private void datePickEditTextDialog() {
        Date date = new Date();
        hideKeyboard(datePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, year, month, day);
        datePickerDialog.show(getFragmentManager(), "DateFragment");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                TextInputLayout userLayout = (TextInputLayout) findViewById(R.id.login_input_username_layout);
                TextInputLayout birthLayout = (TextInputLayout) findViewById(R.id.login_input_birth_layout);
                String username = ((TextInputEditText) findViewById(R.id.login_input_username))
                        .getText().toString();
                String birthday = ((EditText) findViewById(R.id.login_input_birth_))
                        .getText().toString();

                if (username.length() == 0) {
                    userLayout.setError("请输入用户名");
                    return;
                } else if (birthday.equals("请输入您的生日")) {
                    birthLayout.setError("请输入您的生日");
                    return;
                }

                SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
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
            case R.id.login_input_birth_:
                datePickEditTextDialog();
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
    }

    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour, minute;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if(reminderCalendar.after(calendar)){
            Toast.makeText(this, "未来的日期不能作为生日", Toast.LENGTH_SHORT).show();
            return;
        }

        if(DateFormat.is24HourFormat(this)){
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = calendar.get(Calendar.HOUR);
        }

        minute = calendar.get(Calendar.MINUTE);
        calendar.set(year, month, day, hour, minute);
        String dateFormat = "yyyy/MM/dd";
        Date mUserReminderDate = calendar.getTime();
        datePicker.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public static String formatDate(String formatString, Date dateToFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString, Locale.CHINA);
        return simpleDateFormat.format(dateToFormat);
    }
}
