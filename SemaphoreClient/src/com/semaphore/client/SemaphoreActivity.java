package com.semaphore.client;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import android.content.Intent;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class SemaphoreActivity extends Activity 
{
   static final String CLASS_NAME = "SemaphoreActivity";
   
   private static final String FACEBOOK_SEMAPHORE_DEV_APP_ID = "249369708438212";
   
   Facebook facebook = new Facebook( FACEBOOK_SEMAPHORE_DEV_APP_ID );
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       
       //facebook.authorize(this, new String[] {"friends_about_me"}, new DialogListener() {
       facebook.authorize(this, new DialogListener() {
          @Override
          public void onComplete(Bundle values) {}
          
          @Override
          public void onFacebookError(FacebookError error) {}
          
          @Override
          public void onError(DialogError e) {}
          
          @Override
          public void onCancel() {}
       });
       
       try
       {
          //TODO: mimic the AsyncFacebookRunner sample, since this is a network call which will
          //take too long and cause an Android UI timeout.
          facebook.request( "me/friends" );
       }
       catch ( IOException e )
       {
          String error = "Unable to get Facebook friends: " + e.getMessage();
          Log.e( CLASS_NAME, error );
       }
      
      
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}