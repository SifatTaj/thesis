package client;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class TcpClient {

    public static void requestLocation(String observedRSSValues) {

//        String address = discoverServerIP().substring(1);
        try {
            String address = "localhost";
            String myLocation = "";

            Socket socket = new Socket(address, 5000);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            try {
                dos.writeUTF(observedRSSValues);
                myLocation = dis.readUTF();
                System.out.println(myLocation);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        requestLocation("-91 -67 -75 -33");
    }
}