import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class Main {

    private static ArrayList<Fingerprint> fingerprints = new ArrayList<>();
    private static MongoClientURI clientURI;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection collection;

    private static void connectMongoDB(String uri, String databaseName, String collectionName) {
        clientURI = new MongoClientURI(uri);
        mongoClient = new MongoClient(clientURI);
        database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
    }

    private static void fetchDataSet() {
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                fingerprints.add(new Fingerprint(
                        document.getInteger("ap0"),
                        document.getInteger("ap1"),
                        document.getInteger("ap2"),
                        document.getInteger("rp")
                ));
            }
        } finally {
            cursor.close();
        }
    }

    public static void main(String[] args) {
        String uri = "mongodb+srv://admin:admin@thesis-a6lvz.mongodb.net/test?retryWrites=true&w=majority";
        String databaseName = "rssi_fingerprints";
        String collectionName = "home";

        connectMongoDB(uri, databaseName, collectionName);
        fetchDataSet();
    }
}
