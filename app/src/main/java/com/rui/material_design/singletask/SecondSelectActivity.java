package com.rui.material_design.singletask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rui.material_design.R;

public class SecondSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_select);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondSelectActivity.this, SingleTaskActivity.class);
                intent.putExtra("data", "我是从第二级选择页面返回的数据");
                startActivity(intent);
                finish();
            }
        });
    }
}
