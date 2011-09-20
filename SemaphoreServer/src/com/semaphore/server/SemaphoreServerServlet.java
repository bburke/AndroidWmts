package com.semaphore.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.semaphore.server.dao.FriendDAO;
import com.semaphore.server.dao.MockFriendDAO;
import com.semaphore.server.dao.MockStatusDAO;
import com.semaphore.server.dao.StatusDAO;
import com.semaphore.server.dto.FriendDTO;
import com.semaphore.server.dto.UserDTO;
import com.semaphore.server.util.JsonUtil;

@SuppressWarnings("serial")
public class SemaphoreServerServlet extends HttpServlet
{
   private final Logger logger = Logger.getLogger( SemaphoreServerServlet.class.getName() );
   
   public void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws IOException
   {
      //resp.setContentType( "application/json" );
      resp.setContentType( "text/plain" );
      
      
      //TODO replace with non-Mock FriendDAO Impl
      FriendDAO friendDAO = new MockFriendDAO();
      StatusDAO statusDAO = new MockStatusDAO();
      
      //TODO: get the facebookID from the HttpServletRequest?
      UserDTO user = new UserDTO();
      
      //TODO: Get list of friends from biz delegate and return as JSON.
      BizDelegate biz = new BizDelegate( friendDAO, statusDAO );
      biz.getFriends( user );
      biz.getFriendsStatus( user );
      List<FriendDTO> friends = user.getFriends();
      
      try
      {
         JSONObject json = JsonUtil.getJsonObject( friends );
         PrintWriter out = resp.getWriter();
         json.write(out);
         logger.info( "Wrote servlet response: " + json.toString() );
         out.close();
         resp.setStatus( HttpServletResponse.SC_OK );
      }
      catch ( JSONException e )
      {
         resp.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
         String error = "Unable to convert friends to JSON or write to HttpServletResponse: "
                        + e.getMessage();
         logger.severe( error );
         resp.getWriter().println( error );
      }
   }
}
