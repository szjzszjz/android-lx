package com.aituolink.smartbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * author:szjz
 * date:2019/8/6
 */
public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityweb);

        WebView webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        //解决一些图片加载问题
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("------webview", "url: " + url);
                view.loadUrl(url);
                return true;
            }
        });

        //加载网页
//        webView.loadUrl("http://dev.benma-code.com/w/test/m-lexiang-cabinet/");
//        webView.loadUrl("http://dev.benma-code.com/w/test/m-lexiang-cabinet/test/#/home");
        webView.loadUrl("http://dev.benma-code.com/w/test/m-lexiang-shop-cabinet/#/home");

//        这里的"injectedObject"对应js里的"window.injectedObject.startFunction()"，
        webView.addJavascriptInterface(new MyJavascriptInterface(new MyActivity(),webView), "injectedObject");
    }
}
