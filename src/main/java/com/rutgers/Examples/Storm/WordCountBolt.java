package com.rutgers.Examples.Storm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

/**
 * 
 * This is and example of the use of the R-Pulsar API.
 * This class counts the words and emits the results
 * @author eduard
 *
 */

public class WordCountBolt extends BaseWindowedBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word", "count"));
    }

	@Override
	public void execute(TupleWindow inputWindow) {
		// TODO Auto-generated method stub
		
        List<Tuple> tuplesInWindow = inputWindow.get();
        List<Tuple> newTuples = inputWindow.getNew();
        List<Tuple> expiredTuples = inputWindow.getExpired();

       System.out.println("Events in current window: " + tuplesInWindow.size());
        
        for (Tuple tuple : newTuples) {
      	  Integer count = counts.get(tuple.getValue(0));
    	  if (count == null)
    	    count = 0;
    	  count++;
    	  counts.put((String) tuple.getValue(0), count);
        }
        for (Tuple tuple : expiredTuples) {
    	  Integer count = counts.get(tuple.getValue(0));
    	  if (count == null)
    	    count = 0;
    	  count--;
    	  counts.put((String) tuple.getValue(0), count);
        }
		
        System.out.println("Window Count: " + counts.toString());
	}
}
