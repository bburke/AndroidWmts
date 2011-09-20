package com.semaphore.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.semaphore.server.dao.MockFriendDAO;
import com.semaphore.server.dao.MockStatusDAO;
import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

public class BizDelegateTest
{

   private final Logger logger = Logger.getLogger( BizDelegateTest.class.getName() );
   
   @Test
   public void getFriendsStatus()
   {
      MockFriendDAO friendDAO = new MockFriendDAO();
      MockStatusDAO statusDAO = new MockStatusDAO();
      
      //TODO: get the facebookID from the HttpServletRequest?
      UserDTO user = new UserDTO();
      
      //TODO: Get list of friends from biz delegate and return as JSON.
      BizDelegate biz = new BizDelegate( friendDAO, statusDAO );
      biz.getFriends( user );
      biz.getFriendsStatus( user );
      List<FriendDTO> friends = user.getFriends();
      logger.info( "Got friends status: " + friends );
      logger.info( "friend list size: " + friends.size() );
      
      assertNotNull( "Friends List should not be null", friends );
      assertTrue( "Friends List should not be empty", !friends.isEmpty() );
      
      for ( FriendDTO friend : friends )
      {
         assertNotNull( "Friend should not have null status for " + friend, friend.getStatus() );
      }
      
   }
}
