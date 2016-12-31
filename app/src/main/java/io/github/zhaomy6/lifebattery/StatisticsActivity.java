package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    /* 裁剪后图片的宽和高 */
    private static int output_X = 220;
    private static int output_Y = 220;
    private RoundedImageView headImage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setTitle("使用统计");

        headImage = (RoundedImageView)findViewById(R.id.statistic_avatar);
        headImage.setOnClickListener(this);

        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        String userName = sp.getString("username", "");
        String registerTime = sp.getString("registerTime", "1996-03-01");

        TextView uv = (TextView) findViewById(R.id.statistic_user_name);
        uv.setText(userName);

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
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
