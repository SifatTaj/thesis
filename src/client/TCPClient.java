package client;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

class Client {

    // Used when the server IP or hostname is not available
    // Works by flooding UDP packets to every port and wait for appropriate response
    protected static String discoverServerIP() {
        DatagramSocket c;
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }
                }
            }

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP
            }

            //Close the port!
            c.close();
            return receivePacket.getAddress().toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected static void receiveData() throws Exception {
//        String address = discoverServerIP().substring(1);
        String address = "localhost";

        Socket socket = new Socket(address, 5000);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        String filename = "data_from_tcp.rtf";
        try {
            dos.writeUTF("hello");
            System.out.println(dis.readUTF());
            socket.close();
        } catch (EOFException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        long totalTime = 0;
        for (int i = 0 ; i < 10 ; ++i) {
            long start = System.nanoTime();
            receiveData();
            long time = System.nanoTime() - start;
            System.out.println(time);
            totalTime = totalTime + time;
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("Average Time: " + totalTime/10);
    }
}