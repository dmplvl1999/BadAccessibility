package com.hackinghell.badaccessibility;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebViewActivity extends AppCompatActivity {

    private boolean isLocationPortugal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CheckIfLocationIsPortugal();
    }

    private void CheckIfLocationIsPortugal() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://ip-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IpLocation ipLocation = retrofit.create(IpLocation.class);
        Call<IpApiResponse> call = ipLocation.getIpLocation();

        call.enqueue(new Callback<IpApiResponse>() {
            @Override
            public void onResponse(Call<IpApiResponse> call, Response<IpApiResponse> response) {
                //Log.i("facebook", "success but no code");
                if (response.isSuccessful()) {
                    IpApiResponse location = response.body();
                    if ("PT".equals(location.getCountryCode())) {
                        isLocationPortugal = true;
                        Log.i("facebook", "User is in Portugal");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isLocationPortugal",isLocationPortugal);
                        Fragment webViewFrag = WebViewFragment.newInstance();
                        webViewFrag.setArguments(bundle);
                        //Start a transaction
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        // Replace the content of the container with the fragment
                        fragmentTransaction.replace(R.id.fragment_container, webViewFrag); // commit transaction
                        fragmentTransaction.commit();
                    } else {
                        Log.i("facebook", "Location is not Portugal");
                        Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<IpApiResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("facebook", "no success");
            }
        });
    }
}