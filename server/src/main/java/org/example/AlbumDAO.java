package org.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.Part;
import org.apache.commons.dbcp2.BasicDataSource;

public class AlbumDAO {

  private static BasicDataSource dataSource;

  public AlbumInfo getAlbumByID(int albumID) throws SQLException {
    String SELECT_QUERY = "SELECT * FROM albumstore WHERE albumID = ?";

    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      conn = DBCPDataSource.getDataSource().getConnection();
      preparedStatement = conn.prepareStatement(SELECT_QUERY);
      preparedStatement.setInt(1, albumID);

      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        String artist = resultSet.getString("artist");
        String title = resultSet.getString("title");
        String year = resultSet.getString("year");
        return new AlbumInfo(artist, title, year);
      }
    } finally {
      // Close resources in a finally block to ensure they're always closed
      if (resultSet != null) {
        resultSet.close();
      }
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (conn != null) {
        conn.close();
      }
    }

    return null;
  }


  public ReviewInfo getReviewByID(int albumID) throws SQLException {
    String SELECT_QUERY = "SELECT * FROM review WHERE albumID = ?";

    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      conn = DBCPDataSource.getDataSource().getConnection();
      preparedStatement = conn.prepareStatement(SELECT_QUERY);
      preparedStatement.setInt(1, albumID);

      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        int likes = resultSet.getInt("likes");
        int dislikes = resultSet.getInt("dislikes");
        return new ReviewInfo(likes, dislikes);
      }
    } finally {
      // Close resources in a finally block to ensure they're always closed
      if (resultSet != null) {
        resultSet.close();
      }
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (conn != null) {
        conn.close();
      }
    }

    return null;
  }




  public int createAlbum(AlbumInfo album, int imageSize, Part imagePart)
      throws SQLException, IOException {
    String INSERT_QUERY = "INSERT INTO albumstore (artist, title, year, imagesize, image) VALUES (?, ?, ?, ?, ?)";

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    try {
      conn = DBCPDataSource.getDataSource().getConnection();
      preparedStatement = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setString(1, album.getArtist());
      preparedStatement.setString(2, album.getTitle());
      preparedStatement.setString(3, album.getYear());
      preparedStatement.setInt(4, imageSize);
      preparedStatement.setBinaryStream(5, imagePart.getInputStream());

      preparedStatement.executeUpdate();

      int generatedAlbumId = -1;

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          generatedAlbumId = generatedKeys.getInt(1);
        }
      }

      return generatedAlbumId;
    } finally {
      // Close resources in a finally block to ensure they're always closed
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
  }



  public void postReview(int albumID, int likeOrDislike) throws SQLException {
    String UPDATE_QUERY;
    String CREATE_QUERY;
    if (likeOrDislike == 1) {
      UPDATE_QUERY = "UPDATE review SET likes = likes + 1 WHERE albumID = ?";
      CREATE_QUERY = "INSERT IGNORE INTO review (albumID, likes, dislikes) VALUES (?, 1, 0)";
    } else {
      UPDATE_QUERY = "UPDATE review SET dislikes = dislikes + 1 WHERE albumID = ?";
      CREATE_QUERY = "INSERT IGNORE INTO review (albumID, likes, dislikes) VALUES (?, 0, 1)";
    }
    //System.out.println(UPDATE_QUERY);
    //System.out.println(CREATE_QUERY);

    Connection conn = null;
    PreparedStatement preparedStatement = null;

    try {
      conn = DBCPDataSource.getDataSource().getConnection();
      preparedStatement = conn.prepareStatement(UPDATE_QUERY);
      preparedStatement.setInt(1, albumID);

      int rowsUpdated = preparedStatement.executeUpdate();
      //System.out.println(rowsUpdated);

      if (rowsUpdated == 0) { // No record was updated, insert new record
        preparedStatement.close(); // Close previous statement
        preparedStatement = conn.prepareStatement(CREATE_QUERY);
        preparedStatement.setInt(1, albumID);
        //System.out.println(preparedStatement);
        preparedStatement.executeUpdate();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Close resources in a finally block to ensure they're always closed
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
  }


}
