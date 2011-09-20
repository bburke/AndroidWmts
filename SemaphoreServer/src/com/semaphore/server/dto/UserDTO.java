package com.semaphore.server.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO encapsulating all data representing a single Semaphore user.
 */
public class UserDTO
{
   private String facebookId;
   
   private List<FriendDTO> friends = new ArrayList<FriendDTO>();

   public void setFacebookId( String facebookId )
   {
      this.facebookId = facebookId;
   }

   public String getFacebookId()
   {
      return facebookId;
   }

   public List<FriendDTO> getFriends()
   {
      return friends;
   }
   
}
