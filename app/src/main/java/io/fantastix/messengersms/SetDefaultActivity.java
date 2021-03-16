package io.fantastix.messengersms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.okhttp.internal.Util;

public class SetDefaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_default);
        final String myPackageName = getPackageName();

        Button setDefaultBtn = findViewById(R.id.set_default);
        setDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SetDefaultActivity.this, "Hello world", Toast.LENGTH_SHORT).show();
                Log.i("SetDefaultActivity", "Set default");
                Log.i("SetDefaultActivity", Telephony.Sms.getDefaultSmsPackage(getApplicationContext()));
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MyApplication.isDefaultSmsApp(this)) {
            finish();
        }
    }
}