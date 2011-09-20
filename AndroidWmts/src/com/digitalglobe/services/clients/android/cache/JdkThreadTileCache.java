package com.digitalglobe.services.clients.android.cache;

import java.io.IOException;
import java.util.List;

import com.digitalglobe.services.clients.android.TileLocation;

import android.util.Log;

public class JdkThreadTileCache extends TileCache
{
   
   private static final String CLASS_NAME = "JdkThreadTileCache";
   
   public JdkThreadTileCache( String connectId, String profile, String projection ) 
      throws IOException
   {
      super(connectId, profile, projection);
   }
   
   
   /**
    * Adds the requested location to cache, plus the most likely next
    * viewed tile as determined by {@link #getLocationGrid(TileLocation)}.
    *
    * @param center
    * @throws IOException
    */
   @Override
   protected void requestCacheUpdate( TileLocation center ) throws IOException
   {
      long start = System.currentTimeMillis();
      
      List<TileLocation> locations = getLocationGrid( center );
      
      for ( TileLocation location : locations )
      {
         String cacheKey = getCacheKey( location );
         if ( !containsKey( cacheKey ) )
         {
            Log.d( CLASS_NAME, "Requesting asynch download of " + location );
            AsynchTileDownloader downloader = new AsynchTileDownloader( this, 
                                                                        location, 
                                                                        getConnectId(), 
                                                                        getProfile(), 
                                                                        getProjection() );
            Thread thread = new Thread( downloader );
            thread.start();
         }
      }
      
      pruneCache();

      Log.d( CLASS_NAME, "Finished updateCache in " + (System.currentTimeMillis()-start) + " ms" );
   }
}
