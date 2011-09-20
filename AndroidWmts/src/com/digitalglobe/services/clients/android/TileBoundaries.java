package com.digitalglobe.services.clients.android;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TileBoundaries
{
   private static ConcurrentMap<Integer,TileLocation> bounds;
   
   static
   {
      bounds = new ConcurrentHashMap<Integer,TileLocation>();
      
      //There is a smarter, mathematical way to do this, but I'm like a bull in a china shop...
      //just step out the way!
      TileLocation zoom0 = new TileLocation( 0, 0, 1 );
      bounds.put( 0, zoom0 );
      
      TileLocation zoom1 = new TileLocation( 1, 1, 3 );
      bounds.put( 1, zoom1 );
      
      TileLocation zoom2 = new TileLocation( 2, 3, 7 );
      bounds.put( 2, zoom2 );
      
      TileLocation zoom3 = new TileLocation( 3, 7, 15 );
      bounds.put( 3, zoom3 );
      
      TileLocation zoom4 = new TileLocation( 4, 15, 31 );
      bounds.put( 4, zoom4 );
      
      TileLocation zoom5 = new TileLocation( 5, 31, 63 );
      bounds.put( 5, zoom5 );
      
      TileLocation zoom6 = new TileLocation( 6, 63, 127 );
      bounds.put( 6, zoom6 );
      
      TileLocation zoom7 = new TileLocation( 7, 127, 255 );
      bounds.put( 7, zoom7 );
      
      TileLocation zoom8 = new TileLocation( 8, 255, 511 );
      bounds.put( 8, zoom8 );
      
      TileLocation zoom9 = new TileLocation( 9, 511, 1023 );
      bounds.put( 9, zoom9 );
      
      TileLocation zoom10 = new TileLocation( 10, 1023, 2047 );
      bounds.put( 10, zoom10 );
      
      TileLocation zoom11 = new TileLocation( 11, 2047, 4095 );
      bounds.put( 11, zoom11 );
      
      TileLocation zoom12 = new TileLocation( 12, 4095, 8191 );
      bounds.put( 12, zoom12 );
      
      TileLocation zoom13 = new TileLocation( 13, 8191, 16383 );
      bounds.put( 13, zoom13 );
      
      TileLocation zoom14 = new TileLocation( 14, 16383, 32767 );
      bounds.put( 14, zoom14 );
      
      TileLocation zoom15 = new TileLocation( 15, 32767, 65535 );
      bounds.put( 15, zoom15 );
      
      TileLocation zoom16 = new TileLocation( 16, 65535, 131071 );
      bounds.put( 16, zoom16 );
      
      TileLocation zoom17 = new TileLocation( 17, 131071, 262143 );
      bounds.put( 17, zoom17 );
      
      TileLocation zoom18 = new TileLocation( 18, 262143, 524287 );
      bounds.put( 18, zoom18 );
      
      TileLocation zoom19 = new TileLocation( 19, 524287, 1048575 );
      bounds.put( 19, zoom19 );
      
      TileLocation zoom20 = new TileLocation( 20, 1048575, 2097151 );
      bounds.put( 20, zoom20 );
   }
   
   
   /**
    * Check if input location is beyond the EPSG:4326 max row value
    * for the specified zoom.
    *
    * @param location The TileLocation to verify.
    * @return True unless the input row is less than or equal to the max
    *         allowed row for the input location zoom level.
    */
   public static boolean exceedsRowLimit( TileLocation location )
   {
      boolean exceeds = true;
      
      if ( location != null )
      {
         Integer zoom = location.getZoom();
         if ( zoom != null &&
              zoom >= 0 && 
              zoom <= 20  )
         {
            TileLocation limit = bounds.get( zoom );
            Integer row = location.getRow();
            if ( row != null &&
                 row <= limit.getRow() )
            {
               exceeds = false;
            }
         }
      }
      
      return exceeds;
   }
   
   
   /**
    * Check if input location is beyond the EPSG:4326 max column value
    * for the specified zoom.
    *
    * @param location The TileLocation to verify.
    * @return True unless the input column is less than or equal to the max
    *         allowed column for the input location zoom level.
    */
   public static boolean exceedsColumnLimit( TileLocation location )
   {
      boolean exceeds = true;
      
      if ( location != null )
      {
         Integer zoom = location.getZoom();
         if ( zoom != null &&
              zoom >= 0 && 
              zoom <= 20  )
         {
            TileLocation limit = bounds.get( zoom );
            Integer column = location.getColumn();
            if ( column != null && 
                 column <= limit.getColumn() )
            {
               exceeds = false;
            }
         }
      }
      
      return exceeds;
   }
}
