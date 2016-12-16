package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity
    implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

//        ArrayList<String> data = new ArrayList<>();
//        data.add("活过本学期");
//        data.add("周游世界");
//        data.add("期末考试门门满分～");
//        StoreAdapter adapter = new StoreAdapter(this, R.layout.store_item, data);
//        ListView lv = (ListView) findViewById(R.id.store_list);
//        lv.setAdapter(adapter);
//
//        Button addBtn = (Button) findViewById(R.id.store_add_btn);
//        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch(view.getId()) {
            default:
                Toast.makeText(this, "点击逻辑未处理", Toast.LENGTH_SHORT).show();
        }
    }
}
