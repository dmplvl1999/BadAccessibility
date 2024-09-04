package com.hackinghell.badaccessibility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hackinghell.badaccessibility.databinding.ActivityMainBinding;
import com.hackinghell.badaccessibility.databinding.FragmentWebViewBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'badaccessibility' library on application startup.
    static {
        System.loadLibrary("badaccessibility");
    }

    private ActivityMainBinding binding;
    private boolean loginState = true;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginstatefunction();
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        textView = binding.textView;
        areServicesEnabled();

        binding.webviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the buttons and text view
//                findViewById(R.id.textView).setVisibility(View.GONE);
//                findViewById(R.id.button).setVisibility(View.GONE);
//                findViewById(R.id.webview_btn).setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });
    }
    private void areServicesEnabled() {
        if (!isAccessServiceEnabled()) {
            //If accessibility is not enabled then it goes to the settings to make the user enable it
            textView.setText("Accessibility not enable");
            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendGETRequests();
                }
            });
        } else {
            // If the accessibility is enabled then the button can be clicked to open chrome
            textView.setText("Attacking is Enabled");
            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "In construction", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Determines if the accessibility service is enabled in the settings
    private boolean isAccessServiceEnabled() {
        int accessEnabled = 0;
        final String service = getPackageName() + "/" + myAccessibility.class.getCanonicalName();
        //System.out.println(service);
        try {
            accessEnabled = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return settingValue != null && settingValue.contains(service);
        }
        return false;
    }

    private void loginstatefunction() {
        if (loginState) {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void sendGETRequests() {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        acess myAccessAPI = retrofit.create(acess.class);
        Call<Void> call = myAccessAPI.successGet();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("GET request successful!");
                    Toast.makeText(MainActivity.this, "GET Successful", Toast.LENGTH_SHORT).show();
                    //here it gets the intent to enable the settings activity
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Enable the Accessibility Setting Malware", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("GET request failed with status code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("GET request failed: " + t.getMessage());
            }
        });
    }

    /**
     * A native method that is implemented by the 'badaccessibility' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}