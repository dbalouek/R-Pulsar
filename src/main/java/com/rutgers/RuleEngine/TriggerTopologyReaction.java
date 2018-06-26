///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.rutgers.RuleEngine;
//
////import Topology.RuleEngineBolt.RPSingleton;
//import com.rutgers.Core.Message;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.RandomAccessFile;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Collections;
//import java.util.Scanner;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author eduard
// */
//public class TriggerTopologyReaction implements ActionDispatcher
//{
//    String topologyName;
//    String peer;
////    RPSingleton instance;
//    
//    public TriggerTopologyReaction(String topologyName, String location, String method) {  
////        try {
////            instance = RPSingleton.getInstance();
////        } catch (IOException ex) {
////            Logger.getLogger(TriggerTopologyReaction.class.getName()).log(Level.SEVERE, null, ex);
////        } catch (PeerGroupException ex) {
////            Logger.getLogger(TriggerTopologyReaction.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        
////        this.topologyName = topologyName;
////        
////        if(location.equalsIgnoreCase("cloud")){
////            peer = "urn:jxta:uuid-59616261646162614E5047205032503353545F434C4F454480008003";
////        }else {
////            peer = "urn:jxta:uuid-59616261646162614E5047205032503353545F434C4F454480008003";
////        }
//    }
//    
//    @Override
//    public void fire(String filename)
//    {
//        try {
//            String[] minxyz = null, maxxyz = null, points = null, spacing = null;
//            String[] parts = filename.split("--");
//            String[] parts2 = parts[1].split("\\.");
////            String command = "wine bin/lasinfo.exe -i " + parts2[0] + "/" + filename + " -o " + parts2[0] + "/" + filename + ".txt";
////            executeCommand(command);
//            
//            File file = new File(parts2[0] + "/" + filename + ".txt");
//            Scanner scanner = new Scanner(file);
//            while (scanner.hasNextLine()) {
//                String lineFromFile = scanner.nextLine();
//                String[] p = lineFromFile.split(":");
//                if(p[0].contains("min x y z")) {
//                    minxyz = p[1].split("\\s+");
//                    String line2 = scanner.nextLine();
//                    String[] p2 = line2.split(":");
//                    maxxyz = p2[1].split("\\s+");
//                    break;
//                }else if(p[0].contains("number of point records")) {
//                    points = p[1].split("\\s+");
//                } else if(p[0].contains("spacing")) {
//                    spacing = p[1].split("\\s+");
//                } 
//            }
//            
////            Message outMessage = new Message();
////            outMessage = FileToMessage(parts2[0] + "/" + filename, minxyz[1], minxyz[2], minxyz[3], maxxyz[1], maxxyz[2], maxxyz[3]);
////            PeerID dd = (PeerID) IDFactory.fromURI(new URI(peer));
////            outputPipe = instance.getPipeService().createOutputPipe(instance.getPipeAdvertisement(), Collections.singleton(dd), 1);
////            outputPipe.send(outMessage);
//            
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(TriggerTopologyReaction.class.getName()).log(Level.SEVERE, null, ex);
//        }
////        } catch (URISyntaxException | IOException ex) {
////            Logger.getLogger(TriggerTopologyReaction.class.getName()).log(Level.SEVERE, null, ex);
////        }
//    }
//    
//    public Message FileToMessage(String filename, String min_x, String min_y, String min_z, String max_x, String max_y, String max_z) {
////        try {
////            
////            String minXYZ = min_x + "," + min_y + "," + min_z;
////            String maxXYZ = max_x + "," + max_y + "," + max_z;
////         
////            File file = new File(filename);
////            file.createNewFile();
////            RandomAccessFile raf = new RandomAccessFile(file, "rw");
////            
////            raf.setLength(1024 * 4);
////            int size = 4096;
////            byte[] buf = new byte[size];
////            
////            raf.read(buf, 0, size);
////            
////            byte[] buffer = buf;
////            MimeMediaType mimeType = MimeMediaType.AOS;
////            
////            String[] fileNoPath = filename.split("/");
////            message.addMessageElement(new StringMessageElement("FileName", fileNoPath[1], null));
////            message.addMessageElement(new StringMessageElement("MinXYZ", minXYZ, null));
////            message.addMessageElement(new StringMessageElement("MaxXYZ", maxXYZ, null));
////            message.addMessageElement("FileObject", new ByteArrayMessageElement("file", mimeType, buffer, null));
////        } catch (IOException ex) {
////            Logger.getLogger(TriggerTopologyReaction.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return null;
//    }
//}
