package com.app.appified.twitterapi.main;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class HomePage extends AppCompatActivity {

    TextView textViewWelcomeMsg;
    EditText editTextComment;
    Button btnSendPost;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        textViewWelcomeMsg = (TextView) findViewById(R.id.welcomeMsg);
        editTextComment = (EditText) findViewById(R.id.editTextComment);
        btnSendPost = (Button) findViewById(R.id.btnSend);
        SharedPreferences preferences = getSharedPreferences(Config.PREF_NAME,0);
        userName = preferences.getString(Config.KEY_USER_NAME,"");
        textViewWelcomeMsg.setText(userName);
        btnSendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostUpdateToTwitter(HomePage.this,editTextComment.getText().toString(),R.drawable.image_to_send).execute();
            }
        });
    }
}
