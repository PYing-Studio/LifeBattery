package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private  MyDB myDB = new MyDB(this);
    private Date mUserReminderDate = null;

    private EditText mToDoTextBodyEditText;
    private EditText mDateEditText;
    private EditText mTimeEditText;
    private EditText mDetailEdit;
    private SwitchCompat longPlanFlag;
    private FloatingActionButton mAddButton;

    //  简化设计，type分为两种：有DDL和无DDL
    //  SwitchCompat on 为有DDL；否则为无DDL
    //  字符串存储type == "true"即为有DDL；type == "false"即为无DDL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mToDoTextBodyEditText = (EditText) findViewById(R.id.titleEdit);
        mDateEditText = (EditText) findViewById(R.id.pickerButton1);
        mTimeEditText = (EditText) findViewById(R.id.pickerButton2);
        mDetailEdit = (EditText) findViewById(R.id.detailEdit);
        longPlanFlag = (SwitchCompat) findViewById(R.id.add_longPlan);
        mAddButton = (FloatingActionButton) findViewById(R.id.addButton);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        mDateEditText.setOnClickListener(this);
        mTimeEditText.setOnClickListener(this);
        longPlanFlag.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

//        setTitle("添加计划");
//        title = (EditText) findViewById(R.id.titleEdit);
//        DDL = (Button) findViewById(R.id.a_planDDL);
//        detail = (EditText) findViewById(R.id.detailEdit);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String titleText = title.getText().toString();
//                String detailText = detail.getText().toString();
//
//                if (titleText.equals("")) {
//                    Toast.makeText(AddActivity.this, "任务名为空,请完善", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (myDB.isExists(titleText)) {
//                        Toast.makeText(AddActivity.this, "此任务已经存在", Toast.LENGTH_SHORT).show();
//                    } else {
//                        myDB.insert2DB(titleText, DDLText, typeText, detailText, "未完成");
//                        Intent intent = new Intent();
//                        intent.setClass(AddActivity.this, PlansActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }
//        });
//
//        //  处理取消按钮逻辑，点击直接返回上一页面
//
//        DDL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String text = DDL.getText().toString();
//                if (text.equals("点击设置截止日期")) {
//                    datePicker.setVisibility(View.VISIBLE);
//                    timePicker.setVisibility(View.VISIBLE);
//                    DDL.setText("设置完成");
//                } else if (text.equals("设置完成")) {
//                    int y = datePicker.getYear();
//                    int mon = datePicker.getMonth();
//                    int d = datePicker.getDayOfMonth();
//                    int h = timePicker.getCurrentHour();
//                    int min = timePicker.getCurrentMinute();
//
//                    DDLText = "" + y + mon + d + h + min;
//                    datePicker.setVisibility(View.GONE);
//                    timePicker.setVisibility(View.GONE);
//                    DDL.setText("点击设置截止日期");
//                }
//            }
//        });
//
    }

    private void hideKeyboard(EditText et){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    private void datePickEditTextDialog() {
        Date date = new Date();
        hideKeyboard(mToDoTextBodyEditText);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, year, month, day);
        datePickerDialog.show(getFragmentManager(), "DateFragment");
    }

    private void timePickEditTextDialog() {
        Date date = new Date();
        hideKeyboard(mToDoTextBodyEditText);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, hour, minute,
                DateFormat.is24HourFormat(this));
        timePickerDialog.show(getFragmentManager(), "TimeFragment");
    }

    public void setTime(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        if(mUserReminderDate!=null){
            calendar.setTime(mUserReminderDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();
        String dateFormat;
        if(DateFormat.is24HourFormat(this)){
            dateFormat = "k:mm";
        } else {
            dateFormat = "HH:mm a";
        }
        mTimeEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        int hour, minute;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if(reminderCalendar.before(calendar)){
            Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(DateFormat.is24HourFormat(this)){
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);
        calendar.set(year, month, day, hour, minute);
        String dateFormat = "yyyy-MM-dd";
        mUserReminderDate = calendar.getTime();
        mDateEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public static String formatDate(String formatString, Date dateToFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString, Locale.CHINA);
        return simpleDateFormat.format(dateToFormat);
    }

    private void switchEvent() {
        boolean flag = longPlanFlag.isChecked();
        LinearLayout layout = (LinearLayout) findViewById(R.id.date_time_picker_bar);
        if (flag) {
            layout.setVisibility(View.INVISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }

    private void addEvent() {
        String titleText = mToDoTextBodyEditText.getText().toString();
        String detailText = mDetailEdit.getText().toString();
        String DDLText = mDateEditText.getText().toString() + "\n" + mTimeEditText.getText().toString();
        String typeText = longPlanFlag.isChecked() ? "true" : "false";

        if (titleText.equals("")) {
            Toast.makeText(AddActivity.this, "任务名为空,请完善", Toast.LENGTH_SHORT).show();
        } else {
            if (myDB.isExists(titleText)) {
                Toast.makeText(AddActivity.this, "此任务已经存在", Toast.LENGTH_SHORT).show();
            } else {
                myDB.insert2DB(titleText, DDLText, typeText, detailText, "未完成");
                Intent intent = new Intent();
                intent.setClass(AddActivity.this, PlansActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        setTime(hourOfDay, minute);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                finish();
                break;
            case R.id.pickerButton1:
                datePickEditTextDialog();
                break;
            case R.id.pickerButton2:
                timePickEditTextDialog();
                break;
            case R.id.addButton:
                addEvent();
                break;
            case R.id.add_longPlan:
                switchEvent();
                break;
            default:
                Toast.makeText(this, "未处理的OnClick事件", Toast.LENGTH_SHORT).show();
        }
    }
}
