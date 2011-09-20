package com.digitalglobe.services.clients.android;

/**
 * Java bean representing the zoom, row, and column information for a given tile.
 */
public class TileLocation implements Cloneable
{
   private int zoom = 1;
   
   private int row = 1;
   
   private int column = 1;

   /**
    * Default constructor
    */
   public TileLocation()
   {
      
   }
   
   /**
    * Construct using input values.
    * 
    * @param zoom
    * @param row
    * @param column
    */
   public TileLocation( int zoom, int row, int column )
   {
      this.zoom   = zoom;
      this.row    = row;
      this.column = column;
   }

   @Override
   public TileLocation clone()
   {
      return new TileLocation( zoom, row, column );
   }
   
   public int getZoom()
   {
      return zoom;
   }

   public void setZoom( int zoom )
   {
      this.zoom = zoom;
   }

   public int getRow()
   {
      return row;
   }

   public void setRow( int row )
   {
      this.row = row;
   }

   public int getColumn()
   {
      return column;
   }

   public void setColumn( int column )
   {
      this.column = column;
   }
   
   
   public String toString()
   {
      StringBuffer message = new StringBuffer( 512 );
      message.append( "TileLocation: {zoom=" );
      message.append( zoom );
      message.append( "|row=" );
      message.append( row );
      message.append( "|column=" );
      message.append( column );
      message.append( "}" );
      
      return message.toString();
   }

   /**
    * Zoom in on the existing location by incrementing the zoom by 1 and
    * doubling row and column.
    */
   public void zoomIn()
   {
      if ( zoom < 20 )
      {
         zoom++;
         if ( row == 0 )
         {
            row++;
         }
         else
         {
            row = row*2;
         }
         
         if ( column == 0 )
         {
            column++;
         }
         else
         {
            column = column*2;
         }
      }
   }

   public void zoomOut()
   {
      if ( zoom > 0 )
      {
         zoom--;
         
         if ( row > 0 )
         {
            row = row/2;
         }
         
         if ( column > 0 )
         {
            column = column/2;
         }
      }
   }

   /**
    * Move to the east by incrementing column by the input value.
    *
    * @param tileCount
    */
   public void panEast( int tileCount )
   {
      column += tileCount;
      
      //Don't pan beyond the Eastern most limit of the TileMatrix
      if ( TileBoundaries.exceedsColumnLimit(this) )
      {
         column -= tileCount;
      }
   }
   
   /**
    * Move to the west by decrementing column by the input value.
    *
    * @param tileCount
    */
   public void panWest( int tileCount )
   {
      column -= tileCount;
      
      //Don't pan beyond the western limit of the TileMatrix
      if ( column < 0 )
      {
         column = 0;
      }
   }
   
   /**
    * Move to the North by decrementing row by the input value.
    *
    * @param tileCount
    */
   public void panNorth( int tileCount )
   {
      row -= tileCount;
      
      //Don't pan beyond the northern limit of the TileMatrix
      if ( row < 0 )
      {
         row = 0;
      }
   }
   
   /**
    * Move to the South by incrementing row by the input value.
    *
    * @param tileCount
    */
   public void panSouth( int tileCount )
   {
      row += tileCount;
      
      //Don't pan beyond the southern most limit of the TileMatrix
      if ( TileBoundaries.exceedsRowLimit(this) )
      {
         row -= tileCount;
      }
   }
}

