package org.example;

import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;
import java.io.File;

public class GetApiExample {

  public static void main(String[] args) {
    ApiClient client = new ApiClient();
    //client.setBasePath("http://localhost:8080");
    //client.setBasePath("http://34.217.64.77:8080/hw7_server_war/");
    //client.setBasePath("http://hw7-1924081381.us-west-2.elb.amazonaws.com/hw7_server_war/");
    //client.setBasePath("http://localhost:8080/hw9_server_war_exploded");
    client.setBasePath("http://34.214.4.46:8080/hw9_server_war");

    //client.setBasePath("https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.0.0");
    DefaultApi apiInstance = new DefaultApi(client);
    String albumID = "1"; // String | path  parameter is album key to retrieve
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }

  }
}