import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.ReferencePoint;
import util.MongoDBUtil;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String uri = "mongodb+srv://admin:admin@thesis-a6lvz.mongodb.net/test?retryWrites=true&w=majority";
        String databaseName = "rssi_fingerprints";
        String apCollectionName = "sample_accesspoints";
        String rpCollectionName = "sample_referencepoints";

        MongoDatabase fingerprintDatabase = MongoDBUtil.connectMongoDB(uri, databaseName);
        MongoCollection apCollection = MongoDBUtil.fetchCollection(fingerprintDatabase, apCollectionName);
        MongoCollection rpCollection = MongoDBUtil.fetchCollection(fingerprintDatabase, rpCollectionName);

        ArrayList<ReferencePoint> referencePoints = MongoDBUtil.populateFingerprintDataSet(apCollection, rpCollection);

//        LocationWithNearbyPlaces location = Algorithm.KNN_WKNN_Algorithm(referencePoints, test, "4", true);
//        System.out.println(location.getLocationΩocation());
    }
}

