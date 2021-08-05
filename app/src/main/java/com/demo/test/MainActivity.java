package com.demo.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.demo.test.util.DialogUtil;
import com.demo.test.view.SweetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SweetDialog dialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog= DialogUtil.createDateDefault(MainActivity.this, new DialogUtil.OnResultCallback<Date>() {
                    @Override
                    public void onResult(Date date) {
                        String dateStr=new SimpleDateFormat("yyyy-MM-dd").format(date);
                        textView.setText(dateStr);
                    }
                });
                dialog.show();
            }
        });
    }
}
