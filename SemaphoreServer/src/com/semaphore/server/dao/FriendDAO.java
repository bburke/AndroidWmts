package com.semaphore.server.dao;

import java.util.List;

import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

/**
 * Data Access Object interface for Friend data. 
 */
public interface FriendDAO
{
   public FriendDTO get( FriendDTO friend );
   
   public FriendDTO create( FriendDTO friend );
   
   public FriendDTO update( FriendDTO friend );
   
   public FriendDTO delete( FriendDTO friend );
   
   public List<FriendDTO> getFriends( UserDTO user );
}
