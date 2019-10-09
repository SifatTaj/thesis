package client;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.FloorLayout;
import org.bson.Document;
import util.MongoDBHelper;

import java.util.ArrayList;
import java.util.List;

public class AddMapLayout {

    public static  int[][] walls = {{0,8},{0,7},{0,3},{0,5},{1,8},{1,7},{1,3},{1,5},{2,8},{2,7},{2,3},{2,5},{3,8},{3,7},{3,3},{3,5},{3,0},{3,1},{4,7},{5,7},{7,8},{7,7},{7,5},{7,0},{7,1},{7,6},{8,7},{9,3},{9,7},{10,3}};

    public static void main(String[] args) {

        String uri = "mongodb://localhost:27017";
        String databaseName = "home_rssi";
        String collectionName = "home_map_layout";

        MongoDatabase fingerprintDatabase = MongoDBHelper.connectMongoDB(uri, databaseName);
        MongoCollection layoutCollection = MongoDBHelper.fetchCollection(fingerprintDatabase, collectionName);

//        Document doc = new Document();
//        List<List<Integer>> wallList = new ArrayList<>();
//
//        for(int[] wall : walls) {
//            List<Integer> coordinates = new ArrayList<>();
//            coordinates.add(wall[0]);
//            coordinates.add(wall[1]);
//            wallList.add(coordinates);
//        }
//        doc.put("walls", wallList);
//        layoutCollection.insertOne(doc);

        FloorLayout floorLayout = MongoDBHelper.generateMapLayout(layoutCollection, 3);
    }
}
