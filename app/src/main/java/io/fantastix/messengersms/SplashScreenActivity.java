package io.fantastix.messengersms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.fantastix.messengersms.chats.SMSActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, SMSActivity.class);
                startActivity(intent);
                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }
}
