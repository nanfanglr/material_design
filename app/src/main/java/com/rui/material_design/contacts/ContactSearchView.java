package com.rui.material_design.contacts;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rui.material_design.R;


/**
 *
 * 标题栏搜索
 */

public class ContactSearchView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "ContactSearchView";

    private Context mContext;


    private RelativeLayout toplayout;//布局顶部视图
    private ImageButton ib_back;//返回标志
    private ImageButton ib_clear;//清除标志
    private TextView tv_search;//搜索文字
    private EditText mSearchSrcTextView;//编辑搜索
    private RecyclerView mRecyclerView;//用于联想

    private SearchViewListener mSearchViewListener;//搜索状态监听
    private LinearLayout ll_recyclerview_layout;
    private TextView tv_searchview_titletext;


    public ContactSearchView(Context context) {
        this(context, null);
    }

    public ContactSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        init();

        initView();

        initData();

        initEvent();
    }

    protected void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_contact_search, this, true);

        toplayout = (RelativeLayout) findViewById(R.id.rl_searchview_toplayout);
        ib_back = (ImageButton) findViewById(R.id.ib_searchview_back);
        ib_clear = (ImageButton) findViewById(R.id.ib_searchview_clear);
        tv_search = (TextView) findViewById(R.id.tv_searchview_search);
        mSearchSrcTextView = (EditText) findViewById(R.id.et_searchview_edit);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_searchview);
        tv_searchview_titletext = (TextView) findViewById(R.id.tv_searchview_titletext);
//        ll_recyclerview_layout = (LinearLayout) findViewById(R.id.ll_recyclerview_layout);

//        tv_search.setEnabled(false);
    }

    protected void initView() {
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.insert_divider2_left_0));
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    protected void initData() {

    }

    protected void initEvent() {
        ib_back.setOnClickListener(this);
        ib_clear.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        //点击搜索监听
        mSearchSrcTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                onSubmitQuery();
                //失去焦点
                mSearchSrcTextView.clearFocus();

                //关闭键盘
                hideKeyboard(mSearchSrcTextView);

                return true;
            }
        });

        //编辑文字变化监听
        mSearchSrcTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ib_clear.setVisibility(TextUtils.isEmpty(s) ? GONE : VISIBLE);
//                tv_search.setEnabled(!TextUtils.isEmpty(s));
                if (mSearchViewListener != null) {
                    mSearchViewListener.onTextChanged(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }


    public void setHintText(String hintText) {
        if (!TextUtils.isEmpty(hintText)) {
            mSearchSrcTextView.setHint(hintText);
        }
    }

    public void setTitleText(String titleText) {
        if (!TextUtils.isEmpty(titleText)) {
            tv_searchview_titletext.setText(titleText);
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onClick(View v) {
        if (v == ib_back) {
            if (mSearchViewListener != null) {
                mSearchViewListener.onClickBack();
            }
        } else if (v == ib_clear) {
            mSearchSrcTextView.setText(null);
        } else if (v == tv_search) {
            if (tv_searchview_titletext.getVisibility() == View.VISIBLE) {
                tv_searchview_titletext.setVisibility(View.GONE);
                return;
            }
            onSubmitQuery();
        }
    }

    /**
     * 点击搜索后
     */
    private void onSubmitQuery() {
        //失去焦点
        mSearchSrcTextView.clearFocus();

        //关闭键盘
        hideKeyboard(mSearchSrcTextView);


        //搜索文字
        String query = mSearchSrcTextView.getText().toString().trim();
        Log.d(TAG, "搜索文字=" + query);
        if (!TextUtils.isEmpty(query)) {
            if (mSearchViewListener != null) {
                mSearchViewListener.onQueryTextSubmit(query);
            }

        }
    }

    /**
     * 搜索状态监听
     *
     * @param listener
     */
    public void setOnSearchViewListener(SearchViewListener listener) {
        mSearchViewListener = listener;
    }

    public interface SearchViewListener {

        /**
         * 点击返回icon
         */
        void onClickBack();

        /**
         * 点击搜索
         */
        void onClickSearch();

        /**
         * 点击搜索
         *
         * @param query 点击搜索后的 编辑栏的文字
         * @return
         */
        void onQueryTextSubmit(String query);

        /**
         * 编辑栏改变后的文字
         *
         * @param s
         */
        void onTextChanged(String s);

    }
}
