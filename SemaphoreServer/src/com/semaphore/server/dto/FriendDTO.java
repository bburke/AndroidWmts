package com.semaphore.server.dto;

/**
 * POJO encapsulating all data representing a single Friend.
 */
public class FriendDTO
{
   private Integer status = 0;
   private String name;
   
   public Integer getStatus()
   {
      return status;
   }

   public void setStatus( Integer status )
   {
      this.status = status;
   }

   public String getName()
   {
      return name;
   }

   public void setName( String name )
   {
      this.name = name;
   }
 
   
   public String toString()
   {
      return "FriendDTO{ name=" + name + " | status=" + status + "}";
   }
}
