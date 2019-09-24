package com.aituolink.smartbox;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import android_serialport_api.SerialControl;
import android_serialport_api.SerialServer;

/**
 * author:szjz
 * date:2019/8/6
 */
public class MyJavascriptInterface {
    private WebView webView;
    private MainActivity activity;

    public MyJavascriptInterface(MainActivity activity, WebView webView) {
        this.webView = webView;
        this.activity = activity;
    }

    /**
     * 获取mac地址
     */
    @JavascriptInterface
    public void getMacAddress() {

//        final String macAddress = Public.getLocalMacAddressByIp(activity);
        final String macAddress1 = Public.getLocalMacAddressByWifi(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:macAddress('" + macAddress1 + "')");
            }
        });
    }

    /** 接收前端传过来的JSESSIONID */
    @JavascriptInterface
    public void getJsessionId(String jsessionId) {
        activity.getJSessionId(jsessionId);
    }

    /**
     * 开箱操作
     */
    @JavascriptInterface
    public void openBoxAndroid(final String data) {
        Log.e("openBoxAndroid", data);
        //和硬件交互的开箱操作
        final String result = openBox(1, Integer.valueOf(data));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:openBoxJs('" + result + "')");
            }
        });
    }

    /**
     * 监测柜门的状态主要是关闭
     */
    @JavascriptInterface
    public void boxStatusAndroid(final Integer boxNo, final Integer doorNo) {
        Log.i("boxNo:", String.valueOf(boxNo));
        Log.i("doorNo:", String.valueOf(doorNo));
        //boxStatus: Close/Open
        final SerialControl.ResultMsg boxStatus = SerialServer.getInstance().IsBoxOpen(boxNo, doorNo, 3000);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:boxStatusJs('" + boxStatus + "')");
            }
        });
    }

    /**
     * 传入箱体号 和柜子门牌号 即可开箱
     */
    private String openBox(Integer boxNo, Integer doorNo) {

        SerialControl.ResultMsg resultMsg = SerialServer.getInstance().OpenBox(boxNo, doorNo, 3000);

        String result = null;

        if (resultMsg.getErrType().equals(SerialControl.ErrorType.No_Error)) {
            Log.i("1LockTest", "ok");
            result = "ok";
//            Toast.makeText(activity.getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
        } else {

            if (resultMsg.getErrMsg().length() != 0) {
                result = resultMsg.getErrMsg();
                Log.i("2LockTest", resultMsg.getErrMsg());
//                Toast.makeText(activity.getApplicationContext(), resultMsg.getErrMsg(), Toast.LENGTH_SHORT).show();
            } else {
                result = resultMsg.getErrMsgById(resultMsg.getErrType());
                Log.i("3LockTest", resultMsg.getErrMsgById(resultMsg.getErrType()));
//                Toast.makeText(activity.getApplicationContext(), resultMsg.getErrMsgById(resultMsg.getErrType()), Toast.LENGTH_SHORT).show();
            }

        }
        return result;
    }

}
