package com.rui.material_design;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * 定位帮助类
 */
public class LocationHelper {

    public static String cityName;  //城市名
    public static String provinceName;  //省名
    private Thread thread;
    private Context context;
    private LocationCallBack callBack;
    private static LocationManager locationManager;
    private Handler handler;


    public interface LocationCallBack {
        void onResult(String provinceName, String cityName);

        void onLocationResult(Location location);
    }


    public static void asyncGetCNBylocation(Context context, LocationCallBack callBack, Handler handler) {
        LocationHelper locationUtils = new LocationHelper(context, callBack, handler);
    }


    /**
     * 通过地理坐标获取城市名  其中CN分别是city和name的首字母缩写
     *
     * @param context
     */
    public LocationHelper(final Context context, LocationCallBack callBack, Handler handler) {

        this.context = context;
        this.callBack = callBack;
        this.handler = handler;

        try {
            if (thread == null) {//获取位子城市
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        start(context);
                    }
                });
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start(Context context) {

        //用于获取Location对象，以及其他
        String serviceName = Context.LOCATION_SERVICE;
        //实例化一个LocationManager对象
        locationManager = (LocationManager) context.getSystemService(serviceName);
        //provider的类型
        String provider = LocationManager.NETWORK_PROVIDER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //高精度
        criteria.setAltitudeRequired(false);    //不要求海拔
        criteria.setBearingRequired(false); //不要求方位
        criteria.setCostAllowed(false); //不允许有话费
        criteria.setPowerRequirement(Criteria.POWER_LOW);   //低功耗

/*        LogUtils.d("TAG", "----->ACCESS_FINE_LOCATION=" + ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION));
        LogUtils.d("TAG", "----->ACCESS_COARSE_LOCATION=" + ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION));*/

        //通过最后一次的地理位置来获得Location对象
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            handler.sendEmptyMessage(0);
        }
        Location location = locationManager.getLastKnownLocation(provider);//这个对象可以获取坐标经纬度
        Log.d("TAG", "----->GPS_PROVIDER=" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        Log.d("TAG", "----->NETWORK_PROVIDER=" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        if (callBack != null && location != null) {
            callBack.onLocationResult(location);
        }

        String queryed_name = updateWithNewLocation(context, location);
        String province_Name = updateWithNewLocation1(context, location);

        if ((queryed_name != null) && (0 != queryed_name.length())) {
            cityName = queryed_name;
            cloderGPS();
        }
        if ((province_Name != null) && (0 != province_Name.length())) {
            provinceName = province_Name;
        }

        freeBackResult();

        /*
         * 第二个参数表示更新的周期，单位为毫秒；第三个参数的含义表示最小距离间隔，单位是米
         * 设定每30秒进行一次自动定位
         */
        Looper.prepare();
//        context.getMainLooper();
        try {
            locationManager.requestLocationUpdates(provider, 3000, 50, locationListener);
        } catch (Exception e) {
        }
        Looper.loop();
        //移除监听器，在只有一个widget的时候，这个还是适用的
        locationManager.removeUpdates(locationListener);
    }

    private void freeBackResult() {
        if (!TextUtils.isEmpty(provinceName) && !TextUtils.isEmpty(cityName) && callBack != null) {
            callBack.onResult(provinceName, cityName);
        }
    }


    /**
     * 方位改变时触发，进行调用
     */
    private LocationListener locationListener = new LocationListener() {

        String tempCityName;
        String tempprovinceName;

        @Override
        public void onLocationChanged(Location location) {
            Log.d("TAG", "----->locationListener.location=" + location);
            tempCityName = updateWithNewLocation(context, location);
            tempprovinceName = updateWithNewLocation1(context, location);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {
                cityName = tempCityName;
                cloderGPS();
            }
            if ((tempprovinceName != null) && (tempprovinceName.length() != 0)) {
                provinceName = tempprovinceName;
            }

            freeBackResult();
        }

        @Override
        public void onProviderDisabled(String provider) {
            tempCityName = updateWithNewLocation(context, null);
            tempprovinceName = updateWithNewLocation(context, null);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {
                cityName = tempCityName;

                cloderGPS();
            }
            if ((tempprovinceName != null) && (tempprovinceName.length() != 0)) {
                provinceName = tempprovinceName;
            }

            freeBackResult();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }
    };

    private void cloderGPS() {
        if (locationManager != null) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
                locationListener = null;
            }
            locationManager = null;
        }
        if (locationListener != null) {
            locationListener = null;
        }

        if (thread != null) {
            try {
                thread.stop();
                thread = null;
            } catch (Exception e) {
            }
        }
    }


    /**
     * 更新location
     *
     * @param location
     * @return cityName
     */
    private static String updateWithNewLocation(Context context, Location location) {
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {
            System.out.println("无法获取地理信息");
        }

        try {

            Geocoder geocoder = new Geocoder(context);
            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName += add.getLocality();
//                add.getAdminArea();
            }
        }
        return mcityName;
    }

    /**
     * 更新location
     *
     * @param location
     * @return provinceName
     */
    private static String updateWithNewLocation1(Context context, Location location) {
        String mprovinceName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {

            System.out.println("无法获取地理信息");
        }

        try {
            Geocoder geocoder = new Geocoder(context);
            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mprovinceName += add.getAdminArea();
//                add.getAdminArea();
            }
        }
        return mprovinceName;
    }

    /**
     * 通过经纬度获取地址信息的另一种方法
     *
     * @param latitude
     * @param longitude
     * @return 城市名
     */
    public static String GetAddr(String latitude, String longitude) {
        String addr = "";

        /*
         * 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
         * 密钥可以随便写一个key=abc
         * output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
         */
        String url = String.format(
                "http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
                latitude, longitude);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {

            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {

            httpsConn = (URLConnection) myURL.openConnection();

            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(
                        httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                if ((data = br.readLine()) != null) {
                    String[] retList = data.split(",");
                    if (retList.length > 2 && ("200".equals(retList[0]))) {
                        addr = retList[2];
                    } else {
                        addr = "";
                    }
                }
                insr.close();
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return addr;
    }

    /**
     * 判断GPS导航是否打开.
     * false：弹窗提示打开,不建议采用在后台强行开启的方式。
     * true:不做任何处理
     *
     * @return
     */
    public static void isOpenGPS(final Activity activity) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            ComfirmCancelDialog.create(activity, "您还未开启定位，无法获取附近门店", "取消", "去开启",
//                    new ComfirmCancelDialog.ComfirmCancelDialogDialogListener() {
//                        @Override
//                        public void onClick(View v, int position, String password) {
//                            if (position == 1) {
//                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
//                            }
//                        }
//                    }).show();
           /* final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("GPS未打开，是否打开?");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();*/
        }
    }
}
