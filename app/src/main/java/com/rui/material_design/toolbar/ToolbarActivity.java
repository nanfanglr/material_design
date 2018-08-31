package com.rui.material_design.toolbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rui.material_design.R;

public class ToolbarActivity extends AppCompatActivity {

    private Toolbar mNormalToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        mNormalToolbar= (Toolbar) findViewById(R.id.toolbar_normal);
        initToolbar();

    }

    private void initToolbar() {
        //设置menu
        mNormalToolbar.inflateMenu(R.menu.menu_normal);
        //设置menu的点击事件
        mNormalToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    Toast.makeText(ToolbarActivity.this , R.string.menu_search , Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.action_notification) {
                    Toast.makeText(ToolbarActivity.this , R.string.menu_notification , Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.action_item_one) {
                    Toast.makeText(ToolbarActivity.this , R.string.item_one , Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.action_item_two) {
                    Toast.makeText(ToolbarActivity.this , R.string.item_two , Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });
        //设置左侧NavigationIcon点击事件
        mNormalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ToolbarActivity.this, "点击了左侧按钮", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
