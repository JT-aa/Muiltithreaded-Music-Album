package org.example;

import io.swagger.client.*;
import io.swagger.client.api.LikeApi;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;
import java.io.File;


public class PostReviewExample {
  public static void main(String[] args) {

    ApiClient client = new ApiClient();
    //client.setBasePath("http://localhost:8080");
    //client.setBasePath("http://localhost:8080/hw9_server_war_exploded");
    client.setBasePath("http://34.214.4.46:8080/hw9_server_war/");
    //client.setBasePath("http://35.89.243.61:8080/hw9_server_war/");
    //client.setBasePath("http://35.86.253.112:8080/hw7_server_war/");
    //client.setBasePath("http://hw7-alb-575932709.us-west-2.elb.amazonaws.com/hw7_server_war/");
    //client.setBasePath("https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.0.0");

    LikeApi apiInstance = new LikeApi(client);
    String likeornot = "dislike"; // String | like or dislike album
    String albumID = "5"; // String | albumID
    try {
      apiInstance.review(likeornot, albumID);
    } catch (ApiException e) {
      System.err.println("Exception when calling LikeApi#review");
      e.printStackTrace();
    }



  }
}
