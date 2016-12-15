package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class HandleSearchActivity extends AppCompatActivity {
    private MyDB myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_search);
        myDB = new MyDB(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String query = extras.getString("query");
            Cursor cursor = myDB.queryWithKeyword(query);
            cursor.moveToFirst();

            final String progressText = cursor.getString(cursor.getColumnIndex("progress"));
            final String detailText = cursor.getString(cursor.getColumnIndex("detail"));
            Toast.makeText(this, progressText + detailText, Toast.LENGTH_SHORT).show();
        }
    }
}
