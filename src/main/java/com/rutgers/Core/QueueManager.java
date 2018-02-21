/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.FanOutQueueImpl;
import com.leansoft.bigqueue.IBigQueue;
import com.leansoft.bigqueue.IFanOutQueue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author eduard
 */
public class QueueManager {
    private Map<String,IBigQueue> queue = null;
    private Map<String,IFanOutQueue> fqueue = null;
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    public static Random r = null;
        
    public QueueManager() {
        queue = new HashMap<>();  
        fqueue = new HashMap<>();
        r = new Random();
    }
    
    public String randomTopic() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        
        return sb.toString();
    }
    
    public IBigQueue getQueue(String topic) {
        return queue.get(topic);
    }
    
    public void createQueue(String dir, String topic) throws IOException {
        IBigQueue q = null;
        q = new BigQueueImpl(dir, topic);
        queue.put(topic, q);
    }
    
    public IFanOutQueue getFanOutQueue(String topic) {
        return fqueue.get(topic);
    }
    
    public void createFanOutQueue(String dir, String topic) throws IOException {
        IFanOutQueue q = null;
        q = new FanOutQueueImpl(dir, topic);
        fqueue.put(topic, q);
    }
    
    public void addToQueue(String topic, byte[] payload) throws IOException {
        IBigQueue q = queue.get(topic);
        q.enqueue(payload);
    }
    
    public void addToFanOutQueue(String topic, String payload) throws IOException {
        IFanOutQueue q = fqueue.get(topic);
        q.enqueue(payload.getBytes());
    }
    
    public byte[] removeFromQueue(String topic) throws IOException {
        IBigQueue q = queue.get(topic);
        return q.dequeue();
    }
        
    public byte[] removeFromFanOutQueue(String topic) throws IOException {
        IFanOutQueue q = fqueue.get(topic);
        return q.dequeue(topic);
    }
}
