import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import core.Algorithm;
import model.LocationWithNearbyPlaces;
import model.ReferencePoint;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String uri = "mongodb+srv://admin:admin@thesis-a6lvz.mongodb.net/test?retryWrites=true&w=majority";
        String databaseName = "rssi_fingerprints";
        String apCollectionName = "sample_accesspoints";
        String rpCollectionName = "sample_referencepoints";

        MongoDatabase fingerprintDatabase = Util.connectMongoDB(uri, databaseName);
        MongoCollection apCollection = Util.fetchCollection(fingerprintDatabase, apCollectionName);
        MongoCollection rpCollection = Util.fetchCollection(fingerprintDatabase, rpCollectionName);

        ArrayList<ReferencePoint> referencePoints = Util.populateFingerprintDataSet(apCollection, rpCollection);

//        LocationWithNearbyPlaces location = Algorithm.KNN_WKNN_Algorithm(referencePoints, test, "4", true);
//        System.out.println(location.getLocationΩocation());
    }
}

