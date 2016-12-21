package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 新建任务
 */

public class AddActivity extends AppCompatActivity {
    private EditText title;
    private  Button DDL;
    private EditText detail;
    private Button button;
    private  MyDB myDB = new MyDB(this);
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner spinner;
    private String DDLText = "";
    private String typeText = "紧急且重要";
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        setTitle("添加计划");
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        spinner = (Spinner) findViewById(R.id.spinner);
        timePicker.setIs24HourView(true);
        title = (EditText) findViewById(R.id.titleEdit);
        DDL = (Button) findViewById(R.id.a_planDDL);
        detail = (EditText) findViewById(R.id.detailEdit);
        button = (Button) findViewById(R.id.addButton);

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            // 在数据库中搜索输入的关键词
//            String titleText = extras.getString("title");
//            String DDLText = extras.getString("DDL");
//            String typeText = extras.getString("type");
//            String detailText = extras.getString("detail");
//            flag = true;
//            title.setText(titleText);
//            title.setEnabled(false);
//            detail.setText(detailText);
//        }

        // 点击添加任务, 不可重复添加
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = title.getText().toString();
                String detailText = detail.getText().toString();

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
        });

        //  处理取消按钮逻辑，点击直接返回上一页面
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 设置截止时间, 动态显示日期选择器
        DDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = DDL.getText().toString();
                if (text.equals("截止日期")) {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                    DDL.setText("设置完成");
                } else if (text.equals("设置完成")) {
                    int y = datePicker.getYear();
                    int mon = datePicker.getMonth();
                    int d = datePicker.getDayOfMonth();
                    int h = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();

                    DDLText = "" + y + mon + d + h + min;
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                    DDL.setText("截止日期");
                }
            }
        });

        // 任务类型下拉框
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] types = getResources().getStringArray(R.array.spinner);
                typeText = types[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

}
