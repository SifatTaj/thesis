package core;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import constant.Config;
import constant.Service;
import model.*;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SafeBag;
import net.named_data.jndn.util.Blob;
import util.Convert;
import util.MongoDBHelper;
import util.RsaKeyGen;

import java.util.ArrayList;

public class NdnProducer {

    public static String uri;

    public static void run(String uri) {

        try {
            Face face = new Face(Config.nfdAddress);

            KeyChain keyChain = new KeyChain("pib-memory:", "tpm-memory:");
            keyChain.importSafeBag(new SafeBag
                    (new Name("/testname/KEY/123"),
                            new Blob(RsaKeyGen.DEFAULT_RSA_PRIVATE_KEY_DER, false),
                            new Blob(RsaKeyGen.DEFAULT_RSA_PUBLIC_KEY_DER, false)
                    )
            );

            face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
            Name name = new Name("/ips");
            System.out.println("Register prefix  " + name.toUri());
            SendData sendData = new SendData(keyChain, uri);
            face.registerPrefix(name, sendData, sendData);

            while (true) {
                face.processEvents();
//                Thread.sleep(5);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SendData implements OnInterestCallback, OnRegisterFailed {
    private KeyChain keyChain;
    private String uri;
    int responseCount = 0;

    public SendData(KeyChain keyChain, String uri) {
        this.keyChain = keyChain;
        this.uri = uri;
    }

    @Override
    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
        ++responseCount;
        Data data = new Data(interest.getName());

        String[] request = interest.getName().toString().split("/");
        Service service = Service.valueOf(request[2]);
        String place = request[3];
        int floor = Integer.parseInt(request[4]);

        String databaseName = place + "_rssi";
        MongoDatabase database = MongoDBHelper.connectMongoDB(uri, databaseName);

        try {
            if (service == Service.LOCATE) {
                String observedRSSValues = request[5];
                ArrayList<Float> observedRSSList = Convert.toList(observedRSSValues);

                MongoCollection apCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_ap");
                MongoCollection rpCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_rp");

                ArrayList<ReferencePoint> referencePoints = MongoDBHelper.populateFingerprintDataSet(apCollection, rpCollection);
                Location location = Localization.KNN_WKNN_Algorithm(referencePoints, observedRSSList, 4, true);
                String json = new Gson().toJson(location);
                data.setContent(new Blob(json));
                keyChain.sign(data);
                face.putData(data);
                System.out.println("Location sent");
            }

            else if (service == Service.LOAD_MAP) {
                MongoCollection mapCollection = MongoDBHelper.fetchCollection(database, place + "_map_layout");
                MongoCollection apCollection = MongoDBHelper.fetchCollection(database, place + "_" + floor + "_ap");
                FloorLayout floorLayout = MongoDBHelper.generateMapLayout(mapCollection, apCollection, floor);
                String json = new Gson().toJson(floorLayout);
                data.setContent(new Blob(json));
                keyChain.sign(data);
                face.putData(data);
                System.out.println("Maplayout sent");
            }

            else if (service == Service.NAVIGATE) {
                String[] coordinates = request[5].split("_");
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

                if(startFloor != endFloor) {
                    endx = floorLayout.getExitx();
                    endy = floorLayout.getExity();
                }

                Navigation navigation = new Navigation(startx, starty, endx, endy, floor, place);
                Path path = new Path(navigation.run(floorLayout));
                String json = new Gson().toJson(path);
                data.setContent(new Blob(json));
                keyChain.sign(data);
                face.putData(data);
                System.out.println("Navigation sent");
            }

            else if (service == Service.DETECT_FLOOR) {
                float airPressure = Float.parseFloat(request[5]);
                String collectionName = place + "_building_info";
                MongoCollection floorCollection = MongoDBHelper.fetchCollection(database, collectionName);
                BuildingInfo buildingInfo = MongoDBHelper.detectFloor(floorCollection, airPressure);
                FloorDetection floorDetection = new FloorDetection(buildingInfo, airPressure);
                int detectedFloor = floorDetection.detectFloor();
                data.setContent(new Blob(detectedFloor + ""));
                keyChain.sign(data);
                face.putData(data);
                System.out.println("Floor sent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRegisterFailed(Name name) {
        ++responseCount;
        System.out.println("Register failed for prefix " + name.toUri());
    }
}
