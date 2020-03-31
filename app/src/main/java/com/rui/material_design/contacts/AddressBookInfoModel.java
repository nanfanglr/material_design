package com.rui.material_design.contacts;

import android.text.TextUtils;

import com.soyute.comservice.decoration.AbstractSuspension;
import com.soyute.comservice.helper.pinyin.SuspensionHelper;

import java.io.Serializable;

/**
 * C
 * 这是从本地拉取的数据
 */

public class AddressBookInfoModel extends AbstractSuspension implements Serializable {

    private String mobile;//通讯录的手机号
    private String sign;//邀请/已邀请------3/4
    private String name;//昵称/名字
    private String friend;//是否已经关注：已关注/互相关注-----1/2 ,

    public AddressBookInfoModel() {
    }

    public AddressBookInfoModel(String mobile, String name) {
        this.mobile = mobile;
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return name;
    }

    public void setNickName(String nickName) {
        this.name = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "AddressBookInfoModel{" +
                "mobile='" + mobile + '\'' +
                ", sign='" + sign + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public void setPinyin() {
        if (!TextUtils.isEmpty(name)) {
            nameToPinyin = SuspensionHelper.stringToPinyin(name.toUpperCase());
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof AddressBookInfoModel && !TextUtils.isEmpty(mobile)) {
            AddressBookInfoModel model = (AddressBookInfoModel) obj;
            return TextUtils.equals(mobile, model.getMobile());
        }
        return false;

    }
}
