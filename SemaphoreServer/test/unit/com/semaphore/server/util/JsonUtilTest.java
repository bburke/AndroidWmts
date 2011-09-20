package com.semaphore.server.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.semaphore.server.BizDelegate;
import com.semaphore.server.dao.MockFriendDAO;
import com.semaphore.server.dao.MockStatusDAO;
import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

public class JsonUtilTest
{
   private final Logger logger = Logger.getLogger( JsonUtilTest.class.getName() );
   
   @Test
   public void getJsonObject() throws JSONException
   {
      //Setup
      MockFriendDAO friendDAO = new MockFriendDAO();
      MockStatusDAO statusDAO = new MockStatusDAO();
      
      UserDTO user = new UserDTO();
      
      BizDelegate biz = new BizDelegate( friendDAO, statusDAO );
      biz.getFriends( user );
      biz.getFriendsStatus( user );
      List<FriendDTO> friends = user.getFriends();
      
      JSONObject json = JsonUtil.getJsonObject( friends );
      logger.info( "Got friends as JSON: " + json.toString() );
      
      assertNotNull( "JSON returned by getJsonObject should not be null", json );
      assertNotNull( "JSON returned by getJsonObject should have an array", json.getJSONArray( "friends:" ));
   }
}
