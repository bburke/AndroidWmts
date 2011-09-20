package com.digitalglobe.services.clients.android;

import java.net.MalformedURLException;
import java.net.URL;

import com.digitalglobe.services.clients.android.util.Constants;

import android.util.Log;

/**
 * Utility for interacting with DigitalGlobe Cloud Services, specifically WMTS.
 */
public class WmtsRequest
{
   private static final String CLASS_NAME = "WmtsRequest";
   
   private String connectId      = Constants.BARRETs_CONNECT_ID; 
   private String layer          = "DigitalGlobe:ImageryTileService";
   private String imageFormat    = "image/jpeg"; 
   private TileLocation location = new TileLocation( 0, 0, 0 ); 
   private String srs            = "EPSG:4326";
   private boolean useBeta       = false; 
   private String cqlCriteria    = null; 
   private boolean includeMetadata = false;
   private String profile        = "Consumer_Profile";
   
   
   /**
    * Default constructor, probably should be replaced by injected config.
    * Instantiate WMTS Request at zoom 0, row 0, col 0 (Western Hemisphere)
    */
   public WmtsRequest() 
   {
      /*
      this( Constants.BARRETs_CONNECT_ID, 
            "DigitalGlobe:ImageryTileService", 
            "image/jpeg", 
            0, //zoom
            0, //row
            0, //col
            "EPSG:4326", 
            false,   // don't use beta
            null,    // no cqlCriteria
            false,   //don't includeMetadata
            "Consumer_Profile" );
      */ 
   }
   
   /**
    * Constructor which acts like default but with zoom, row, and column
    * passed in.
    * 
    * @param location The TileLocation containing zoom, row, and column to set.
    * @param connectId TODO
    */
   private WmtsRequest( TileLocation location, String connectId ) 
   {
      /*
      this( Constants.BARRETs_CONNECT_ID, 
            "DigitalGlobe:ImageryTileService", 
            "image/jpeg", 
            0, //zoom
            0, //row
            0, //col
            "EPSG:4326", 
            false,   // don't use beta
            null,    // no cqlCriteria
            false,   //don't includeMetadata
            "Consumer_Profile" );
      */
      this.location = location;
      
      if ( connectId != null && !connectId.equalsIgnoreCase( "" ) )
      {
         this.connectId = connectId;
      }
      else
      {
         String message = "Cannot instantiate WmtsRequest with null connectId";
         Log.e( CLASS_NAME, message );
         throw new IllegalArgumentException( message );
      }
   }
   
   
   private WmtsRequest( TileLocation location, String connectId, String profile ) 
   {
      this( location, connectId );
      this.profile = profile;
   }
   
   public WmtsRequest( TileLocation location, 
                       String connectId, 
                       String profile,
                       String srs ) 
   {
      this( location, connectId, profile );
      this.srs = srs;
   }
   
   public WmtsRequest( String connectId, 
                       String layer, 
                       String imageFormat,
                       int zoom, 
                       int row, 
                       int column, 
                       String srs, 
                       boolean useBeta,
                       String cqlCriteria, 
                       boolean includeMetadata, 
                       String profile )
   {
      this.connectId = connectId;
      this.layer = layer;
      this.imageFormat = imageFormat;
      location = new TileLocation();
      location.setZoom( zoom );
      location.setRow( row );
      location.setColumn( column );
      this.srs = srs;
      this.useBeta = useBeta;
      this.cqlCriteria = cqlCriteria;
      this.includeMetadata = includeMetadata;
      this.profile = profile;
   }
   
   
   /**
    * TODO Single sentence method description.
    *
    * @param connectId
    * @param layer
    * @param imageFormat
    * @param zoom
    * @param row
    * @param column
    * @param srs
    * @param useBeta
    * @param cqlCriteria If provided, add CQL filter
    * @param includeMetadata Include metadata xml in jpeg EXIF tags
    * @return
    * @throws MalformedURLException If the URL is invalid.
    */
   public URL getWmtsTileUrl() 
      throws MalformedURLException
   {
      StringBuffer wmtsUrl = new StringBuffer( 512);
      if ( useBeta )
      {
         wmtsUrl.append( "https://services2.digitalglobe.com/earthservice/wmtsaccess" );
      }
      else
      {
         wmtsUrl.append( "https://services.digitalglobe.com/earthservice/wmtsaccess" );
      }

      wmtsUrl.append( "?SERVICE=WMTS" );
      wmtsUrl.append( "&VERSION=1.0.0" );
      wmtsUrl.append( "&STYLE=" ); //no value specified on purpose
      wmtsUrl.append( "&REQUEST=GetTile" );
      wmtsUrl.append( "&CONNECTID=" );
      wmtsUrl.append( connectId );
      wmtsUrl.append( "&LAYER=" );
      wmtsUrl.append( layer );
      wmtsUrl.append( "&FORMAT=" );
      wmtsUrl.append( imageFormat );
      wmtsUrl.append( "&TRANSPARENT=TRUE" );
      wmtsUrl.append( "&TileRow=" );
      wmtsUrl.append( location.getRow() );
      wmtsUrl.append( "&TileCol=" );
      wmtsUrl.append( location.getColumn() );
      wmtsUrl.append( "&TileMatrixSet=" );
      wmtsUrl.append( srs );
      wmtsUrl.append( "&TileMatrix=" );
      wmtsUrl.append( srs );
      wmtsUrl.append( ":" );
      wmtsUrl.append( location.getZoom() );
      wmtsUrl.append( "&inclusionOfSimpleMetadata=" );
      wmtsUrl.append( includeMetadata );
      wmtsUrl.append( "&featureProfile=" );
      wmtsUrl.append( profile );
           
      if ( cqlCriteria != null && !cqlCriteria.equalsIgnoreCase("") )
      {
         wmtsUrl.append( getWmtsCql(cqlCriteria) );
      }
      
      return new URL( wmtsUrl.toString() );
   }
   
   
   /**
    * Create a CQL filter string appropriate for use in a DigitalGlobe WMS request.
    *
    * @param criteria The property name and value to filter by.
    * @return A CQL filter String ready to add to a WMS URL.
    */
   private static String getWmtsCql( String criteria )
   {
      String cql = "";
      
      if ( criteria != null && !criteria.equalsIgnoreCase("") )
      {
         cql = "&COVERAGE_CQL_FILTER=" +  criteria;
      }
      
      return cql;
   }

   /*
   
      
   public TileLocation getLocation()
   {
      return location;
   }
   
   public URL zoomIn() throws MalformedURLException
   {
      location.zoomIn();
      return getWmtsTileUrl();
   }

   public URL zoomOut() throws MalformedURLException
   {
      location.zoomOut();
      return getWmtsTileUrl();
   }
   
   public URL panEast() throws MalformedURLException
   {
      location.panEast( 1 );
      return getWmtsTileUrl();
   }
   
   public URL panWest() throws MalformedURLException
   {
      location.panWest( 1 );
      return getWmtsTileUrl();
   }
   
   public URL panNorth() throws MalformedURLException
   {
      location.panNorth( 1 );
      return getWmtsTileUrl();
   }
   
   public URL panSouth() throws MalformedURLException
   {
      location.panSouth( 1 );
      return getWmtsTileUrl();
   }
   */
}

