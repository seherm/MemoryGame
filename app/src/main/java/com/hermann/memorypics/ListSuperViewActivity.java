package com.hermann.memorypics;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class ListSuperViewActivity extends AppCompatActivity {

    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_super_view);

        ListView listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedRow", position);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/saved_images");

        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; ++i) {
            list.add(Uri.fromFile(files[i]).toString());
        }

        ListAdapter adapter = new ListAdapter(getApplicationContext());
        adapter.list = list;
        listView.setAdapter(adapter);

    }

}
