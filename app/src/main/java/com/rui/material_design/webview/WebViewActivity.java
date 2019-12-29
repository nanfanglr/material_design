package com.rui.material_design.webview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rui.material_design.R;

import org.w3c.dom.Text;

public class WebViewActivity extends AppCompatActivity implements JsBridge {

    private WebView webView;
    private TextView textView;
    private Button button;
    private EditText editText;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        handler = new Handler();

        webView = findViewById(R.id.webView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.edit);
        //允许webview加载js代码
        webView.getSettings().setJavaScriptEnabled(true);
        //给webview添加js接口
        webView.addJavascriptInterface(new JsInterface(this), "android");

        webView.loadUrl("file:///android_asset/webview.html");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = editText.getText().toString().trim();
                webView.loadUrl("javascript:if(window.remote){window.remote('" + str + "')}");
            }
        });

    }

    @Override
    public void setTextViewValue(final String value) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(value);
            }
        });

    }
}
