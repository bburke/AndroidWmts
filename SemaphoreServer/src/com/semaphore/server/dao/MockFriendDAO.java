package com.semaphore.server.dao;

import java.util.List;

import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

public class MockFriendDAO implements FriendDAO
{
   public MockFriendDAO()
   {
      friend = new FriendDTO();
      friend.setName( "mock friend" );
      friend.setStatus( 10 );
   }
   
   private FriendDTO friend;
   
   @Override
   public FriendDTO get( FriendDTO friend )
   {
      return this.friend;
   }
   

   @Override
   public FriendDTO create( FriendDTO friend )
   {
      this.friend = friend;
      return this.friend;
   }
   

   @Override
   public FriendDTO update( FriendDTO friend )
   {
      this.friend = friend;
      return this.friend;
   }
   

   @Override
   public FriendDTO delete( FriendDTO friend )
   {
      this.friend = null;
      return this.friend;
   }


   @Override
   public List<FriendDTO> getFriends( UserDTO user )
   {
      List<FriendDTO> friends = user.getFriends();
      friends.add( this.friend );
      
      FriendDTO friend1 = new FriendDTO();
      friend1.setName( "friend1" );
      friend1.setStatus( 9 );
      friends.add( friend1 );
      
      FriendDTO friend2 = new FriendDTO();
      friend2.setName( "friend2" );
      friend2.setStatus( 8 );
      friends.add( friend2 );
      
      return friends;
   }
   
}
