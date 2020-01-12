package com.rui.material_design.toolbar;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rui.material_design.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HeadBarActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.head_bar)
    HeadBar headBar;
    @BindView(R.id.btn_show_list_popup)
    TextView btnShowListPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_bar);
        ButterKnife.bind(this);

        headBar.SetTvRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupOverflowMenu(headBar);
            }
        });
        initListPopupWindow();

    }

    /**
     * 弹出自定义溢出菜单
     * 使用PopupWindow很简单，可以总结为三个步骤：
     * 创建PopupWindow对象实例；
     * 设置背景、注册事件监听器和添加动画；
     * 显示PopupWindow。
     */
    public void popupOverflowMenu(View view) {
        // 显示溢出菜单的时候设置背景半透明
//        setWindowAlpha(0.5f);
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // 状态栏高度 frame.top
        int yOffset = frame.top + headBar.getHeight() + dp2px(this, -1f); // 减去阴影宽度，适配UI
        int xOffset = dp2px(this, 0f); // 设置x方向offset为5dp

        View popView = getLayoutInflater().inflate(R.layout.menu_popup, null);

        // popView即popupWindow布局
        final PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 必须设计BackgroundDrawable后setOutsideTouchable(true)才会有效。这里在XML中定义背景，所以这里为null
        popupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true); // 点击外部关闭
//        popupWindow.setAnimationStyle(R.style.popmenu_animation);
        popupWindow.setAnimationStyle(R.style.popmenu_animation);
        // 设置Gravity,让它显示在右上角
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, xOffset, yOffset);
        popView.findViewById(R.id.tv_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Toast.makeText(HeadBarActivity.this, "点击了", Toast.LENGTH_SHORT).show();
            }
        });
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                // popupWindow消失时，设置为全透明
//                setWindowAlpha(1f);
//            }
//        });
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public int dp2px(Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void backClick(View view) {
        finish();
    }

    @OnClick(R.id.btn_show_list_popup)
    public void onViewClicked() {
        mPopup.show();
    }

    private ListPopupWindow mPopup;
    private String[] mGoodArray = {"pencil", "potato", "peanut", "carrot", "cabbage", "cat"};

    private void initListPopupWindow() {
        mPopup = new ListPopupWindow(this);
//        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, mGoodArray);
        List<String> strings = Arrays.asList(mGoodArray);
        ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(strings,this);
        mPopup.setAdapter(adapter);
        mPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup.setModal(true);
        mPopup.setOnItemClickListener(this);
//        mPopup.setHorizontalOffset(100);
//        mPopup.setVerticalOffset(100);
        mPopup.setAnchorView(btnShowListPopup);
        //动画效果有问题
//        mPopup.setAnimationStyle(R.style.popmenu_animation);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mPopup.dismiss();
    }
}
