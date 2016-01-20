package com.androidtmc.fingerprint;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Signature;

@SuppressWarnings("ResourceType")
public class MainActivity extends AppCompatActivity {


    private Signature signature;
    private KeyguardManager keyguardManager;
    private FingerprintManager mFingerprintManager;
    private Button btnListen;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create key
       signature = CreateKeyHelper.create(this);

        //check has ready set fingerprint on device
         keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        mFingerprintManager = (FingerprintManager) getSystemService(Activity.FINGERPRINT_SERVICE);
        TextView tvMessage  = (TextView) findViewById(R.id.tvMessage);
        btnListen  = (Button) findViewById(R.id.btnListen);
        if(!mFingerprintManager.isHardwareDetected())
            tvMessage.setText("This device is not support!");
        if(!keyguardManager.isKeyguardSecure()){

            tvMessage.setText("Secure lock screen hasn't set up.\n" + "\" + \"Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint");
        }

        else if(!mFingerprintManager.hasEnrolledFingerprints()){
           tvMessage.setText("Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint");
        }
        else{
            tvMessage.setText("Please put your finger on device's finger sensor");
            listenTouchSenor();
        }

        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            listenTouchSenor();
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void listenTouchSenor(){
        //listen action user touch on finger sensor
        FingerprintManager.CryptoObject cryptObject = new FingerprintManager.CryptoObject(signature);
        CancellationSignal cancellationSignal = new CancellationSignal();
        FingerprintManager fingerprintManager =
                this.getSystemService(FingerprintManager.class);
        fingerprintManager.authenticate(cryptObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Error: "+errString.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                btnListen.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Success: "+result.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "fail ", Toast.LENGTH_SHORT).show();
            }
        }, null);
    }
}
