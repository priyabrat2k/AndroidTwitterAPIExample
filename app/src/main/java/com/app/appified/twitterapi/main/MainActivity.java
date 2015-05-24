package com.app.appified.twitterapi.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends AppCompatActivity implements FragmentTaskCompleteListener {

    Button btnLogin;
    Twitter twitter;
    RequestToken requestToken;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enabling Strict Mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(Config.PREF_NAME,0);
        boolean status = sharedPreferences.getBoolean(Config.KEY_TWITTER_LOGIN,false);
        if(status){
           Intent intent = new Intent(getApplicationContext(),HomePage.class);
           startActivity(intent);
           finish();
        }
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(Config.KEY_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(Config.KEY_CONSUMER_SECRET);

                final Configuration configuration = builder.build();
                final TwitterFactory factory = new TwitterFactory(configuration);
                twitter = factory.getInstance();

                try {
                    requestToken = twitter.getOAuthRequestToken(Config.KEY_CALLBACK_URL);
                    String authUrl = requestToken.getAuthenticationURL();
                    // Open Fragment Dialog
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    LoginDialogFragment dialogFragment = LoginDialogFragment.getInstance(authUrl);
                    dialogFragment.show(fragmentManager,"appified_tag_priyabrat");

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTaskComplete(String verifierUrl) {
        try{
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifierUrl);
            saveUserData(accessToken);
            long userID = accessToken.getUserId();
            final User user = twitter.showUser(userID);
            String username = user.getName();
            Toast.makeText(getApplicationContext(),"UserName is: "+username,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),HomePage.class);
            startActivity(intent);
            finish();
        }catch (Exception e){
            Log.d("Error",e+"");
        }
    }

    public void saveUserData(AccessToken accessToken){
            long userID = accessToken.getUserId();
            User user;
            try {
                user = twitter.showUser(userID);
                String username = user.getName();
			/* Storing oAuth tokens to shared preferences */
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString(Config.KEY_OAUTH_TOKEN, accessToken.getToken());
                e.putString(Config.KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                e.putBoolean(Config.KEY_TWITTER_LOGIN, true);
                e.putString(Config.KEY_USER_NAME, username);
                e.commit();
            } catch (TwitterException e1) {
                e1.printStackTrace();
            }
    }
}
