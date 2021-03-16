package io.fantastix.messengersms;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static io.fantastix.messengersms.MyApplication.isDefaultSmsApp;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        mSplashScreenActivityBinding = DataBindingUtil.setContentView(this, R.layout.splash_screen_activity);

//        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(SplashScreenActivity.this, R.anim.logo_animation);
//        set.setTarget(mSplashScreenActivityBinding.ivLogo);
//        set.start();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//        final String myPackageName = getPackageName();
//        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
//            // App is not default.
//            // Show the "not currently set as the default SMS app" interface
//            View viewGroup = findViewById(R.id.not_default_app);
//            viewGroup.setVisibility(View.VISIBLE);
//
//            // Set up a button that allows the user to change the default SMS app
//            Button button = findViewById(R.id.set_default);
//            button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
//                    startActivity(intent);
//                }
//            });
//        } else {
//            // App is the default.
//            // Hide the "not currently set as the default SMS app" interface
//            View viewGroup = findViewById(R.id.not_default_app);
//            viewGroup.setVisibility(View.GONE);
//        }
        Intent intent;
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())/*isDefaultSmsApp(this)*/) {
            intent = new Intent(SplashScreenActivity.this, SMSActivity.class);
        }
        else {
            intent = new Intent(SplashScreenActivity.this, SetDefaultActivity.class);
        }
        startActivity(intent);
        finish();
//            }
//        }, SPLASH_TIME_OUT);

    }
}
