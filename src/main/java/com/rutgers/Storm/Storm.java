/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Storm;

import com.rutgers.Core.Utils;

/**
 *
 * @author eduard
 */
public class Storm {
    
    public static  void StartTopology(String jar, String mainClass, String arg) {
        Utils.executeCommand("storm jar " + jar + " " + mainClass + " " + arg);
    }
    
    public static void KillTopology(String topologyName) {
        Utils.executeCommand("storm kill " + topologyName);
    }
}
