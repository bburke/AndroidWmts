package com.digitalglobe.services.clients.android.listener;

import com.digitalglobe.services.clients.android.R;
import com.digitalglobe.services.clients.android.WmtsActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Handles changes to the Profile drop-down menu by updating the 
 * WmtsActivity passed into the constructor.
 * 
 * @see com.digitalglobe.services.clients.android.WmtsActivity#setProfile(String)
 */
public class WmtsParamListener implements OnItemSelectedListener
{
   private static final String CLASS_NAME = "WmtsParamListener";
   
   final WmtsActivity wmtsActivity;
   
   public WmtsParamListener( WmtsActivity wmtsActivity )
   {
      this.wmtsActivity = wmtsActivity;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onItemSelected( AdapterView<?> parent, 
                               View view, 
                               int position, 
                               long id  )
   {
      String menuValue = parent.getItemAtPosition(position).toString();
      int viewId = parent.getId();

      if ( R.id.profileMenu == viewId )
      {
         Log.d( CLASS_NAME, "onItemSelected setting new profile: " + menuValue );
         wmtsActivity.setProfile( menuValue );
         WmtsActivity.showToast( parent.getContext(), "Profile changed to: " + menuValue );
      }
      else if ( R.id.projectionMenu == viewId )
      {
         Log.d( CLASS_NAME, "onItemSelected setting new projection: " + menuValue );
         wmtsActivity.setProjection( menuValue );
         WmtsActivity.showToast( parent.getContext(), "Projection changed to: " + menuValue );
      }
      else if ( R.id.connectIdMenu == viewId )
      {
         int index = menuValue.indexOf( ": " );
         String connectId = menuValue.substring( index + 2 );
         Log.d( CLASS_NAME, "onItemSelected setting new connectId: " + connectId );
         
         wmtsActivity.setConnectId( connectId );
         WmtsActivity.showToast( parent.getContext(), "ConnectId changed to: " + connectId );
      }
      else 
      {
         Log.d( CLASS_NAME, "Failed to identify view in onItemSelected for selected menu value " 
                            + menuValue );
         WmtsActivity.showToast( parent.getContext(), "Error, unable to use " + menuValue );
      }
      
   }
   

   /**
    * {@inheritDoc}
    */
   @Override
   public void onNothingSelected( AdapterView<?> arg0 )
   {
      //Do nothing      
   }
   
}
