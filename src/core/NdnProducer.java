package core;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.LocationWithNearbyPlaces;
import model.ReferencePoint;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SafeBag;
import net.named_data.jndn.util.Blob;
import util.Convert;
import util.MongoDBHelper;
import util.RsaKeyGen;

import java.util.ArrayList;

public class NdnProducer {

    static String apCollectionName = "3rd_floor_aps";
    static String rpCollectionName = "3rd_floor_rps";

    public static void run(MongoDatabase database) {

        MongoCollection apCollection = MongoDBHelper.fetchCollection(database, apCollectionName);
        MongoCollection rpCollection = MongoDBHelper.fetchCollection(database, rpCollectionName);

        ArrayList<ReferencePoint> referencePoints = MongoDBHelper.populateFingerprintDataSet(apCollection, rpCollection);

        try {
            Face face = new Face();

            KeyChain keyChain = new KeyChain("pib-memory:", "tpm-memory:");
            keyChain.importSafeBag(new SafeBag
                    (new Name("/testname/KEY/123"),
                            new Blob(RsaKeyGen.DEFAULT_RSA_PRIVATE_KEY_DER, false),
                            new Blob(RsaKeyGen.DEFAULT_RSA_PUBLIC_KEY_DER, false)
                    )
            );

            face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
            Name name = new Name("/findlocation");
            System.out.println("Register prefix  " + name.toUri());
            SendLocation sendLocation = new SendLocation(keyChain, referencePoints);
            face.registerPrefix(name, sendLocation, sendLocation);

            while (true) {
                face.processEvents();
//                Thread.sleep(5);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SendLocation implements OnInterestCallback, OnRegisterFailed {
    private KeyChain keyChain;
    int responseCount = 0;
    ArrayList<ReferencePoint> referencePoints;

    public SendLocation(KeyChain keyChain, ArrayList<ReferencePoint> referencePoints) {
        this.keyChain = keyChain;
        this.referencePoints = referencePoints;
    }

    @Override
    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
        ++responseCount;
        Data data = new Data(interest.getName());
        String[] observedRSSString = interest.getName().toString().split("/");
        ArrayList<Float> observedRSSValue = Convert.toList(observedRSSString[observedRSSString.length - 1]);
        LocationWithNearbyPlaces location = KNN.KNN_WKNN_Algorithm(referencePoints, observedRSSValue, 4, true);

        try {
            data.setContent(new Blob(location.getLocation()));
            keyChain.sign(data);
            face.putData(data);
            System.out.println("New Location sent at " + System.currentTimeMillis());
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
