package com.rui.material_design.behavior;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rui.material_design.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FollowBehaviorActivity extends AppCompatActivity {

    @BindView(R.id.first)
    View first;
    @BindView(R.id.second)
    View second;

    private int cnta;
    private int min;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (cnta == 0) {
                min = (int) first.getY();
                cnta = min;
            }
            switch (msg.what) {
                case 1:
                    cnta = cnta + 1;
                    first.setY(cnta);
                    if (cnta < 700) {
                        handler.sendEmptyMessage(1);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                    break;
                case 2:
                    Log.i("TAG", "------------>cnta=" + cnta);
                    cnta = cnta - 1;
                    if (cnta > min) {
                        first.setY(cnta);
                        handler.sendEmptyMessage(2);
                    } else {
                        first.setY(min);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_behavior);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.first, R.id.second})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.first:
                handler.sendEmptyMessage(1);
                break;
            case R.id.second:
                break;
        }
    }
}
