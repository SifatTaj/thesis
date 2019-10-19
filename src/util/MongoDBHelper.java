package util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import model.AccessPoint;
import model.FloorLayout;
import model.ReferencePoint;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

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

    public static FloorLayout generateMapLayout(MongoCollection mapCollection, int floorQuery) {

        Document doc = (Document) mapCollection.find(eq("floor", floorQuery)).first();

        int height = doc.getInteger("height");
        int width = doc.getInteger("width");
        String place = doc.getString("place");
        int floor = doc.getInteger("floor");
        int exitx = doc.getInteger("exitx");
        int exity = doc.getInteger("exity");

        List coordinateList = (List) doc.get("walls");
        int[][] walls = new int[coordinateList.size()][2];
        int index = 0;
        for (Object coordinates : coordinateList) {
            List values = (List) coordinates;
            int x = (Integer) values.get(0);
            int y = (Integer) values.get(1);
            walls[index] = new int[]{x, y};
            ++index;
        }

        return new FloorLayout(place, floor, height, width, exitx, exity, walls);
    }

    public static int detectFloor(MongoCollection floorCollection, float airPressure) {
        Document doc = (Document) floorCollection.find().first();
        int floors = doc.getInteger("floors");
        double refPressure = doc.getDouble("ref");
        double height = doc.getDouble("height");

        double refPoint = (2.746 * refPressure) / .1;
        double alt = (2.746 * airPressure) / .1;
        double elevation = refPoint - alt;
        int floor = (int) Math.round(elevation/height);

        return Math.min(floor, floors);
    }
}
