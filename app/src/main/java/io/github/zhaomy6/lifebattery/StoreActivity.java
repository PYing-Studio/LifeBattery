package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class StoreActivity extends AppCompatActivity
    implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private int[] batteriesID = new int[] {
            R.id.store_battery0, R.id.store_battery1, R.id.store_battery2,
            R.id.store_battery3, R.id.store_battery4, R.id.store_battery5,
            R.id.store_battery6, R.id.store_battery7, R.id.store_battery8,
            R.id.store_battery9, R.id.store_battery10, R.id.store_battery11
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //  配置工具栏
//        Toolbar toolbar = (Toolbar)findViewById(R.id.store_toolBar);
//        toolbar.setTitle("LifeBattery");
//        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(this);

        for (int id : this.batteriesID) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    //  TODO: 处理点击电池的逻辑
    //  TODO: 电池图标动态变化
    @Override
    public void onClick(View view) {
        boolean isImgButton = false;
        for (int id : batteriesID) {
            if (id == view.getId()) {
                isImgButton = true;
                break;
            }
        }

        if (isImgButton) {
            //  处理点击电池的逻辑
            //  查询该周所有待办事项
            Toast.makeText(this, "点击了一块电池", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = null;
        switch(view.getId()) {
            default:
                Toast.makeText(this, "点击逻辑未处理", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.store_menu_add) {
            Intent intent = new Intent(StoreActivity.this, AddActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }
}
