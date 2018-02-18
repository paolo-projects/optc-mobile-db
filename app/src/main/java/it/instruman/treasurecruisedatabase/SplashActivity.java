package it.instruman.treasurecruisedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by infan on 28/09/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {Thread.sleep(2000);}catch(Exception e) {e.printStackTrace();}
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
