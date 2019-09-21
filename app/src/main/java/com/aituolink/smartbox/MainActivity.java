package com.aituolink.smartbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android_serialport_api.SerialControl;
import android_serialport_api.SerialServer;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity {


    private SharedPreferences sp;
    private String _url = "http://dev.benma-code.com/w/test/m-lexiang-shop-cabinet/#/home";

    //----------------------------------------------------关闭串口
    private void closeComPort() {
        if (SerialServer.getInstance() != null) {
            SerialServer.getInstance().stopSend();
            SerialServer.getInstance().close();
        }
    }

    //----------------------------------------------------开启串口
    private void openComPort() {
        try {
            SerialServer.getInstance().Init("/dev/ttyS3", SerialControl.LockBoardType.ATAT);
        } catch (SecurityException e) {
            //ShowMessage("打开串口失败:没有串口读/写权限!");
            Log.i("", e.getMessage());
        } catch (Exception e) {
            //ShowMessage("打开串口失败:参数错误!");
            Log.i("", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获得SharedPreferences的实例 sp_cookie是文件名
        sp = this.getSharedPreferences("sp_cookie", Context.MODE_PRIVATE);

        //开启串口
        openComPort();
        //加载web网页
        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");  // 设置编码
        webSettings.setTextZoom(100);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  //自适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true); // 支持js
        webSettings.setDomStorageEnabled(true);
        webSettings.setBlockNetworkImage(false);//解决一些图片加载问题
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //不缓存

        MyJavascriptInterface jsBridge = new MyJavascriptInterface(this, webView);// 设置本地调用对象及其接口
        webView.addJavascriptInterface(jsBridge, "app");

        webView.loadUrl(_url); //加载网页

        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//                Log.e("onPageFinished",url);
//                super.onPageFinished(view, url);
//            }

            @SuppressLint("LongLogTag")
            // 要想执行此方法 前端页面必须用超链接跳转(window.location.href = "https://dev.benma-code.com/w/test/m-lexiang-shop-cabinet/index.html#/bind";)
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("shouldOverrideUrlLoading",url);
                //必须在loadUrl之前同步cookie
                syncCookie(url);
                view.loadUrl(url);
                return false;
            }
        });
    }

    /** 同步cookie */
    private void syncCookie(String url) {
        Log.e("syncCookie",url);
        try {
            CookieSyncManager.createInstance(getApplicationContext());
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            //移除之前的cookie 重新设定(这里的cookie是后台返回的jsessionId)
            cookieManager.removeSessionCookie();

            //log
            String oldCookie = cookieManager.getCookie(url);
            if (oldCookie != null) {
                android.util.Log.e("oldCookie:", oldCookie);
            }

            String cookie = sp.getString("jsessionId", "");
            String[] cookies = cookie.split(";");
            if (!TextUtils.isEmpty(cookie)) {
                for (String cook : cookies) {
                    // 最好将cookie绑定在域名下 否则失败
                    cookieManager.setCookie(getDomain(url), cook.trim()); //一定要一个一个set, 如果是拼接的话，设置不成功。
                }
            }

            // 同步
            CookieSyncManager.getInstance().sync();

            //验证cookie是否设置成功
            String newCookie = cookieManager.getCookie(url);
            if (newCookie != null) {
                android.util.Log.e("newCookie",  newCookie);
            }
        } catch (Exception e) {
            android.util.Log.e("Exception" , e.toString());
        }
    }

    // 接收前端传过来的JSESSIONID 并持久化
    public void getJSessionId(String sessionId) {
        String JSESSIONID = "JSESSIONID=" + sessionId;
        sp.edit().putString("jsessionId", JSESSIONID).apply();
    }

    /**
     * 获取URL的域名
     */
    private String getDomain(String url) {
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf('/'));
        }
        Log.e("getDomain",url);
        return url;
    }

}
