package com.digitalglobe.services.clients.android.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.graphics.drawable.Drawable;
import android.util.Log;


/**
 * Common IO Utilities
 */
public class IOUtils
{
   
   public static final String CLASS_NAME = "IOUtils";
   
   
   public static final void safeSilentClose( Closeable closeable )
   {
      if ( closeable != null )
      {
         try
         {
            closeable.close();
         }
         catch( Exception e )
         {
            //ignore
         }
      }
   }

   
   /**
    * Download an image from the input URL and convert it to a Drawable.
    *
    * @param url The URL from which to request the image.
    * @return A Drawable instance containing the tile image.
    * @throws IOException If unable to open the URL connection.
    */
   public static Drawable downloadTile( URL url ) throws IOException 
   {
      long start = System.currentTimeMillis();
      
      Log.d( CLASS_NAME, "Downloading image from: " + url.toString() );
      
      HttpsURLConnection con = null;
      InputStream iStream = null;
      
      try
      {  
         con      = (HttpsURLConnection)url.openConnection();
         iStream  = con.getInputStream();
         //int responseCode = con.getResponseCode(); 
         Drawable drawable = Drawable.createFromStream(iStream, "src");
         Log.d( CLASS_NAME, "Completed image download in " + (System.currentTimeMillis()-start) + " ms" );
         return drawable;
      }
      finally
      {
         safeSilentClose( iStream );
         con.disconnect();
      }
   }
   
   
   
}

