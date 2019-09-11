package core;

import model.LocationWithNearbyPlaces;
import model.ReferencePoint;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SafeBag;
import net.named_data.jndn.util.Blob;
import util.Convert;
import util.RsaKeyGen;

import java.util.ArrayList;

public class NdnProducer {

    public static void run(ArrayList<ReferencePoint> referencePoints) {
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

            while (sendLocation.responseCount < 1) {
                face.processEvents();
                Thread.sleep(5);
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
        LocationWithNearbyPlaces location = Algorithms.KNN_WKNN_Algorithm(referencePoints, observedRSSValue, 4, true);

        try {
            data.setContent(new Blob(location.getLocation()));
            keyChain.sign(data);
            face.putData(data);
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
