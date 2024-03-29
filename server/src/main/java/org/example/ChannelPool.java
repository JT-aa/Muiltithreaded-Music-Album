package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.TimeoutException;

public class ChannelPool {
  private int poolSize;
  private Connection connection;
  private ArrayDeque<Channel> available;

  public ChannelPool(int poolSize, Connection connection, String queueName) {
    this.poolSize = poolSize;
    this.connection = connection;
    this.available = new ArrayDeque<>();
    for (int i = 0; i < poolSize; i++) {
      try {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false, false, false, null);
        available.add(channel);
      } catch (IOException e) {
        throw new RuntimeException("Error creating channel", e);
      }
    }
  }

  public synchronized Channel getChannel() {
    while (this.available.isEmpty()) {
      try {
        wait(); // Wait until a channel becomes available
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Interrupted while waiting for channel", e);
      }
    }
    return this.available.poll();
  }

  public synchronized void returnChannel(Channel channel) {
    if (channel != null) {
      this.available.add(channel);
      notify(); // Notify waiting threads that a channel is available
    }
  }

  public void closeAll() {
    for (Channel channel : available) {
      try {
        channel.close();
      } catch (IOException | TimeoutException e) {
        throw new RuntimeException("Error closing channel", e);
      }
    }
  }
}
