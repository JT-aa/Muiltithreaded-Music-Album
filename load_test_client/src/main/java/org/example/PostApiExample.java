package org.example;

import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;
import java.io.File;


public class PostApiExample {
  public static void main(String[] args) {

    ApiClient client = new ApiClient();
    //client.setBasePath("http://localhost:8080");
    //client.setBasePath("http://localhost:8080/hw9_server_war_exploded");
    client.setBasePath("http://34.214.4.46:8080/hw9_server_war/");
    //client.setBasePath("http://hw7-alb-575932709.us-west-2.elb.amazonaws.com/hw7_server_war/");
    //client.setBasePath("https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.0.0");
    DefaultApi apiInstance = new DefaultApi(client);

    File image = new File("/Users/s/Downloads/4KB.jpg"); // File |
    AlbumsProfile profile = new AlbumsProfile(); // AlbumsProfile | "artist", "title", "year
    profile.setArtist("artist1 lastname");
    profile.setTitle("title title!");
    profile.setYear("yearhaha");
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
  }
}
