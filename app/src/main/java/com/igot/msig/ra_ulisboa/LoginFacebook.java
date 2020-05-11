package com.igot.msig.ra_ulisboa;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.igot.msig.ra_ulisboa.Utils.Constant;
import java.util.Arrays;

public class LoginFacebook extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_facebook);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setPermissions(Arrays.asList("public_profile")); //setReadPermissions ---------------------------
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback <LoginResult>() {
            @Override
            public void onSuccess( LoginResult loginResult ) {

                Constant.id = String.valueOf(loginResult.getAccessToken());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

            }

            @Override
            public void onCancel() {
            }
            @Override
            public void onError( FacebookException error ) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void onStart(){
        super.onStart();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            Constant.id = String.valueOf(accessToken);
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }
}