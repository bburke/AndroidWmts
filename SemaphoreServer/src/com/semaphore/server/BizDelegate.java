package com.semaphore.server;

import java.util.List;

import com.semaphore.server.dao.FriendDAO;
import com.semaphore.server.dao.StatusDAO;
import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;

public class BizDelegate
{
   private FriendDAO friendDAO;
   
   private StatusDAO statusDAO;
   
   public BizDelegate( FriendDAO friendDAO, StatusDAO statusDAO )
   {
      super();
      this.friendDAO = friendDAO;
      this.statusDAO = statusDAO;
   }


   public List<FriendDTO> getFriends( UserDTO user )
   {
      return friendDAO.getFriends( user );
   }
   
   public List<FriendDTO> getFriendsStatus( UserDTO user )
   {
      return statusDAO.getFriendsStatus( user );
   }
}
