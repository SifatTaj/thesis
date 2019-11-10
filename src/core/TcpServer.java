package core;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import constant.Config;
import constant.Service;
import model.*;
import util.Convert;
import util.MongoDBHelper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TcpServer {

    public static void run(String uri) {
//        Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
//        discoveryThread.start();
        try {
            ServerSocket serverSocket = new ServerSocket(Config.tcpServerPort);

            while (true) {
                System.out.println("Fingerprint server is running");
                System.out.println("Listening to port " + serverSocket.getLocalPort());
                Socket socket = serverSocket.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                try {
                    String utf = ois.readUTF();
                    System.out.println(utf);
                    String[] request = utf.split("/");
                    Service service = Service.valueOf(request[0]);
                    String place = request[1];
                    int floor = Integer.parseInt(request[2]);

                    String databaseName = place + "_rssi";
                    MongoDatabase database = MongoDBHelper.connectMongoDB(uri, databaseName);

                    if (service == Service.LOCATE) {
                        String observedRSSValues = request[3];
                        ArrayList<Float> observedRSSList = Convert.toList(observedRSSValues);

                        MongoCollection apCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_ap");
                        MongoCollection rpCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_rp");

                        ArrayList<ReferencePoint> referencePoints = MongoDBHelper.populateFingerprintDataSet(apCollection, rpCollection);
                        Location location = Localization.KNN_WKNN_Algorithm(referencePoints, observedRSSList, 4, true);
                        String json = new Gson().toJson(location);
                        oos.writeObject(json);
                        oos.flush();
                        System.out.println("Location sent");
                    }

                    else if (service == Service.LOAD_MAP) {
                        MongoCollection mapCollection = MongoDBHelper.fetchCollection(database, place + "_map_layout");
                        MongoCollection apCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_ap");
                        FloorLayout floorLayout = MongoDBHelper.generateMapLayout(mapCollection, apCollection, floor);
                        String json = new Gson().toJson(floorLayout);
                        oos.writeObject(json);
                        oos.flush();
                    }

                    else if (service == Service.NAVIGATE) {
                        String[] coordinates = request[3].split("_");
                        int startx = Integer.parseInt(coordinates[0]);
                        int starty = Integer.parseInt(coordinates[1]);
                        int startFloor = Integer.parseInt(coordinates[2]);
                        int endx = Integer.parseInt(coordinates[3]);
                        int endy = Integer.parseInt(coordinates[4]);
                        int endFloor = Integer.parseInt(coordinates[5]);

                        String collectionName = place + "_map_layout";
                        MongoCollection layoutCollection = MongoDBHelper.fetchCollection(database, collectionName);
                        MongoCollection apCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_ap");
                        FloorLayout floorLayout = MongoDBHelper.generateMapLayout(layoutCollection, apCollection, floor);

                        if (startFloor != endFloor) {
                            endx = floorLayout.getExitx();
                            endy = floorLayout.getExity();
                        }

                        Navigation navigation = new Navigation(startx, starty, endx, endy, floor, place);
                        Path path = new Path(navigation.run(floorLayout));
                        String json = new Gson().toJson(path);
                        oos.writeObject(json);
                        oos.flush();
                    }

                    else if (service == Service.DETECT_FLOOR) {
                        float airPressure = Float.parseFloat(request[3]);
                        String collectionName = place + "_building_info";
                        MongoCollection floorCollection = MongoDBHelper.fetchCollection(database, collectionName);
                        BuildingInfo buildingInfo = MongoDBHelper.detectFloor(floorCollection, airPressure);
                        FloorDetection floorDetection = new FloorDetection(buildingInfo, airPressure);
                        int detectedFloor = floorDetection.detectFloor();
                        oos.writeObject(detectedFloor);
                        oos.flush();
                    }
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