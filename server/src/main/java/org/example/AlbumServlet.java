package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "example.AlbumServlet", value = "/albums/*")
@MultipartConfig
public class AlbumServlet extends HttpServlet {

  private Gson gson = new Gson();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
      AlbumDAO albumDAO = new AlbumDAO();
      AlbumInfo album = null;
      //System.out.println(albumId);
      try {
        album = albumDAO.getAlbumByID(Integer.parseInt(albumId));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      // Writing the extracted values to the response
      //res.getWriter().write("Album ID: " + albumId + "\n");
      //AlbumInfo album = new AlbumInfo("Sex Pistols", "Never Mind The Bollocks!", "1977");
      String albumJsonString = this.gson.toJson(album);

      PrintWriter out = res.getWriter();
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      out.print(albumJsonString);
      out.flush();
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    return urlPath.length == 1 || urlPath.length == 2;
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    //res.setContentType("text/plain");
    String urlPath = req.getPathInfo();


    if (urlPath != null) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    } else {
      Part imagePart = req.getPart("image");

    if (imagePart == null || imagePart.getSize() == 0) {
      // If image part is missing or has no content, respond with an error
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }


    try {
      String profileJsonString = req.getParameter("profile");
      //System.out.println(profileJsonString);
//      String[] splitArray = profileJsonString.split("\\s+");
//      String artist = splitArray[4];
//      System.out.println(artist);
//      String title = splitArray[6];
//      String year = splitArray[8];
//      System.out.println(title);
//      System.out.println(year);

      Pattern pattern = Pattern.compile("artist: (.*?)\\s+title: (.*?)\\s+year: (.*?)\\s");
      Matcher matcher = pattern.matcher(profileJsonString);
      matcher.find();
      String artist = matcher.group(1);
      String title = matcher.group(2);
      String year = matcher.group(3);
//      System.out.println(artist);
//      System.out.println(title);
//      System.out.println(year);

      if (artist == null || title == null || year == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }

      long imageSize = imagePart.getSize();
      //System.out.println(imageSize);

      AlbumInfo album = new AlbumInfo(artist, title, year);
//      System.out.println(album.getArtist());
//      System.out.println(album.getTitle());
//      System.out.println(album.getYear());
      AlbumDAO albumDAO = new AlbumDAO();

      //System.out.println("no error here1");

      int albumID;
      try {
        albumID = albumDAO.createAlbum(album, (int) imageSize, imagePart);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      //System.out.println("no error here2");

      // Print the requestBody in the response
      res.setStatus(HttpServletResponse.SC_CREATED);


      ImageMetaData albumMeta = new ImageMetaData(Integer.toString(albumID), imageSize + " bytes");
      String albumMetaJsonString = this.gson.toJson(albumMeta);

      PrintWriter out = res.getWriter();
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      out.print(albumMetaJsonString);
      out.flush();

    } catch (Exception e) {
          res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          return;
    }

    }


//      res.getWriter().write("POST request processed successfully\n");
//      res.getWriter().write("Request Body:\n" + requestBody.toString());
  }
}