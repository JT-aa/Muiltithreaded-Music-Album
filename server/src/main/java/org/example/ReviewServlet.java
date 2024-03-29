package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet(name = "example.ReviewServlet", value = "/review/*")
public class ReviewServlet extends HttpServlet {

  private Connection connection;
  private ChannelPool pool;
  private final static String QUEUE_NAME = "review";
  //private final static String HOST = "localhost";
  private final static String HOST = "52.32.89.21"; // rmq address

  private Gson gson = new Gson();


  @Override
  public void init() throws ServletException {
    super.init();
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    factory.setUsername("test"); // rmq username
    factory.setPassword("test"); // rmq password
//    factory.setUsername("guest"); // rmq username
//    factory.setPassword("guest"); // rmq password
    factory.setConnectionTimeout(300);
    try {
      connection = factory.newConnection();
      pool = new ChannelPool(200, connection, QUEUE_NAME);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    //res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      //res.getWriter().write("missing paramterers");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (urlParts.length != 2) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`

      //res.setStatus(HttpServletResponse.SC_OK);
      // Extracting URL parameters
      String albumId = urlParts[1];
      //System.out.println(albumId);
      AlbumDAO albumDAO = new AlbumDAO();
      ReviewInfo review = null;
      //System.out.println(albumId);
      try {
        review = albumDAO.getReviewByID(Integer.parseInt(albumId));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      // Writing the extracted values to the response
      //res.getWriter().write("Album ID: " + albumId + "\n");
      //AlbumInfo album = new AlbumInfo("Sex Pistols", "Never Mind The Bollocks!", "1977");
      String reviewJsonString = this.gson.toJson(review);

      PrintWriter out = res.getWriter();
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      out.print(reviewJsonString);
      out.flush();
    }

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing paramterers");
      return;
    }

    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 3) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("invalid url");
      return;
    }


    String likeOrDislike = urlParts[1];
    int likeOrDislikeInt = 0;
    if (likeOrDislike.equals("like")) {
      likeOrDislikeInt = 1;
    } else if (likeOrDislike.equals("dislike")) {
      likeOrDislikeInt = 0;
    }
    String albumId = urlParts[2];
    String message = albumId + "/" + likeOrDislikeInt;


    Channel channel = null;
    try {
      channel = this.pool.getChannel();
      if (channel != null) {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("It works!");
      } else {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Error: Channel is null");
      }
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write("Error occurred: " + e.getMessage());
      e.printStackTrace(); // Log the exception for debugging
    } finally {
      if (channel != null) {
        this.pool.returnChannel(channel);
      }
    }

  }

  private boolean isUrlValid(String[] urlPath) {
    return urlPath.length == 2 || urlPath.length == 3;
  }
}
