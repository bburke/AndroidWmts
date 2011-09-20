package com.semaphore.server.dao;

import java.util.List;

import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

/**
 * Mock implementation of the StatusDAO interface, used only for dev and test.
 */
public class MockStatusDAO implements StatusDAO
{
   
   @Override
   public List<FriendDTO> getFriendsStatus( UserDTO user )
   {
      List<FriendDTO> friends = user.getFriends();
      for ( FriendDTO friend : friends )
      {
         int status = (int) Math.round( Math.random()*10 );
         friend.setStatus( status );
      }
      
      return friends;
   }
   
}
