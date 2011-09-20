package com.semaphore.server.util;

import java.util.List;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.semaphore.server.dto.FriendDTO;



/**
 * Tool for performing JSON conversion operations
 */
public class JsonUtil
{
   public static JSONObject getJsonObject( List<FriendDTO> friends ) throws JSONException
   {
      JSONObject json = new JSONObject();    
      
      JSONArray jsonFriends = new JSONArray();
      for ( FriendDTO friend : friends )
      {
         JSONObject jsonFriend = new JSONObject();  
         jsonFriend.put( "name: ", friend.getName() );
         jsonFriend.put( "status: ", friend.getStatus() );
         jsonFriends.put( jsonFriend );
      }
      
      json.put( "friends:", jsonFriends );
      
      return json;
   }   
}
