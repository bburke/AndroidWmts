package com.digitalglobe.services.clients.android.listener;

import com.digitalglobe.services.clients.android.WmtsActivity;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class WmtsGestureListener extends SimpleOnGestureListener
{
   private static final String CLASS_NAME = "WmtsGestureListener";
   
   private static final int SWIPE_MIN_DISTANCE = 120;
   private static final int SWIPE_MAX_OFF_PATH = 250;
   private static final int SWIPE_THRESHOLD_VELOCITY = 200;
   
   private WmtsActivity wmtsActivity;
   
   public WmtsGestureListener( WmtsActivity wmtsActivity )
   {
      this.wmtsActivity = wmtsActivity;
   }
   
   @Override
   public void onLongPress( MotionEvent event )
   {
      Log.d( CLASS_NAME, "Begin onLongPress, will zoom out" );
      wmtsActivity.zoomOut();
   }
   
   @Override
   public boolean onSingleTapConfirmed( MotionEvent event )
   {
      Log.d( CLASS_NAME, "Begin onSingleTap, will zoom in" );
      wmtsActivity.zoomIn();
      return true;
   }
   
   @Override
   public boolean onDoubleTapEvent( MotionEvent event )
   {
      Log.d( CLASS_NAME, "Begin onDoubleTapEvent, will double zoom in" );
      wmtsActivity.doubleZoomIn();
      return true;
   }
   
   @Override
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
   {
      Log.d( CLASS_NAME, "Begin onFling" );

      boolean handled = false;
      
      if ( e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&    //swiped far enough
           e1.getY() - e2.getY() < SWIPE_MAX_OFF_PATH &&    //didn't swipe too far
           Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY ) //swiped fast enough
      {
         Log.d( CLASS_NAME, "onFling panEast" );
         wmtsActivity.panEast();
         handled = true;
      } 
      else if ( e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && 
                e2.getY() - e1.getY() < SWIPE_MAX_OFF_PATH && 
                Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
      {
         Log.d( CLASS_NAME, "onFling panWest" );
         wmtsActivity.panWest();
         handled = true;
      }
      else if ( e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && 
                e1.getX() - e2.getX() < SWIPE_MAX_OFF_PATH && 
                Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY ) 
      {
         Log.d( CLASS_NAME, "onFling panSouth" );
         wmtsActivity.panSouth();
         handled = true;
      } 
      else if ( e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && 
                e2.getX() - e1.getX() < SWIPE_MAX_OFF_PATH && 
                Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) 
      {
         Log.d( CLASS_NAME, "onFling panNorth" );
         wmtsActivity.panNorth();
         handled = true;
      }
      else
      {
         String message = "onFling but wasn't really a fling. " + 
                          "Swipe X-axis distance: " + (e2.getX() - e1.getX()) + "\n" + 
                          "Swipe Y-axis distance: " + (e2.getY() - e1.getY()) + "\n" + 
                          "Swip min distance:     " + SWIPE_MIN_DISTANCE + "\n" + 
                          "Swip max off path:     " + SWIPE_MAX_OFF_PATH + "\n" + 
                          "Swipe X velocity:      " + velocityX + "\n" + 
                          "Swipe Y velocity:      " + velocityY + "\n" + 
                          "Swipe velocity min:    " + SWIPE_THRESHOLD_VELOCITY + "\n"
                          ;
         Log.d( CLASS_NAME, message);
         
      }
      
      return handled;
   }
   
}
