/**
 * 
 */
package com.digitalglobe.services.clients.android.cache;

import java.io.IOException;
import java.net.URL;

import com.digitalglobe.services.clients.android.TileLocation;
import com.digitalglobe.services.clients.android.WmtsRequest;
import com.digitalglobe.services.clients.android.util.IOUtils;

import android.os.AsyncTask;
import android.util.Log;
import android.graphics.drawable.Drawable;

/**
 * TODO Single sentence class description.
 *
 */
public class DownloadImageTask extends AsyncTask<String, Integer, Drawable>
{
   private static final String CLASS_NAME = "DownloadImageTask";
   
   private TileLocation location;
   
   private TileCache cache;
   
   private String connectId;
   
   private String profile;
   
   private String projection;
   
   public DownloadImageTask( TileCache cache, 
                             TileLocation location,
                             String connectId, 
                             String profile, 
                             String projection )
   {
      super();
      this.cache = cache;
      this.location = location;
      this.connectId = connectId;
      this.profile = profile;
      this.projection = projection;
   }


   @Override
   protected Drawable doInBackground( String... params )
   {      
      Drawable tile = null;
      try
      {
         if ( location != null )
         {
            WmtsRequest wmtsRequest = new WmtsRequest( location, connectId, profile, projection );
            URL dgcsUrl = wmtsRequest.getWmtsTileUrl();
            tile = IOUtils.downloadTile( dgcsUrl );
            if ( tile == null )
            {
               Log.e( CLASS_NAME, "IOUtils downloaded a null tile" );
            }
         }
         else
         {
            Log.e( CLASS_NAME, "Input location was null, no download occurred" );
         }
      }
      catch( IOException e )
      {
         Log.e( CLASS_NAME, "Failed to download tile: " + e.getMessage() );
      }
      
      cache.addToCache( location, tile );
      
      return tile;
   }
   
   
   @Override
   protected void onPostExecute(Drawable tile) 
   {
      //Moved to doInBackground
      //cache.addToCache( location, tile );
   }
   
   
   /*
   @Override 
   protected void onProgressUpdate(Integer... progress) 
   {
      //Not implemented
   }
   */
}
