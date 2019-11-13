package com.rutgers.RuleEngine;

import com.google.protobuf.ProtocolStringList;
import static com.rutgers.Core.Globals._MAX_QUERY_DIM_;
import static com.rutgers.Core.Globals._QUERY_BITS_;
import com.rutgers.Core.LocationKeyManager;
import com.rutgers.Core.Manager;
import com.rutgers.Core.Message;
import com.rutgers.Core.RP;
import com.rutgers.Hilbert.HilbertCurve;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
* This class is used to send a specific AR Profile when a rule is satisfied.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/
public class TriggerProfileReaction implements ActionDispatcher {
    
    Message.ARMessage msg;
    Manager mgr;
    RP rpOne = null;
    RP rpTwo = null;
    LocationKeyManager lkManager = null;
    HilbertCurve hc = null;
    
    public TriggerProfileReaction(Message.ARMessage msg) throws IOException {
        this.msg = msg;
        mgr = Manager.getInstance();
        rpOne = mgr.getRpOne();
        rpTwo = mgr.getRpTwo();
        hc = HilbertCurve.bits(_QUERY_BITS_).dimensions(_MAX_QUERY_DIM_);
    }

    @Override
    public void fire() {
        try {
        	System.out.println("Fire Rule");
            ProtocolStringList payloadList = msg.getPayloadList();
            List<String> singleList = msg.getHeader().getProfile().getSingleList();
            Number160 index = hc.index(singleList, payloadList.toArray(new String[payloadList.size()]));
            PeerAddress peer = lkManager.nearestKey(index); 
            
            if(rpTwo != null) {
                rpTwo.sendDirectMessageNonBlocking(peer, msg);
            }else {
                rpOne.sendDirectMessageNonBlocking(peer, msg);
            }
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(TriggerProfileReaction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
