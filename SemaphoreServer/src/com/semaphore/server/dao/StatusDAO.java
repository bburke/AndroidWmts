package com.semaphore.server.dao;

import java.util.List;

import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

/**
 * Data Access Object interface for Friend and User status data. 
 */
public interface StatusDAO
{  
   public List<FriendDTO> getFriendsStatus( UserDTO user );
}
