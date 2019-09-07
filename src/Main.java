import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.ReferencePoint;
import util.Util;

import java.util.ArrayList;

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
    }
}

