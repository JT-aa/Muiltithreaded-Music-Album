package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MultithreadedConsumer {

  private final static String QUEUE_NAME = "review";
  private final static int NUM_THREADS = 100;

  private final static int NUM_REQUESTS = 1000;

  private static long startTime = 0;
  private static long endTime = 0;

  //private static String HOST = "localhost";
  private static String HOST = "52.32.89.21";

  private static boolean writeToDB(String message, AlbumDAO albumDAO) {
//    if (startTime == 0) {
//      startTime = System.currentTimeMillis();
//    }
    boolean writeSuccess = false;
    int albumID = Integer.parseInt(message.split("/")[0]);
    int likeOrDislike = Integer.parseInt(message.split("/")[1]);

    //System.out.printf("[INFO] albumID is %d\n", albumID);
    //System.out.printf("[INFO] likeOrDislike is %d\n", likeOrDislike);

    try {
      albumDAO.postReview(albumID, likeOrDislike);
    } catch (Exception e){
      return false;
    }
//    endTime = System.currentTimeMillis();
    return writeSuccess;
  }

  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    factory.setUsername("test");
    factory.setPassword("test");
//    factory.setUsername("guest");
//    factory.setPassword("guest");
    Connection connection = factory.newConnection();
//    ArrayList<String> records = new ArrayList<>();

    // A channel per thread
    for (int i = 0; i < NUM_THREADS; i++) {
      Runnable thread = () -> {
        try {
          AlbumDAO albumDAO = new AlbumDAO();
          Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.printf("[INFO] record consumed is %s\n", message);
            writeToDB(message, albumDAO);
          };
          channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
      new Thread(thread).start();
    }
  }

}
