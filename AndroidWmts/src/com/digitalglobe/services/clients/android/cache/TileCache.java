package com.digitalglobe.services.clients.android.cache;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.digitalglobe.services.clients.android.TileLocation;
import com.digitalglobe.services.clients.android.WmtsRequest;
import com.digitalglobe.services.clients.android.util.IOUtils;


import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Utility for creating and maintaining a cache of tiles in local memory. The
 * standard use case is to first construct an instance of this, then make a
 * request to {@link #getTile(TileLocation)}. This attempts to fulfill such
 * requests from the cache, allowing misses to trigger synchronous calls to
 * download a new tile from DGCS. {@link #getTile(TileLocation)} requests also
 * trigger logic to populate the cache via asynchronous downloads of any new
 * tiles not yet in cache that would be needed to maintain a geospatial buffer
 * of tiles around the last {@link #getTile(TileLocation)} request to improve
 * the likelihood of having the next requested location in cache.
 */
public class TileCache
{
   private static final String CACHE_KEY_DELIMITER = "_";

   private static final String CLASS_NAME = "TileCache";

   private static final int MAX_CACHE_SIZE = 60;

   private static final int MIN_CACHE_SIZE = 20;
   
   /**
    * Used to intelligently prune cache
    */
   private TileLocation lastRequestedLocation;
   
   private final String connectId;
   
   private final String profile;
   
   private String projection;
   
   private long lastDownloadTime = 0;
   
   private long totalDownloadTime = 0;
   
   private int downloadCount = 0;

   /**
    * A grid of tiles cached in local memory. The key is a concatenation
    * of the zoom, column, and row, and the values are Drawable instances.
    */
   private ConcurrentMap<String,Drawable> cache = new ConcurrentHashMap<String,Drawable>();
   
   
   public TileCache( String connectId, String profile, String projection ) 
      throws IOException
   {
      this.connectId = connectId;
      this.profile   = profile;
      this.projection = projection;
      
      long start = System.currentTimeMillis();

      //Force synchronous downloads to avoid duplicating DGCS calls in first getTile() request
      //zoom 0 - 2 tiles
      getCachedTileOrDownload( new TileLocation( 0, 0, 0 ) );
      lastRequestedLocation = new TileLocation( 0, 0, 0 );
      
      /*
      if ( projection.equalsIgnoreCase( "EPSG:4326") )
      {
         getCachedTileOrDownload( new TileLocation( 0, 0, 1 ) );
      }
      
      //zoom 1 - 8 tiles
      
      getCachedTileOrDownload( new TileLocation( 1, 0, 0 ) );
      getCachedTileOrDownload( new TileLocation( 1, 0, 1 ) );
      getCachedTileOrDownload( new TileLocation( 1, 0, 2 ) );
      getCachedTileOrDownload( new TileLocation( 1, 0, 3 ) );
      getCachedTileOrDownload( new TileLocation( 1, 1, 0 ) );
      getCachedTileOrDownload( new TileLocation( 1, 1, 1 ) );
      getCachedTileOrDownload( new TileLocation( 1, 1, 2 ) );
      getCachedTileOrDownload( new TileLocation( 1, 1, 3 ) );
      */
      
      Log.d( CLASS_NAME, "Completed initial cache build-out in " 
                         + (System.currentTimeMillis()-start) + " ms" );
   }
   
   
   public Drawable getTile( TileLocation location ) throws IOException
   {
      lastRequestedLocation = location;
      requestCacheUpdate( location ); 
      return getCachedTileOrDownload( location );
   }
   
   /**
    * Adds the requested location to cache, plus the most likely next
    * viewed tile as determined by {@link #getLocationGrid(TileLocation)}.
    * This is a no-op for this class, should be overridden in child classes.
    *
    * @param center
    * @throws IOException
    */
   protected void requestCacheUpdate( TileLocation center ) throws IOException
   {
      Log.d( CLASS_NAME, "requestCacheUpdate is a no-op for TileCache class" );
   }
   

   /**
    * Remove stale tiles from cache.  Try to do this intelligently by:
    * Only removing tiles if cache exceeds {@link #MAX_CACHE_SIZE}.
    * Only remove tiles if they are geographically far away from {@link #lastRequestedLocation}.
    * Stop removing tiles once cache size is equal to {@link #MIN_CACHE_SIZE}.
    *
    */
   protected void pruneCache()
   {
      //TODO: Remove tiles from cache once it is too big
      //      a) *** Determine reasonable cache size ***
      //      b) Remove tiles that are too far away from center
      //      c) Be smart about removing, e.g. don't remove low zooms
      if ( cache.size() > MAX_CACHE_SIZE )
      {
         //List<TileLocation> locations = getLocationGrid( lastRequestedLocation );
         int currentZoom = lastRequestedLocation.getZoom();
         int currentRow  = lastRequestedLocation.getRow();
         int currentCol  = lastRequestedLocation.getColumn();
         
         for ( String potentialPruneKey : cache.keySet() )
         {
            TileLocation potentialPruneLocation = getLocationFromCacheKey( potentialPruneKey );
            int zoomToPrune = potentialPruneLocation.getZoom();
            int rowToPrune  = potentialPruneLocation.getRow();
            int colToPrune  = potentialPruneLocation.getColumn();
            
            /*
             * Don't discard if on same zoom and within 2 rows or columns of the current tile being viewed.
             * This is a 5x5 grid of cells around center, potentially keeping 25 tiles in cache.
             */
            if ( zoomToPrune == currentZoom &&
                 Math.abs(rowToPrune-currentRow) <= 2 &&
                 Math.abs(colToPrune-currentCol) <= 2
               )
            {
               continue;
            }
            /*
             * Nor discard if within 1 zoom and 1 row or column of the current tile being viewed.
             * This is a 3x3 grid of cells around center, both above (zoom up) and below (zoom down),
             * potentially keeping 18 more tiles in cache.
             */
            else if ( Math.abs(zoomToPrune-currentZoom) == 1 &&
                      Math.abs(rowToPrune-currentRow) <= 1 &&
                      Math.abs(colToPrune-currentCol) <= 1
                     )
            {
               continue;
            }
            else
            {
               cache.remove( potentialPruneKey );
            }
            
            
            
            /*
             * Keep tiles within the getLocationGrid of the last requested tile.
             * This approach was replaced with the logic above, which keeps a larger 
             * grid of nearby tiles in cache.
             * 
            boolean remove = true;
            //First check if this tile is within the grid of current center
            for ( TileLocation location : locations )
            {
               String keepLocationKey = getCacheKey( location );
               if ( potentialPruneKey.equals(keepLocationKey) )
               {
                  remove = false;
                  break;
               }
            }
            
            if ( remove )
            {
               //If this cached tile is outside our standard buffer, then remove the tile from cache
               cache.remove( potentialPruneKey );
            }
            */
            
            
            /*
             * Stop pruning if we are back to a safe size.
             * This allows us to additionally keep some of the most likely to be re-viewed tiles
             * even if they fall beyond the nearest tile grid characteristics defined above.
             */
            if ( cache.size() <= MIN_CACHE_SIZE )
            {
               break;
            }
            
         }
         Log.d( CLASS_NAME, "New cache size after prune: " + cache.size() );
      }
   }


   /**
    * Check if requested location is already cached.  If so, return the
    * cached instance, otherwise download the tile and cache the image.
    *
    * @param location The TileLocation to obtain.
    * @return The Drawable image tile of the requested location.
    * @throws MalformedURLException If unable to generate the WMTS request URL.
    * @throws IOException If unable to download the image.
    */
   private Drawable getCachedTileOrDownload( TileLocation location )
         throws MalformedURLException, IOException
   {
      Drawable tile = null;
      
      String cacheKey = getCacheKey( location );
      
      if ( cache.containsKey(cacheKey) )
      {
         tile = cache.get( cacheKey );
         lastDownloadTime = 0;
         Log.d( CLASS_NAME, "Found tile in cache: " + location );
      }
      else
      {
         Log.w( CLASS_NAME, "Cache miss, forcing synchronous download for: " + location );
         WmtsRequest wmtsRequest = new WmtsRequest( location, connectId, profile, projection );
         URL dgcsUrl = wmtsRequest.getWmtsTileUrl();
         
         //download tile and gather performance stats
         long start = System.currentTimeMillis();
         tile = IOUtils.downloadTile( dgcsUrl );
         lastDownloadTime = System.currentTimeMillis() - start;
         downloadCount++;
         totalDownloadTime += lastDownloadTime;
         
         addToCache( location, tile );
      }

      return tile;
   }
   
   
   protected static String getCacheKey( TileLocation location )
   {
      return location.getZoom() + CACHE_KEY_DELIMITER + 
             location.getColumn() + CACHE_KEY_DELIMITER + 
             location.getRow();
   }
   
   private static TileLocation getLocationFromCacheKey( String cacheKey )
   {
      String[] parts = cacheKey.split( CACHE_KEY_DELIMITER );
      int zoom   = Integer.parseInt( parts[0] );
      int column = Integer.parseInt( parts[1] );
      int row    = Integer.parseInt( parts[2] );
      
      return new TileLocation( zoom, row, column );
   }
   
   /**
    * Create a grid of tiles centered around input location,
    * including the input center tile, which are most likely to be viewed
    * next.
    *
    * @param centerTileLocation the location to use as center.
    */
   protected static List<TileLocation> getLocationGrid( TileLocation centerTileLocation )
   {
      List<TileLocation> locations = new ArrayList<TileLocation>();
      
      //Get grid of surrounding tiles on same zoom as input
      getSurroundingLocations( centerTileLocation, locations );
      
      //Get grid of surrounding tiles on next zoom in
      TileLocation zoomIn = centerTileLocation.clone();
      zoomIn.zoomIn();
      //Don't over-do pre-caching, just get next possible tile
      //getSurroundingLocations( zoomIn, locations );
      locations.add( zoomIn );
      
      //Get grid of surrounding tiles on previous zoom out
      /*
      TileLocation zoomOut = centerTileLocation.clone();
      zoomOut.zoomOut();
      getSurroundingLocations( zoomOut, locations );
      */
      
      return locations;
   }

   private static void getSurroundingLocations( TileLocation centerTileLocation, 
                                                List<TileLocation> locations )
   {
      locations.add( centerTileLocation );
      
      TileLocation dueNorth = centerTileLocation.clone();
      dueNorth.panNorth( 1 );
      locations.add( dueNorth );

      TileLocation dueEast = centerTileLocation.clone();
      dueEast.panEast( 1 );
      locations.add( dueEast );
      
      TileLocation dueSouth = centerTileLocation.clone();
      dueSouth.panSouth( 1 );
      locations.add( dueSouth );
      
      TileLocation dueWest = centerTileLocation.clone();
      dueWest.panWest( 1 );
      locations.add( dueWest );
      
      /*
       * Four corners
      TileLocation nw = centerTileLocation.clone();
      nw.panNorth( 1 );
      nw.panWest( 1 );
      locations.add( nw );
      
      TileLocation ne = centerTileLocation.clone();
      ne.panNorth( 1 );
      ne.panEast( 1 );
      locations.add( ne );
      
      
      TileLocation se = centerTileLocation.clone();
      se.panSouth( 1 );
      se.panEast( 1 );
      locations.add( se );
      
      TileLocation sw = centerTileLocation.clone();
      sw.panSouth( 1 );
      sw.panWest( 1 );
      locations.add( sw );
      */
   }


   /**
    * Callback to allow asynchronous threads to add downloaded tiles to this
    * cache.
    *
    * @param location The key to 
    * @param tile
    */
   public void addToCache( TileLocation location, Drawable tile )
   {
      Log.d( CLASS_NAME, "addToCache received asynch callback tile for: " + location );
      String cacheKey = getCacheKey( location );
      if ( tile != null )
      {
         cache.put( cacheKey, tile );
      }
      
      Log.d( CLASS_NAME, "New cache size: " + cache.size() );
      
      pruneCache();
   }

   /**
    * TODO Single sentence method description.
    *
    * @param cacheKey
    * @return
    */
   boolean containsKey( String cacheKey )
   {
      return cache.containsKey( cacheKey );
   }
   
   public String getConnectId()
   {
      return connectId;
   }
   
   public String getProfile()
   {
      return profile;
   }
   
   public String getProjection()
   {
      return projection;
   }
   
   public long getAvgDownloadTime()
   {
      return totalDownloadTime/downloadCount;
   }
   
   public long getLastDownloadTime()
   {
      return lastDownloadTime;
   }
}
