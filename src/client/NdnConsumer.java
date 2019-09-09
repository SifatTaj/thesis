package client;

import net.named_data.jndn.*;
import java.util.concurrent.TimeUnit;

public class NdnConsumer {

    public static void main(String[] args) throws InterruptedException {

        Interest.setDefaultCanBePrefix(true);

        try {
            Face face = new Face();
            ReceiveLocation receiveFile = new ReceiveLocation();
            Name name = new Name("/sendtestdata");
            face.expressInterest(name, receiveFile, receiveFile);
            while (receiveFile.callbackCount < 1) {
                face.processEvents();
                Thread.sleep(5);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        TimeUnit.SECONDS.sleep(1);
    }
}

class ReceiveLocation implements OnData, OnTimeout {
    public int callbackCount = 0;
    public byte[] bytes;

    @Override
    public void onData(Interest interest, Data data) {
        ++callbackCount;
        String location = data.getContent().toString();
        System.out.println(location);
    }

    @Override
    public void onTimeout(Interest interest) {
        ++callbackCount;
        System.out.println("Time out for interest " + interest.getName().toUri());
    }
}
