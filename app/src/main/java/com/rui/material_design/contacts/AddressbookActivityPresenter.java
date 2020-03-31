package com.rui.material_design.contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.soyute.comservice.database.helper.DBOpenHelper;
import com.soyute.comservice.database.table_model.AddressBookInfo;
import com.soyute.comservice.database.table_model.AddressBookInfoDao;
import com.soyute.comservice.database.table_model.DaoSession;
import com.soyute.comservice.model.UserInfo;
import com.soyute.comservice.service.GroupService;
import com.soyute.mvp.base.BasePresenter;
import com.soyute.mvp.base.model.ResultModel;
import com.supplychain.contacts.di.mvpview.AddressbookActivityView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 *
 */

public class AddressbookActivityPresenter extends BasePresenter<AddressbookActivityView> {

    private final GroupService groupService;

    public AddressbookActivityPresenter(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 获取本地数据库的数据集合
     */
    public void listAddressBook(Context context) {

        Timber.d("----------->>>>>>>>-----------listAddressBook");

        Observable.just(1)
                .map(integer -> {
                    List<AddressBookInfoModel> infoModelList = new ArrayList<AddressBookInfoModel>();

                    List<AddressBookInfoModel> infoModelListTemp = listAddressForDB(context);
                    if (infoModelListTemp == null || infoModelListTemp.size() == 0) {//如果不存在DB数据
                        Timber.d("----------->>>>>>>>-----------读取DB数据为空");
                        infoModelListTemp = listAddressBookForLocal(context);
                    }
                    if (infoModelListTemp != null && infoModelListTemp.size() > 0) {

                        infoModelListTemp = syncAddressBookToServer(infoModelListTemp);//同步后台数据
                        if (infoModelListTemp != null && infoModelListTemp.size() > 0) {
                            infoModelList.addAll(infoModelListTemp);
                        }
                    }


                    return infoModelList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    getView().showLoadingBar();
                })
                .doAfterTerminate(() -> {
                    getView().dismissLoadingBar();
                })
                .subscribe(list -> {
                    getView().listAddressBookResult(list);

                    new Thread(() -> {
                        Timber.d("----------->>>>>>>>-----------开始更新通讯录本地数据库");
                        listAddressBookForLocal(context);
                    }).start();

                }, throwable -> {

                });


    }

    private List<AddressBookInfoModel> syncAddressBookToServer(List<AddressBookInfoModel> localList) {
        Observable.fromIterable(localList)
                .map(new Function<AddressBookInfoModel, String>() {
                    @Override
                    public String apply(@NonNull AddressBookInfoModel model) throws Exception {
                        return model.getMobile();
                    }
                })
                .toList()
                .map(new Function<List<String>, String>() {
                    @Override
                    public String apply(@NonNull List<String> strings) throws Exception {
                        return TextUtils.join(",", strings);
                    }
                })
                .flatMap(new Function<String, SingleSource<ResultModel<List<AddressBookInfoModel>>>>() {
                    @Override
                    public SingleSource<ResultModel<List<AddressBookInfoModel>>> apply(@NonNull String mobiles) throws Exception {
                        return groupService.synAddressBook(mobiles, UserInfo.TOKEN);
                    }
                })
                .toObservable()
                .flatMap(new Function<ResultModel<List<AddressBookInfoModel>>, ObservableSource<AddressBookInfoModel>>() {
                    @Override
                    public ObservableSource<AddressBookInfoModel> apply(@NonNull ResultModel<List<AddressBookInfoModel>> resultModel) throws Exception {
                        return Observable.fromIterable(resultModel.getData());
                    }
                })
                .subscribe(signModel -> {
                    for (AddressBookInfoModel localModel : localList) {
                        if (TextUtils.equals(localModel.getMobile(), signModel.getMobile())) {
                            //②这里设置关系
                            localModel.setSign(signModel.getSign());
                            localModel.setFriend(signModel.getFriend());
                            break;
                        }
                    }
                }, throwable -> {
                    getView().showLoadingFailureError();
                    throwable.printStackTrace();
                });

        return localList;
    }


    /**
     * 从本地数据库提取数据
     *
     * @return
     */
    public List<AddressBookInfoModel> listAddressForDB(Context context) {
        DaoSession daoSession = DBOpenHelper.getDaoSession(context);
        AddressBookInfoDao infoDao = daoSession.getAddressBookInfoDao();
        QueryBuilder<AddressBookInfo> queryBuilder = infoDao.queryBuilder();
        List<AddressBookInfo> infoList = queryBuilder.build().list();


        List<AddressBookInfoModel> list = new ArrayList<>();

        Observable.fromIterable(infoList)
                .map(new Function<AddressBookInfo, AddressBookInfoModel>() {
                    @Override
                    public AddressBookInfoModel apply(@NonNull AddressBookInfo addressBookInfo) throws Exception {
                        Timber.d("----------->>>>>>>>-----------读取通讯录结果数据:" + addressBookInfo.toString());
                        AddressBookInfoModel model = new AddressBookInfoModel();

                        //①这里设置手机号和昵称
                        model.setMobile(addressBookInfo.getMobile());
                        model.setNickName(addressBookInfo.getName());

                        return model;
                    }
                })
                .filter(new Predicate<AddressBookInfoModel>() {
                    @Override
                    public boolean test(@NonNull AddressBookInfoModel model) throws Exception {
                        return !list.contains(model);
                    }
                })
                .subscribe(model -> {
                    list.add(model);
                }, throwable -> {
                    Timber.d("----------->>>>>>>>-----------本地数据库为空");
                });


        return list;


    }


    /**
     * 获取手机机主的通讯录集合
     *
     * @return
     */
    private List<AddressBookInfoModel> listAddressBookForLocal(Context context) {
        List<AddressBookInfoModel> infoModelList = new ArrayList<>();

        AddressBookInfoModel infoModel = null;

        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{"display_name", "sort_key", "contact_id",
                        "data1", "photo_uri"}, null, null, null);
//        moveToNext方法返回的是一个boolean类型的数据

        while (cursor.moveToNext()) {
            AddressBookInfo info = null;
            //读取通讯录的姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String img = cursor.getString(4);
            //读取通讯录的号码
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            int Id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//            String Sortkey = getSortkey(cursor.getString(1));

            String mobile = getRightMobiel(number);

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobile)) {
                infoModel = new AddressBookInfoModel(mobile, name);
                Timber.d("----------->>>>>>>>-----------infoModel:" + infoModel.toString());
                if (!infoModelList.contains(infoModel)) {
                    infoModelList.add(infoModel);
                }
            }


        }

        DaoSession daoSession = DBOpenHelper.getDaoSession(context);
        AddressBookInfoDao infoDao = daoSession.getAddressBookInfoDao();
        infoDao.deleteAll();
        Observable.fromIterable(infoModelList)
                .map(new Function<AddressBookInfoModel, AddressBookInfo>() {
                    @Override
                    public AddressBookInfo apply(@NonNull AddressBookInfoModel model) throws Exception {
                        AddressBookInfo info = new AddressBookInfo();
                        info.setName(model.getNickName());
                        info.setMobile(model.getMobile());

                        return info;
                    }
                })
                .subscribe(info -> {
                    long insert = infoDao.insert(info);
                    Timber.d("----------->>>>>>>>-----------" + String.format("数据库无手机号:%s,执行插入id:%s", info.getMobile(), insert + ""));
                });

        return infoModelList;
    }


    public void inviteFriend(AddressBookInfoModel model) {
        composite.add(groupService
                .inviteFriend(model.getMobile(), model.getNickName(), UserInfo.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    getView().showLoadingBar();
                })
                .doAfterTerminate(() -> {
                    getView().dismissLoadingBar();
                })
                .subscribe(resultModel -> {
                    getView().inviteFriendResult(resultModel, model);
                }, throwable -> {
                    getView().showLoadingFailureError();
                })
        );
    }

    /**
     * 将通讯录的手机号验证并提取出(必须号码规范,且是11位纯数字)
     *
     * @param mobile
     * @return
     */
    private String getRightMobiel(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return null;
        }


        //去除除数字以外的其他符号
        char[] chars = mobile.toCharArray();

        mobile = "";

        for (char aChar : chars) {
            if ((int) aChar >= 48 && (int) aChar <= 57) {
                mobile += aChar;
            }
        }


//        mobile = mobile.replaceAll(" ", "");
//        mobile = mobile.replaceAll("-", "");

        /**国家号码段分配如下：

         移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188

         联通：130、131、132、152、155、156、185、186

         电信：133、153、180、189、（1349卫通）*/
        String[] mobileFirst = new String[]{
                "134", "135", "136", "137", "138", "139", "150", "151", "157", "158", "159", "187", "188",
                "130", "131", "132", "152", "155", "156", "185", "186",
                "133", "153", "180", "189", "1349"

        };

        //提取11位数的手机号
        int index = -1;
        for (String first : mobileFirst) {
            index = mobile.indexOf(first);
            if (index >= 0) {
                break;
            }
        }

        if (index < 0) {
            return null;
        }

        mobile = mobile.substring(index);

        if (mobile.length() > 11) {
            mobile = mobile.substring(0, 11);
        }

        //如果不是正确的手机号
        if (!isMobileNO(mobile)) {
            return null;
        }

        return mobile;

    }

    public boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

}
