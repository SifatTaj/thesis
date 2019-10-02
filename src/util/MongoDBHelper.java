package util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import model.AccessPoint;
import model.ReferencePoint;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBHelper {

    public static MongoDatabase connectMongoDB(String uri, String databaseName) {
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient mongoClient = new MongoClient(clientURI);
        return mongoClient.getDatabase(databaseName);
    }

    public static MongoCollection fetchCollection(MongoDatabase mongoDatabase, String collectionName) {
        return mongoDatabase.getCollection(collectionName);
    }

    private static ArrayList<AccessPoint> addAccessPoints(MongoCollection ap, List<Document> readings) {

        int i = 0;
        ArrayList<AccessPoint> accessPoints = new ArrayList<>();

        MongoCursor<Document> apCursor = ap.find().iterator();
        try {
            while (apCursor.hasNext()) {
                Document apDocument = apCursor.next();
                accessPoints.add(new AccessPoint(
                        apDocument.get("_id").toString(),
                        apDocument.getString("description"),
                        apDocument.getString("ssid"),
                        apDocument.getString("mac_address"),
                        apDocument.getInteger("x"),
                        apDocument.getInteger("y"),
                        Double.parseDouble("" + readings.get(i))
                ));
                ++i;
            }
        } finally {
            apCursor.close();
        }
        return accessPoints;
    }

    public static ArrayList<ReferencePoint> populateFingerprintDataSet(MongoCollection ap, MongoCollection rp) {

        ArrayList<ReferencePoint> referencePoints = new ArrayList<>();

        MongoCursor<Document> rpCursor = rp.find().iterator();
        try {
            while (rpCursor.hasNext()) {
                Document rpDocument = rpCursor.next();
                referencePoints.add(new ReferencePoint(
                        rpDocument.get("_id").toString(),
                        rpDocument.getString("name"),
                        rpDocument.getString("description"),
                        rpDocument.getInteger("x"),
                        rpDocument.getInteger("y"),
                        addAccessPoints(ap, (List<Document>) rpDocument.get("readings"))
                ));
            }
        } finally {
            rpCursor.close();
        }

        return referencePoints;
    }
}
