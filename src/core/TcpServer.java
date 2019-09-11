package core;

import model.LocationWithNearbyPlaces;
import model.ReferencePoint;
import util.Convert;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TcpServer {

    public static void run(ArrayList<ReferencePoint> referencePoints) {
//        Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
//        discoveryThread.start();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {
                System.out.println("Fingerprint server is running");
                Socket socket = serverSocket.accept();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                try {
                    String observedRSSValues = dis.readUTF();
                    ArrayList<Float> observedRSSList = Convert.toList(observedRSSValues);
                    LocationWithNearbyPlaces location = Algorithms.KNN_WKNN_Algorithm(referencePoints, observedRSSList, 4, true);
                    dos.writeUTF(location.getLocation());
                    dos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

class DiscoveryThread implements Runnable {

    DatagramSocket socket;

    @Override
    public void run() {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_FINGERPRINTSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_FINGERPRINTSERVER_REQUEST".getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static DiscoveryThread getInstance() {
        return DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {
        private static final DiscoveryThread INSTANCE = new DiscoveryThread();
    }

}