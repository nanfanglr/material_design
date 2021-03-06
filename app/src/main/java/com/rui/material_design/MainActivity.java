package com.rui.material_design;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anlia.pageturn.BookPageViewActivity;
import com.rui.material_design.behavior.FollowBehaviorActivity;
import com.rui.material_design.behavior.ScrollToTopBehaviorActivity;
import com.rui.material_design.singletask.SingleTaskActivity;
import com.rui.material_design.toolbar.HeadBarActivity;
import com.rui.material_design.toolbar.ToolbarActivity;
import com.rui.material_design.webview.WebViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.head_bar)
    TextView headBar;
    @BindView(R.id.root_layout)
    ConstraintLayout rootLayout;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        //设置状态栏文字颜色
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.addMarginTopEqualStatusBarHeight(rootLayout);

//        BarUtils.setStatusBarVisibility(this, false);
//        BarUtils.setNavBarVisibility(this, false);
//        BarUtils.setNavBarImmersive(this.getWindow());

//        BarUtils.setNavBarColor(this, Color.parseColor("#55000000"));
    }

    @OnClick({R.id.button, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(this, ScrollToTopBehaviorActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, FollowBehaviorActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this, BookPageViewActivity.class));
                break;
            case R.id.button4:
                startActivity(new Intent(this, ToolbarActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(this, HeadBarActivity.class));
                break;
            case R.id.button6:
                startActivity(new Intent(this, TextFromHtmlActivity.class));
                break;
            case R.id.button7:
                startActivity(new Intent(this, SingleTaskActivity.class));
                break;
            case R.id.button8:
                startActivity(new Intent(this, WebViewActivity.class));
                break;
        }
    }


}
