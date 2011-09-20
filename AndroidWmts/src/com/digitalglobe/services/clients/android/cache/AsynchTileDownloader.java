package com.digitalglobe.services.clients.android.cache;

import java.io.IOException;
import java.net.URL;


import com.digitalglobe.services.clients.android.TileLocation;
import com.digitalglobe.services.clients.android.WmtsRequest;
import com.digitalglobe.services.clients.android.util.IOUtils;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Instantiate this class to asynchronously download a tile
 * and add it to the TileCache provided to the constructor.
 */
public class AsynchTileDownloader implements Runnable
{
   private static final String CLASS_NAME = "AsynchTileDownloader";
   
   private TileLocation location;
   
   private TileCache cache;
   
   private String connectId;
   
   private String profile;
   
   private String projection;
   
   /**
    * Asynchronously downloads the requested tile location
    * and adds it to the TileCache provided to the constructor.
    * @param location The TileLocation to download.
    * @param connectId TODO
    * @param profile TODO
    * @param projection TODO
    * @param TileCache the cache to which to add the downloaded tile.
    */
   public AsynchTileDownloader( TileCache cache, 
                                TileLocation location, 
                                String connectId, 
                                String profile, 
                                String projection )
   {
      this.cache = cache;
      this.location = location;
      this.connectId = connectId;
      this.profile = profile;
      this.projection = projection;
   }   
   
   
   @Override
   public void run()
   {
      try
      {
         if ( location != null )
         {
            WmtsRequest wmtsRequest = new WmtsRequest( location, connectId, profile, projection );
            URL dgcsUrl = wmtsRequest.getWmtsTileUrl();
            Drawable tile = IOUtils.downloadTile( dgcsUrl );
            if ( tile != null )
            {
               cache.addToCache( location, tile );
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
   }
   
}
