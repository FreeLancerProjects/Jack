package com.endpoint.Jack.activities_fragments.telr_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.endpoint.Jack.R;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.PayPalLinkModel;

import java.util.Locale;

import io.paperdb.Paper;

public class TelrActivity extends AppCompatActivity {
    private PayPalLinkModel payPalLinkModel;
    private boolean isForm = false;
    private String url;
private WebView webView;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language_Helper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data"))
        {
            payPalLinkModel = (PayPalLinkModel) intent.getSerializableExtra("data");
        }
    }

    private void initView() {

        url =payPalLinkModel.getData().getUrl();
        webView=findViewById(R.id.webView);
        webView.loadUrl(url);

       webView.getSettings().setJavaScriptEnabled(true);
       webView.getSettings().setBuiltInZoomControls(true);
       webView.getSettings().setDisplayZoomControls(false);
       webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.contains("success")||url.contains("checkout/done"))
                {
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(TelrActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }


}
