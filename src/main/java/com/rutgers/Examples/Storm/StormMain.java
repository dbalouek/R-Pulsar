/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples.Storm;

import com.google.common.io.Resources;
import com.rutgers.Core.Listener;
import com.rutgers.Core.Message;
import com.rutgers.Core.MessageListener;
import com.rutgers.Core.PulsarConsumer;
import com.rutgers.Examples.HelloWorldPublisher;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt.Count;
import org.apache.storm.tuple.Fields;

/**
 * This is and example of the use of the R-Pulsar API.
 * This example shows how to build a R-Pulsar consumer.
 * @param keys
 * @return
 */
public class StormMain {    
    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        try {
            // Load the consumer properties into an InputStream              
            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("spout", new RandomSentenceSpout());
            builder.setBolt("split", new SplitSentenceBolt()).shuffleGrouping("spout");
            builder.setBolt("count", new WordCountBolt().withWindow(Count.of(5), Count.of(3))).fieldsGrouping("split", new Fields("word"));
            
            Config conf = new Config();
            conf.setDebug(true);
            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            
            cluster.submitTopology("word-count", conf, builder.createTopology());
            
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(HelloWorldPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
