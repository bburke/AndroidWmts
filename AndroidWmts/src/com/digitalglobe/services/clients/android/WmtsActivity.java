package com.digitalglobe.services.clients.android;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.digitalglobe.services.clients.android.cache.AsyncTaskTileCache;
import com.digitalglobe.services.clients.android.cache.TileCache;
import com.digitalglobe.services.clients.android.listener.WmtsGestureListener;
import com.digitalglobe.services.clients.android.listener.WmtsParamListener;
import com.digitalglobe.services.clients.android.util.Constants;

public class WmtsActivity extends Activity implements OnTouchListener
{   
    static final String CLASS_NAME = "WmtsActivity";
   
    private TileCache tileCache;
    
    /**
     * Most recently requested tile location, used to request
     * tiles from the TileCache. 
     */
    private TileLocation location;
    
    private GestureDetector gestureDetector;
    
    private WmtsParamListener wmtsParamListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        gestureDetector = new GestureDetector( new WmtsGestureListener(this) );
        wmtsParamListener = new WmtsParamListener( this );

        //Instantiate the TileCache and display the first tile
        createTileCache( Constants.BARRETs_CONNECT_ID, "Consumer_Profile", "EPSG:4326" );
        if ( tileCache !=  null )
        {
           //Display tile at zoom 0, row 0, col 0 (Western Hemisphere)
           location  = new TileLocation( 0, 0, 0 );
           displayTile( location, "onCreate" );
        }
        else
        {
           Log.e( CLASS_NAME, "Fatal error, tileCache is null in onCreate()" );
        }

        //Display the WMTS request parameter menus
        createConnectIdMenu();
        createProfileMenu();
        createProjectionMenu();
    }


   private void createTileCache( String connectId, String profile, String projection )
   {
      try
      {
         //TODO: Instead of re-instantiating, consider just updating the cache?
         //tileCache = new TileCache( connectId, profile, projection );
         //tileCache = new JdkThreadTileCache( connectId, profile, projection );
         //TODO: implement and use this
         tileCache = new AsyncTaskTileCache( connectId, profile, projection ); 
      }
      catch ( IOException e )
      {
         Log.e( CLASS_NAME, "Fatal failure to create tileCache for connectId: "
                            + connectId + "| " + e.getMessage() );
      }
   }

    
    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
       Log.d( CLASS_NAME, "Begin onTouchEvent" );
       return gestureDetector.onTouchEvent( event );
       //gestureDetector.onTouchEvent( event );
       //return super.onTouchEvent( event );
    }

    
    @Override
    public boolean onTouch( View v, MotionEvent event )
    {
       return onTouchEvent( event );
    }
    
    
    /*
     * The following methods handle callbacks from UI event listeners
     */
    public void panEast()
    {
          location.panEast( 1 );
          displayTile( location, "panEast" );
    }
    
    public void panWest()
    {
          location.panWest( 1 );
          displayTile( location, "panWest" );
    }
    
    public void panNorth()
    {
          location.panNorth( 1 );
          displayTile( location, "panNorth" );
    }
    
    public void panSouth()
    {
          location.panSouth( 1 );
          displayTile( location, "panSouth" );
    }
    
    public void zoomOut()
    {
          location.zoomOut();
          displayTile( location, "zoomOut" );
    }
    
    public void zoomIn()
    {
       location.zoomIn();
       displayTile( location, "zoomIn" );
    }
    
    public void doubleZoomIn()
    {
       location.zoomIn();
       location.zoomIn();
       displayTile( location, "doubleZoomIn" );
    }
    
    
    private void displayTile( TileLocation location, String logMessage )
    {
       try
       {
          long start = System.currentTimeMillis();
          Drawable tile = tileCache.getTile( location );
          showToast( getApplicationContext(), 
                       "Download: " + tileCache.getLastDownloadTime() + " ms\n"
                     + "Average:  " + tileCache.getAvgDownloadTime()  + " ms\n"
                     + "Zoom:     " + location.getZoom() );
          
          //Add the image to the UI layout
          ImageView imgView = (ImageView)findViewById(R.id.DgTile);
          imgView.setImageDrawable(tile);
          imgView.setOnTouchListener( this );
          
          Log.d( CLASS_NAME, "display tile from cache took: " 
                             + (System.currentTimeMillis()-start) + " ms" );
       }
       catch ( IOException e )
       {
          Log.e( CLASS_NAME, "Failed to download and display tile in " 
                             + logMessage + ": " + e.getMessage() );
       }
    }


   private void createConnectIdMenu()
   {
      Spinner connectIdMenu = (Spinner) findViewById(R.id.connectIdMenu);
      ArrayAdapter<CharSequence> connectIdAdapter = 
           ArrayAdapter.createFromResource( this, 
                                            R.array.connectIDs, 
                                            android.R.layout.simple_spinner_item );
      connectIdAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
      connectIdMenu.setAdapter( connectIdAdapter );
      connectIdMenu.setOnItemSelectedListener( wmtsParamListener );
   }
   
   
   private void createProfileMenu()
   {
      Spinner profileMenu = (Spinner) findViewById(R.id.profileMenu);
      ArrayAdapter<CharSequence> profileAdapter = 
           ArrayAdapter.createFromResource( this, 
                                            R.array.profiles, 
                                            android.R.layout.simple_spinner_item );
      profileAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
      profileMenu.setAdapter( profileAdapter );
      profileMenu.setOnItemSelectedListener( wmtsParamListener );
   }

   
   private void createProjectionMenu()
   {
      Spinner projectionMenu = (Spinner) findViewById(R.id.projectionMenu);
      ArrayAdapter<CharSequence> projectionAdapter = 
           ArrayAdapter.createFromResource( this, 
                                            R.array.projections, 
                                            android.R.layout.simple_spinner_item );
      projectionAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
      projectionMenu.setAdapter( projectionAdapter );
      projectionMenu.setOnItemSelectedListener( wmtsParamListener );
   }



   public void setConnectId( String connectId )
   {
      if ( connectId != null && !connectId.equalsIgnoreCase( "" ) )
      {
         //Only reset the cache if this is a distinct connectId
         if ( !tileCache.getConnectId().equals(connectId) )
         {
            createTileCache( connectId, tileCache.getProfile(), tileCache.getProjection() );
            if ( tileCache !=  null )
            {
               //Display current location using new connectId
               displayTile( location, "setConnectId" );
            }
         }
      }
      else
      {
         String message = "Cannot set connectId to null";
         Log.e( CLASS_NAME, message );
         throw new IllegalArgumentException( message );
      }
   }
   
   
   public void setProfile( String profile )
   {
      if ( profile != null && !profile.equalsIgnoreCase( "" ) )
      {
         //Only reset the cache if this is a distinct profile
         if ( !tileCache.getProfile().equals(profile) )
         {
            createTileCache( tileCache.getConnectId(), profile, tileCache.getProjection() );
            if ( tileCache !=  null )
            {
               //Display current location using new profile
               displayTile( location, "setProfile" );
            }
         }
      }
      else
      {
         String message = "Cannot set profile to null";
         Log.e( CLASS_NAME, message );
         throw new IllegalArgumentException( message );
      }      
   }


   public void setProjection( String proj )
   {
      if ( proj != null && !proj.equalsIgnoreCase( "" ) )
      {
         //Only reset the cache if this is a different projection
         if ( !tileCache.getProjection().equals(proj) )
         {
            createTileCache( tileCache.getConnectId(), tileCache.getProfile(), proj );
            if ( tileCache !=  null )
            {
               //New projection means new Tile Matrix, so zoom back out
               location  = new TileLocation( 0, 0, 0 );
               displayTile( location, "setProjection" );
            }
         }
      }
      else
      {
         String message = "Cannot set projection to null";
         Log.e( CLASS_NAME, message );
         throw new IllegalArgumentException( message );
      }     
   }


   public static void showToast(Context context, CharSequence msg) 
   {
      Toast.makeText( context, msg, Toast.LENGTH_SHORT).show();
   }
    
}