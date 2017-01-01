package io.github.zhaomy6.lifebattery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private RoundedImageView headImage = null;
    private TextView userName;
    private TextView pastDays;
    private TextView successedPlan;
    private TextView failedPlan;
    private MyDB myDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setTitle("使用统计");

        myDB = new MyDB(this);
        userName = (TextView)findViewById(R.id.statistic_user_name);
        pastDays = (TextView)findViewById(R.id.statistic_days);
        successedPlan = (TextView)findViewById(R.id.statistic_tasks);
        failedPlan = (TextView) findViewById(R.id.statistic_state);
        findViewById(R.id.statistic_about).setOnClickListener(this);
        findViewById(R.id.statistic_logout).setOnClickListener(this);

        headImage = (RoundedImageView)findViewById(R.id.statistic_avatar);
        headImage.setOnClickListener(this);

        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        String userNameText = sp.getString("username", "");
        String registerTime = sp.getString("registerTime", "1996-03-01");
        boolean flag = sp.getBoolean("hasChoose", false);
        if (flag) {
            String name = "test.jpg";
            try{
                FileInputStream fis = getApplicationContext().openFileInput(name);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
                headImage.setImageBitmap(bitmap);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        userName.setText(userNameText);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(registerTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long s1 = date.getTime();
        long s2 = System.currentTimeMillis();
        long day = (s1 - s2) / 1000 / 60 / 60 / 24 + 1;
        pastDays.setText("已打卡 " + day + " 天");

        int finishedCount = myDB.getFinisedTaskNum();
        int overtimeCount = myDB.getOvertimeTaskNum();
        int unFinishedCount = myDB.getUnfinishedTaskNum();
        successedPlan.setText("已完成 " + finishedCount + " 任务");
        failedPlan.setText("" + overtimeCount + " 待完成, " + unFinishedCount + " 已超时");

        //  TODO: 从数据库中读取并设置统计信息

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
            case R.id.statistic_avatar:
                gallery();
                break;
            case R.id.statistic_about:
                intent = new Intent(StatisticsActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.statistic_logout:
                intent = new Intent(StatisticsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    /*
    * 从相册获取
    */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", headImage.getWidth());
        intent.putExtra("outputY", headImage.getHeight());

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                this.headImage.setImageBitmap(bitmap);
                SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("hasChoose", true);
                editor.apply();
                String name = "test.jpg";
                FileOutputStream out;
                try {
                    out = getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
