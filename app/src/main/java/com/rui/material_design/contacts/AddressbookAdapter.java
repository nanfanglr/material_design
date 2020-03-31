package com.rui.material_design.contacts;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.soyute.comservice.decoration.ISuspensionAdapterInterface;
import com.soyute.comservice.model.contact.AddressBookInfoModel;
import com.supplychain.contacts.R;

import java.util.List;

/**
 *
 */

public class AddressbookAdapter extends BaseQuickAdapter<AddressBookInfoModel, BaseViewHolder> implements ISuspensionAdapterInterface {

    public AddressbookAdapter(@Nullable List<AddressBookInfoModel> data) {
        super(R.layout.item_addressbook_adapter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddressBookInfoModel item) {
        helper.itemView.setTag(item);//用于字母表

        //姓名
        helper.setText(R.id.tv_address_name, item.getNickName());


        //关注
        helper.setVisible(R.id.tv_address_attentioned, false);//已关注
        helper.setVisible(R.id.tv_address_togetherattention, false);//相互关注
        helper.setVisible(R.id.tv_address_invited, false);//已邀请
        helper.setVisible(R.id.tv_address_invite, false);//邀请

        if (TextUtils.equals("2", item.getFriend())) {//相互关注
            helper.setVisible(R.id.tv_address_togetherattention, true);
        } else if (TextUtils.equals("1", item.getFriend())) {//已关注
            helper.setVisible(R.id.tv_address_attentioned, true);
        } else if (TextUtils.equals("4", item.getSign())) {//已邀请
            helper.setVisible(R.id.tv_address_invited, true);
        } else {//邀请
            helper.setVisible(R.id.tv_address_invite, true);
        }

        helper.addOnClickListener(R.id.tv_address_invite);

    }

    public void changeState(AddressBookInfoModel model) {
        if (model != null && mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                AddressBookInfoModel infoModel = mData.get(i);
                if (TextUtils.equals(infoModel.getMobile(), model.getMobile())) {
                    infoModel.setSign("4");
                    notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    /**
     * 根据给定的字符串获取悬浮view所在的position
     *
     * @param suspensiontText
     * @return
     */
    public int getPositionBySuspensionText(String suspensiontText) {
        if (TextUtils.isEmpty(suspensiontText)) {
            return -1;
        }
        if (mData == null || mData.isEmpty()) {
            return -1;
        }
        int i = 0;
        for (AddressBookInfoModel addressBookInfoModel : mData) {
            if (addressBookInfoModel.isShowAlphabet() && TextUtils.equals(addressBookInfoModel.getSuspensionTitleText(), suspensiontText)) {
                return i + 1;
            }

            i++;
        }
        return -1;
    }
}
