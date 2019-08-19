package com.aituolink.smartbox;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * author:szjz
 * date:2019/8/6
 */
public class MyJavascriptInterface {
    private WebView webView;
    private MyActivity activity;

    public MyJavascriptInterface(MyActivity activity, WebView webView) {
        this.webView = webView;
        this.activity = activity;
    }

    /**
     * 前端代码嵌入js：
     * imageClick 名应和js函数方法名一致
     *
     * @param src 图片的链接
     */
    @JavascriptInterface
    public void imageClick(String src) {
        Log.e("imageClick", "----点击了图片");
        Log.e("src", src);
    }

    /**
     * 前端代码嵌入js
     * 遍历<li>节点
     *
     * @param type    <li>节点下type属性的值
     * @param item_pk item_pk属性的值
     */
    @JavascriptInterface
    public void textClick(String type, String item_pk) {
        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(item_pk)) {
            Log.e("textClick", "----点击了文字");
            Log.e("type", type);
            Log.e("item_pk", item_pk);
        }
    }

    /**
     * 网页使用的js，方法无参数
     */
    @JavascriptInterface
    public void startFunction() {
        Log.e("startFunction", "----无参");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:javacalljs()");
            }
        });
    }

    /**
     * 网页使用的js，方法有参数，且参数名为data
     *
     * @param data 网页js里的参数名
     */
//    @JavascriptInterface
//    public void startFunction(String data) {
//        Log.e("startFunction", data);
//        //无参数调用
//        // 传递参数调用
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                webView.loadUrl("javascript:javacalljswithargs('" + "我是Android传过来的数据" + "')");
//            }
//        });
//
//    }

    int num = 0;
    @JavascriptInterface
    public void clicktitle(final String data) {
        Log.e("startFunction", data);
       num ++;
        //无参数调用
        // 传递参数调用
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:updatetitle('" +data+num+ "')");
            }
        });

    }


//    andoroid 调用vue中的方法：
//    mounted() {
//        // 将方法挂载到vue上
//        window.updatetitle = this.updatetitle;
//    },
//    methods: {
//        // 接收安卓端的方法
//        updatetitle(data) {
//            alert(data);
//            this.title = data;
//        },
//    },
}
