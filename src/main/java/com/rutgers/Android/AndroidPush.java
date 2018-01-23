/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Android;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author eduard
 */
public class AndroidPush {
    /**
     * Replace SERVER_KEY with your SERVER_KEY generated from FCM
     * Replace DEVICE_TOKEN with your DEVICE_TOKEN
     */
    private String serverKey = "";
    private String deviceToken = "";
    
    public AndroidPush(String serverKey, String deviceToken) {
        this.serverKey = serverKey;
        this.deviceToken = deviceToken;
    }
    
    public void sendPushNotification(String title, String message) throws Exception {
        String pushMessage = "{\"data\":{\"title\":\"" +
                title +
                "\",\"message\":\"" +
                message +
                "\"},\"to\":\"" +
                deviceToken +
                "\"}";
        // Create connection to send FCM Message request.
        URL url = new URL("https://fcm.googleapis.com/fcm/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "key=" + serverKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Send FCM message content.
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(pushMessage.getBytes());

        System.out.println(conn.getResponseCode());
        System.out.println(conn.getResponseMessage());
    }
    
    public String getServerKey() {
        return serverKey;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setServerKey(String ServerKey) {
        this.serverKey = ServerKey;
    }

    public void setDeviceToken(String DeviceToken) {
        this.deviceToken = DeviceToken;
    }
}
