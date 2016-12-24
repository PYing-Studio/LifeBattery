package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 新建任务
 */
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
    private boolean updateFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("添加计划");

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
        updateFlag = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null && !extras.isEmpty()) {
            modifyPlanWithBundle(extras);
        }
    }

    // 修改计划
    private void modifyPlanWithBundle(Bundle extras) {
        updateFlag = true;
        String title = extras.getString("titleText");
        String DDL = extras.getString("DDLText");
        String detail = extras.getString("detailText");
        longPlanFlag.setEnabled(false);

        mToDoTextBodyEditText.setText(title);
        mToDoTextBodyEditText.setEnabled(false);
        mDetailEdit.setText(detail);
        String[] frag = DDL.split("\n");
        mDateEditText.setText(frag[0]);
        mTimeEditText.setText(frag[1]);

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
//        dateFormat = "HH:mm a";
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

    private void addEvent() throws ParseException {
        String titleText = mToDoTextBodyEditText.getText().toString();
        String detailText = mDetailEdit.getText().toString();
        String DDLText = mDateEditText.getText().toString() + "\n" + mTimeEditText.getText().toString();
        if (!longPlanFlag.isChecked()) {
            if (DDLText.equals("\n")) {
                Toast.makeText(AddActivity.this, "请设置时间", Toast.LENGTH_SHORT).show();
                return;
            } else if (DDLText.indexOf('\n') == DDLText.length() - 1) {
                DDLText += "00:00 上午";
            } else if (DDLText.indexOf('\n') == 0) {
                Calendar calendar = Calendar.getInstance();
                DDLText = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + DDLText;
            }

            String minuteFormat = "";
            if (DateFormat.is24HourFormat(getApplicationContext())) {
                minuteFormat += "k:mm";
            } else {
                minuteFormat += "HH:mm a";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + minuteFormat, Locale.CHINA);
            String[] frag = DDLText.split("\n");
            String dstr = frag[0] + " " + frag[1];
            Date date = sdf.parse(dstr);
            long s1 = date.getTime();
            long s2 = System.currentTimeMillis();
            if (s2 - s1 > 0) {
                Toast.makeText(this, "时间不可倒流", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        String typeText = longPlanFlag.isChecked() ? "true" : "false";

       if (updateFlag) {
           myDB.updateDB(titleText, DDLText, typeText, detailText);
           Intent intent = new Intent();
           intent.setClass(AddActivity.this, PlansActivity.class);
           startActivity(intent);
           finish();
       } else {
           updateFlag = false;
           if (titleText.equals("")) {
               Toast.makeText(AddActivity.this, "任务名为空,请完善", Toast.LENGTH_SHORT).show();
           } else if (myDB.isExists(titleText)) {
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
                try {
                    addEvent();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.add_longPlan:
                switchEvent();
                break;
            default:
                Toast.makeText(this, "未处理的OnClick事件", Toast.LENGTH_SHORT).show();
        }
    }
}
