/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Storm;

import com.rutgers.Core.Utils;

/**
* This is a simple API abstraction to support Apache Storm.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/
public class Storm {
    
	/**
	 * Start an Apache Storm topology.
	 * @param jar
	 * @param mainClass
	 * @param arg
	 */
    public static  void StartTopology(String jar, String mainClass, String arg) {
        Utils.executeCommand("storm jar " + jar + " " + mainClass + " " + arg);
    }
    
    /**
     * Stop an Apache storm topology.
     * @param topologyName
     */
    public static void KillTopology(String topologyName) {
        Utils.executeCommand("storm kill " + topologyName);
    }
}
