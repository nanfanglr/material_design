package com.rui.material_design.contacts;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.soyute.comservice.decoration.FriendDividerDecoration;
import com.soyute.comservice.decoration.SuspensionDecoration;
import com.soyute.comservice.helper.pinyin.SuspensionHelper;
import com.soyute.comservice.model.contact.AddressBookInfoModel;
import com.soyute.mvp.base.BaseActivity;
import com.soyute.mvp.base.model.ResultModel;
import com.soyute.viewkit.widget.toast.ToastUtils;
import com.supplychain.contacts.R;
import com.supplychain.contacts.R2;
import com.supplychain.contacts.adapter.AddressbookAdapter;
import com.supplychain.contacts.di.component.AddressbookActivityComponent;
import com.supplychain.contacts.di.factory.ContactComponentFactory;
import com.supplychain.contacts.di.mvpview.AddressbookActivityView;
import com.supplychain.contacts.di.presenter.AddressbookActivityPresenter;
import com.supplychain.contacts.di.viewstate.AddressbookActivityViewState;
import com.supplychain.contacts.view.ContactSearchView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import timber.log.Timber;

/**
 *
 * 管理通讯录的界面,该数据第一次来源于接口,以后来源于数据库,但是会让子线程同步后台数据
 */

public class AddressbookActivity extends BaseActivity<AddressbookActivityComponent, AddressbookActivityView,
        AddressbookActivityPresenter, AddressbookActivityViewState> implements AddressbookActivityView, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R2.id.recyclerview_addressbook)
    RecyclerView mRecyclerView;
    @BindView(R2.id.searchview_addressbook)
    ContactSearchView contactSearchView;
    @BindView(R2.id.indexbar_addressbook)
    IndexBar mIndexBar;


    private AddressbookAdapter mAdapter;
    private AddressbookAdapter mAdapterSearch;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerViewSearch;
    private List<AddressBookInfoModel> allDadas = new ArrayList<>();

    @Override
    protected AddressbookActivityComponent constructComponent() {
        return ContactComponentFactory.getAddressbookActivityComponent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_addressbook;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permission < 0) {
            ToastUtils.showToast("未获取读取通讯录权限!");
            return;
        }

        mRecyclerViewSearch = contactSearchView.getRecyclerView();

        initView();

        initData();

        initEvent();
    }

    private void initView() {
        mAdapter = new AddressbookAdapter(allDadas);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        SuspensionDecoration suspensionDecoration = new SuspensionDecoration(this);
        mRecyclerView.addItemDecoration(suspensionDecoration);
        mRecyclerView.addItemDecoration(new FriendDividerDecoration(this));

        mRecyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        mAdapterSearch = new AddressbookAdapter(new ArrayList<>());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerViewSearch.setAdapter(mAdapterSearch);
        mRecyclerViewSearch.addItemDecoration(dividerItemDecoration);

        contactSearchView.setHintText("搜索通讯录好友");
        contactSearchView.setTitleText("通讯录好友");
    }

    private void initData() {
        getPresenter().listAddressBook(this);
    }

    private void initEvent() {
        mAdapter.setOnItemChildClickListener(this);
        mAdapterSearch.setOnItemChildClickListener(this);

        contactSearchView.setOnSearchViewListener(new ContactSearchView.SearchViewListener() {
            @Override
            public void onClickBack() {
                AddressbookActivity.this.finish();
            }

            @Override
            public void onClickSearch() {

            }

            @Override
            public void onQueryTextSubmit(String query) {

            }

            @Override
            public void onTextChanged(String s) {
                String searchText = s.toString();
                if (TextUtils.isEmpty(searchText)) {
                    mRecyclerViewSearch.setVisibility(View.GONE);
                    Timber.d("----------->>>>>>>>-----------搜索文字为空");
                } else {

                    Timber.d("----------->>>>>>>>-----------搜索文字:" + searchText);

                    if (allDadas != null && allDadas.size() != 0) {

                        List<AddressBookInfoModel> searchList = new ArrayList<AddressBookInfoModel>();

                        for (AddressBookInfoModel model : allDadas) {
                            if (!TextUtils.isEmpty(model.getNickName())) {
                                if (model.getNickName().contains(searchText) || model.getMobile().contains(searchText)) {
                                    if (!searchList.contains(model)) {
                                        searchList.add(model);
                                        Timber.d("----------->>>>>>>>-----------搜索添加:" + model.toString());
                                    }
                                }
                            }
                        }

                        if (searchList.size() != 0) {
                            mAdapterSearch.setNewData(searchList);
                            mRecyclerViewSearch.setVisibility(View.VISIBLE);
                        } else {
                            mRecyclerViewSearch.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        mIndexBar.setListener(new IndexBar.onIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (!TextUtils.isEmpty(text)) {
                    if (TextUtils.equals(text, IndexBar.firstText)) {
                        mLayoutManager.scrollToPositionWithOffset(0, 0);
                    } else {

                        int position = mAdapter.getPositionBySuspensionText(text);
                        if (position >= 0) {
                            mLayoutManager.scrollToPositionWithOffset(position, 0);
                        }
                    }


                }
            }

            @Override
            public void onMotionEventEnd() {
            }
        });

    }

    @Override
    public void inviteFriendResult(ResultModel<Object> resultModel, AddressBookInfoModel model) {
        if (resultModel != null) {
            if (resultModel.isSuccess()) {
                ToastUtils.showToast("邀请成功!");
                mAdapter.changeState(model);
                mAdapterSearch.changeState(model);
            } else {
                ToastUtils.showToast(resultModel.getErrMsg());
            }
        }
    }

    @Override
    public void listAddressBookResult(List<AddressBookInfoModel> list) {

        if (allDadas == null) {
            allDadas = new ArrayList<>();
        }
        allDadas.clear();
        if (list != null && list.size() != 0) {
            allDadas.addAll(list);
        } else {
            ToastUtils.showToast("未获取手机通讯录");
        }
        allDadas = SuspensionHelper.setSuspensionInfo(allDadas);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.tv_address_invite) {
            Object object = adapter.getItem(position);
            if (object != null && object instanceof AddressBookInfoModel) {
                AddressBookInfoModel item = (AddressBookInfoModel) object;
                if (item != null) {
                    getPresenter().inviteFriend(item);
                }
            }
        }
    }
}
