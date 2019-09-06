package com.rutgers.Examples.Storm;

import java.util.Map;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
/**
 * 
 * This is and example of the use of the R-Pulsar API.
 * This class gets the sentences from the Spout and brakes the sentence into words.
 * @author eduard
 *
 */

public class SplitSentenceBolt extends BaseBasicBolt {
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
      return null;
    }
    
	@Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
      String sentence = tuple.getStringByField("sentence");
      //Split the sentence into words
      String words[] = sentence.split(" ");
      for (String w : words) {
    	  //Emit each word separate to the WordCountBolt
        basicOutputCollector.emit(new Values(w));
      }
    }
}
