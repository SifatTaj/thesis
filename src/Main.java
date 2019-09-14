import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import core.NdnProducer;
import core.TcpServer;
import model.ReferencePoint;
import util.MongoDBHelper;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String uri = "mongodb+srv://admin:admin@thesis-a6lvz.mongodb.net/test?retryWrites=true&w=majority";
        String databaseName = "rssi_fingerprints";
//
        MongoDatabase fingerprintDatabase = MongoDBHelper.connectMongoDB(uri, databaseName);

//        TcpServer.run();
        NdnProducer.run(fingerprintDatabase);
    }
}

