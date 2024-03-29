package org.example;

import static java.lang.Long.parseLong;

import io.swagger.client.*;
import io.swagger.client.api.LikeApi;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LoadServer {

  private static final StringBuilder csvRecords = new StringBuilder();

  private static final List<Long> postResponseTimes = new ArrayList<>();
  private static final List<Long> getResponseTimes = new ArrayList<>();
  private static final List<Long> getReviewResponseTimes = new ArrayList<>();

  private int maxID = 0;

  public synchronized int getMaxID() {
    return maxID;
  }

  public synchronized void incrementMaxID() {
    maxID += 1000;
  }


  private int successful = 0;
  private int failed = 0;

  synchronized public void incrementSuccessful() {
    successful++;
  }
  synchronized public void incrementFailed() {
    failed++;
  }

  public int getSuccessful() {
    return successful;
  }
  public int getFailed() {
      return failed;
  }


  private static void loadResponseTimesFromCSV(String csvFilePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
          //long startTime = parseLong(parts[0]);
          String requestType = parts[1];
          long latency = Long.parseLong(parts[2]);
          //int responseCode = Integer.parseInt(parts[3]);

          // Add response time to the corresponding list
//          if ("POST".equals(requestType)) {
//            postResponseTimes.add(latency);
//          } else if ("GET".equals(requestType)) {
//            getResponseTimes.add(latency);
//          }
            if ("GETR".equals(requestType)) {
                getReviewResponseTimes.add(latency);
            }
        }
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
  }


  private static long calculateMedian(List<Long> responseTimes) {
    Collections.sort(responseTimes);
    int size = responseTimes.size();
    return size % 2 == 0 ?
        (responseTimes.get(size / 2 - 1) + responseTimes.get(size / 2)) / 2 :
        responseTimes.get(size / 2);
  }

  private static long calculatePercentile(List<Long> responseTimes, int percentile) {
    Collections.sort(responseTimes);
    int index = (int) Math.ceil((double) percentile / 100 * responseTimes.size()) - 1;
    return responseTimes.get(index);
  }

  private static void calculateAndPrintStatistics(List<Long> responseTimes, String requestType) {
    if (!responseTimes.isEmpty()) {
      // Calculate and print mean, median, p99, min, and max
      double mean = responseTimes.stream().mapToLong(Long::valueOf).average().orElse(0.0);
      long median = calculateMedian(responseTimes);
      long p99 = calculatePercentile(responseTimes, 99);
      long min = Collections.min(responseTimes);
      long max = Collections.max(responseTimes);

      System.out.printf("%s Request Statistics:\n", requestType);
      System.out.printf("Mean Response Time: %.2f ms\n", mean);
      System.out.printf("Median Response Time: %d ms\n", median);
      System.out.printf("p99 Response Time: %d ms\n", p99);
      System.out.printf("Min Response Time: %d ms\n", min);
      System.out.printf("Max Response Time: %d ms\n", max);
      System.out.println();
    }
  }




  public static void main(String[] args) throws InterruptedException, ApiException {
//    // local java
//    System.out.println("Java local with 1 free tier RDS Provisioned IOPS 3000/ 100G");
//    System.out.println("10 groups of 10 threads:");
//    loadTest(10, 10, 2, "http://localhost:8080/hw7_server_war_exploded/", "LOCAL_RDS_10.csv");
//    System.out.println("20 groups of 10 threads:");
//    loadTest(10, 20, 2, "http://localhost:8080/hw7_server_war_exploded/", "LOCAL_RDS_20.csv");
    System.out.println("30 groups of 10 threads:");
    loadTest(10, 30, 2, "http://34.214.4.46:8080/hw9_server_war/", "LOCAL_RDS_30.csv");
//
//    // local go
//    System.out.println("Local Go");
//    System.out.println("10 groups of 10 threads:");
//    loadTest(10, 10, 2, "http://localhost:8080");
//    System.out.println("20 groups of 10 threads:");
//    loadTest(10, 20, 2, "http://localhost:8080");
//    System.out.println("30 groups of 10 threads:");
//    loadTest(10, 30, 2, "http://localhost:8080");
//
    // ec2 java
//    System.out.println("ALB + 4 EC2 + beefed up RDS");
//    System.out.println("10 groups of 10 threads:");
//    loadTest(10, 10, 2, "http://hw7-alb-575932709.us-west-2.elb.amazonaws.com/hw7_server_war/", "ALBRDS_10.csv");
//    System.out.println("20 groups of 10 threads:");
//    loadTest(10, 20, 2, "http://hw7-alb-575932709.us-west-2.elb.amazonaws.com/hw7_server_war/", "ALBRDS_20.csv");
//    System.out.println("30 groups of 10 threads:");
//    loadTest(10, 30, 2, "http://hw7-alb-575932709.us-west-2.elb.amazonaws.com/hw7_server_war/", "ALBRDS_30.csv");


    //ec2 java
//    System.out.println("ALB 2  EC2 with 1 free tier RDS");
//    System.out.println("10 groups of 10 threads:");
//    loadTest(10, 10, 2, "http://hw7-1924081381.us-west-2.elb.amazonaws.com/hw7_server_war/", "ALBRDS_10.csv");
//    System.out.println("20 groups of 10 threads:");
//    loadTest(10, 20, 2, "http://hw7-1924081381.us-west-2.elb.amazonaws.com/hw7_server_war/", "ALBRDS_20.csv");
//    System.out.println("30 groups of 10 threads:");
//    loadTest(10, 30, 2, "http://hw7-1924081381.us-west-2.elb.amazonaws.com/hw7_server_war/", "ALBRDS_30.csv");
////
//    // ec2 go
//    System.out.println("EC2 Go");
//    System.out.println("10 groups of 10 threads:");
//    loadTest(10, 10, 2, "http://34.219.6.80:8080", "EC2Go_10.csv");
//    System.out.println("20 groups of 10 threads:");
//    loadTest(10, 20, 2, "http://34.219.6.80:8080", "EC2Go_20.csv");
//    System.out.println("30 groups of 10 threads:");
//    loadTest(10, 30, 2, "http://34.219.6.80:8080", "EC2Go_30.csv");
  }

  private static void loadTest(int threadGroupSize, int numThreadGroups, int delay, String IPAddr, String fileName)
      throws InterruptedException, ApiException {
    LoadServer counter = new LoadServer();
    CountDownLatch completed = new CountDownLatch(10);
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < 10; i++) {
      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread =  () -> {

        ApiClient client = new ApiClient();
        client.setBasePath(IPAddr);
        DefaultApi apiInstance = new DefaultApi(client);
        LikeApi likeInstance = new LikeApi(client);

        for(int j = 0; j < 100; j++) {
          int retry = 0;
          while (retry < 5) {
            if (requestWrapper(apiInstance, likeInstance)) {
              break;
            } else {
              retry++;
            }
          }
          //if (retry == 5) counter.incrementFailed();
        }

        completed.countDown();

      };
      new Thread(thread).start();
    }
    completed.await();

    // Start 3 threads after your first thread group has completed

    List<Thread> additionalThreads = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Thread thread = new Thread(() -> {
        ApiClient client = new ApiClient();
        client.setBasePath(IPAddr);
        LikeApi likeInstance = new LikeApi(client);

        while (!Thread.currentThread().isInterrupted()) {
          if(getReviewRequestWrapper(likeInstance)) {
            counter.incrementSuccessful();
          } else {
            counter.incrementFailed();
          }
        }
      });
      thread.start();
      additionalThreads.add(thread);
    }


    // CountDownLatch completed1 = new CountDownLatch(threadGroupSize * numThreadGroups);
    //    for (int i = 0; i < numThreadGroups - 1; i++) {
    //      startOneGroup(threadGroupSize, IPAddr, completed1, counter);
    //      Thread.sleep(delay * 1000);
    //    }
    //    startOneGroup(threadGroupSize, IPAddr, completed1, counter);
    //    completed1.await();

    List<CountDownLatch> latchList = new ArrayList<>();
    // Initialize 30 CountDownLatch instances
    for (int i = 0; i < numThreadGroups; i++) {
      latchList.add(new CountDownLatch(threadGroupSize));
    }

    for (int i = 0; i < numThreadGroups - 1; i++) {
      startOneGroup(threadGroupSize, IPAddr, latchList.get(i), counter);
      Thread.sleep(delay * 1000);
    }
    startOneGroup(threadGroupSize, IPAddr, latchList.get(numThreadGroups - 1), counter);
    // Await on each CountDownLatch in the list
    for (CountDownLatch latch : latchList) {
      latch.await();
      counter.incrementMaxID(); // Increment maxid by 1000 after each CountDownLatch completes
    }


    // Stop the 3 threads after all thread groups have completed
    // Interrupt the additional threads to stop them
    for (Thread thread : additionalThreads) {
      thread.interrupt();
    }


    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    //int requests = (threadGroupSize * numThreadGroups * 100 + 1000) * 4;
    System.out.printf("Successful Requests:   %d\n", counter.getSuccessful());
    System.out.printf("Failed Requests:       %d\n", counter.getFailed());
    System.out.printf("Wall Time:  %d s\n", totalTime / 1000);
    System.out.printf("Throughput: %d request/s\n", counter.getSuccessful() / (totalTime / 1000));


    writeRecordsToCSV(fileName);
    loadResponseTimesFromCSV(fileName);
    //calculateAndPrintStatistics(getResponseTimes, "GET");
    //calculateAndPrintStatistics(postResponseTimes, "POST");
    calculateAndPrintStatistics(getReviewResponseTimes, "GET Review");
    //postResponseTimes.clear();
    //getResponseTimes.clear();
    getReviewResponseTimes.clear();

  }

  private static void startOneGroup(int threadGroupSize, String IPAddr, CountDownLatch completed, LoadServer counter) throws InterruptedException {
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < threadGroupSize; i++) {
      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread = () -> {

        ApiClient client = new ApiClient();
        client.setBasePath(IPAddr);
        DefaultApi apiInstance = new DefaultApi(client);
        LikeApi likeInstance = new LikeApi(client);

        for(int j = 0; j < 100; j++) {
          int retry = 0;
          while (retry < 5) {
            if (requestWrapper(apiInstance, likeInstance)) {
              break;
            } else {
              retry++;
            }
          }
          //if (retry == 5) counter.incrementFailed();
        }

        completed.countDown();

      };
      new Thread(thread).start();
    }
  }

  private static boolean requestWrapper(DefaultApi instance, LikeApi likeInstance) {

    String albumID = "1"; // String | path  parameter is album key to retrieve
    //File image = new File("/Users/s/Downloads/Example.jpg"); // File |
    File image = new File("/Users/s/Downloads/4KB.jpg"); // File |
    AlbumsProfile profile = new AlbumsProfile(); // AlbumsProfile | "artist", "title", "year
    profile.setArtist("artist");
    profile.setTitle("title");
    profile.setYear("year");
    try {
//      long startTime = System.currentTimeMillis();
//      AlbumInfo result1 = instance.getAlbumByKey(albumID);
//      long endTime = System.currentTimeMillis();
//      long latency = endTime - startTime;
      //addRecordToCSV(startTime, "GET", latency, "200");


      //startTime = System.currentTimeMillis();
      ImageMetaData result2 = instance.newAlbum(image, profile);
      likeInstance.review("like", albumID);
      likeInstance.review("like", albumID);
      likeInstance.review("dislike", albumID);
//      endTime = System.currentTimeMillis();
//      latency = endTime - startTime;
//      addRecordToCSV(startTime, "POST", latency, "200");

      return true;
    } catch (ApiException e) {
      return false;
    }
  }

  private static boolean getReviewRequestWrapper(LikeApi likeInstance) {

    String albumID = "5";
    try {
      long startTime = System.currentTimeMillis();
      Likes result = likeInstance.getLikes(albumID);
      long endTime = System.currentTimeMillis();
      long latency = endTime - startTime;
      addRecordToCSV(startTime, "GETR", latency, "200");
      return true;
    } catch (ApiException e) {
      return false;
    }
  }


  private static void addRecordToCSV(long startTime, String requestType, long latency, String responseCode) {


    String csvRecord = String.format("%s,%s,%d,%s\n", startTime, requestType, latency, responseCode);
    if (csvRecords.capacity() < csvRecord.length()) {
      csvRecords.ensureCapacity(csvRecord.length());
    }
    csvRecords.append(csvRecord);
  }

  private static void writeRecordsToCSV(String fileName) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
      // Write the content of StringBuilder to the CSV file
      writer.write(csvRecords.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Clear the StringBuilder after writing
    csvRecords.setLength(0);
  }
}