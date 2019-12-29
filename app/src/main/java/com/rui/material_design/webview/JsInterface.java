package com.rui.material_design.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsInterface {

    private JsBridge jsBridge;

    public JsInterface(JsBridge jsBridge) {
        this.jsBridge = jsBridge;
    }

    /**
     * 这个方法不在主线程执行
     * @param value
     */
    @JavascriptInterface
    public void setValue(String  value){
        Log.d(this.getClass().getSimpleName(),value);
        jsBridge.setTextViewValue(value);
        // 这里抛异常不会使app崩溃，在html会收到一个异常
        // throw new RuntimeException("");
    }

    /**
     *java与js传递参数的类型不一致（数组和对象），例如js数组可以使不同类型，而java则必须使统一类型
     * 解决将数组或对象转成json，用String传过去
     * @param array
     */
    @JavascriptInterface
    public void getArray(int[] array){
        for (int i = 0; i < array.length; i++) {
            Log.d(this.getClass().getSimpleName(),String.valueOf(array[i]));
        }

    }

    //html端字符串类型为空时传递undefined

    // web端不进行对象存在的判断
}
