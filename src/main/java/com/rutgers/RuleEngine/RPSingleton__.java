package com.rutgers.RuleEngine;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package RuleEngine;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.util.Random;
//import net.jxta.document.AdvertisementFactory;
//import net.jxta.exception.PeerGroupException;
//import net.jxta.id.IDFactory;
//import net.jxta.peergroup.PeerGroup;
//import net.jxta.peergroup.PeerGroupID;
//import net.jxta.pipe.OutputPipe;
//import net.jxta.pipe.PipeID;
//import net.jxta.pipe.PipeService;
//import net.jxta.platform.NetworkConfigurator;
//import net.jxta.platform.NetworkManager;
//import net.jxta.protocol.PipeAdvertisement;
//
///**
// *
// * @author eduard
// */
//public class RPSingleton {
//    
//    static String topologyName;
//    static String alphabet = "abcdefghijklmnopqrstuvwxyz";
//    static File ConfigurationFile = null;
//    static Random r = new Random();
//    static NetworkManager MyNetworkManager = null;
//    static PeerGroup NetPeerGroup = null;
//    static PipeService pipeService = null;
//    static String peer;
//    
//    private static RPSingleton instance = null;
//    private RPSingleton() {}
//    
//    public static RPSingleton getInstance() throws IOException, PeerGroupException {
//        if(instance == null) {
//            instance = new RPSingleton();
//            
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < 10; i++) {
//                sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
//            }
//            String randomString = sb.toString();
//            String RV_Name = "ST_" + randomString;
//
//            ConfigurationFile = new File("." + System.getProperty("file.separator") + RV_Name);
//
//            MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS, RV_Name, ConfigurationFile.toURI());
//            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
//            MyNetworkConfigurator.setTcpPort(1234);
//            MyNetworkConfigurator.setTcpInterfaceAddress(InetAddress.getLocalHost().getHostAddress());
//            MyNetworkConfigurator.setTcpEnabled(true);
//            MyNetworkConfigurator.setTcpIncoming(true);
//            MyNetworkConfigurator.setTcpOutgoing(true);
//            MyNetworkConfigurator.setUseMulticast(false);
//            MyNetworkConfigurator.setPeerID(IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, RV_Name.getBytes()));
//
//            MyNetworkManager.startNetwork();
//            NetPeerGroup = MyNetworkManager.getNetPeerGroup();
//            NetPeerGroup.getRendezVousService().setAutoStart(false);
//
//            pipeService = NetPeerGroup.getPipeService();
//        }
//        return instance;
//    }
//    
//    public PipeService getPipeService() {
//        return pipeService;
//    }
//    
//    public PipeAdvertisement getPipeAdvertisement() {
//        // Creating a Pipe Advertisement    
//        String tmp = "SmartCity";
//        PipeAdvertisement MyPipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
//        PipeID MyPipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, tmp.getBytes());
//                
//        MyPipeAdvertisement.setPipeID(MyPipeID);
//        MyPipeAdvertisement.setType(PipeService.PropagateType);
//        MyPipeAdvertisement.setName("RV Pipe");
//        
//        return MyPipeAdvertisement;
//    }
//    
//    public void stopNetwork() {
//        MyNetworkManager.stopNetwork();
//    }
//}
