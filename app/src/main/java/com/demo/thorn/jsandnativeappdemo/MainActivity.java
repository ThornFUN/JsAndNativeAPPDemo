package com.demo.thorn.jsandnativeappdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;
    private Button btnDoJS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDoJS = findViewById(R.id.btn_fun);
        mWebView = findViewById(R.id.wv_content);
        mWebSettings = mWebView.getSettings();

        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        mWebView.addJavascriptInterface(this,"androidTag");//传入类对象，传入 androidTag 作为调用标志
        mWebView.setWebViewClient(new MyWebViewClient());//传入 MyWebViewClient 对象，达到接收 HTML 传递的点击动作
        mWebView.setWebChromeClient(new MyWebChromeClient());//传入 MyWebChormeClient 对象，达到对话框可用的目的

        mWebView.loadUrl("file:///android_asset/test.html");


        btnDoJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("javascript:doSomething()");// Activity 中调用 JS 的方法，可以加参数传值

                /* 如果调用 JS 时，需要获得返回值，则调用 evaluateJavascript 这个方法，示例如下：
                mWebView.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.e(TAG, "onReceiveValue value=" + value);
                    }
                });
                */
            }
        });
    }//onCreate 结束

    /*HTML 中被调用的方法*/
    @JavascriptInterface
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /*HTML 中被调用的方法*/
    @JavascriptInterface
    public void toAnotherAcitity(){
        Intent intent2 = new Intent(MainActivity.this,SecondActivity.class);
        startActivity(intent2);
    }

    /*HTML 中被调用的方法*/
    @JavascriptInterface
    public void showDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Title Here")
                .setCancelable(true)
                .setNegativeButton("cacel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast("negative");
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast("positive");
                    }
                })
                .setMessage("messages here ")
                .show();
    }

    private class MyWebViewClient extends WebViewClient {

        /*官方文档：Give the host application a chance to take over the control when a new url is about to be loaded in the current WebView.*/
        /*在加载一个新的 URL 链接之前进行的操作（一般都是对一些 URL 进行拦截）*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.equals("http://www.qq.com/")){
                showToast("拦截了 QQ 网站");
                return true;// 返回 true 表示拦截本次跳转；返回 false 表示继续跳转
            }else if(url.endsWith("test")){
                toAnotherAcitity();
                return true;
            }
            return false;
        }

        /*如果加载某个页面出错，跳转到本地的一个错误展示页面*/
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mWebView.loadUrl("file:///android_asset/errorPage.html");//加载本地的错误页面
            Log.i("jin",error.toString());
        }
    }

    //继承 WebChromeClient 管理 JS 调用 Native APP 得事件
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {


            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("jin","title="+title.toString());
        }
    }

    /*用于实现网页浏览的“返回”功能*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(mWebView.canGoBack()&& keyCode == KeyEvent.KEYCODE_BACK){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}