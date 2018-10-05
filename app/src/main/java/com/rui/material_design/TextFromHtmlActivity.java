package com.rui.material_design;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextFromHtmlActivity extends AppCompatActivity {

    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.img_item_pic)
    ImageView imgItemPic;
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_sell_data)
    TextView tvSellData;
    @BindView(R.id.tv_discount_data)
    TextView tvDiscountData;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_color);
        ButterKnife.bind(this);
        tvNum.setText("1");
        tvProductName.setText(Html.fromHtml(String.format("%s(<font color='#db5153'>销售价：￥%d</font>)", "男T恤U7JI2L49", 58)));
        tvSellData.setText(Html.fromHtml(String.format("七天销量%d，现存%d，周转<font color='#db5153'>%.02f</font>，动销率<font color='#16A72A'>%.02f</font>", 25, 62, 7.50, 0.8)));
        tvDiscountData.setText(Html.fromHtml(String.format("今日销售折扣<font color='#16A72A'>%.01f折</font>&nbsp;&nbsp;&nbsp;&nbsp;季度累计销售折扣<font color='#16A72A'>%.01f折</font>", 8.2, 8.0)));
        tvTime.setText(Html.fromHtml(String.format("到店时间<font color='#4595D8'>%s</font>", "2018-09-01")));
    }
}
