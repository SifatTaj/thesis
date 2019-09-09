package core;

import model.ReferencePoint;
import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SafeBag;
import net.named_data.jndn.util.Blob;
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
            Name name = new Name("/sendtestdata");
            System.out.println("Register prefix  " + name.toUri());
            SendLocation sendMeme = new SendLocation(keyChain, "testing");
            face.registerPrefix(name, sendMeme, sendMeme);

            while (sendMeme.responseCount < 1) {
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
    private String location;

    public SendLocation(KeyChain keyChain, String location) {
        this.keyChain = keyChain;
        this.location = location;
    }

    @Override
    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
        ++responseCount;
        Data data = new Data(interest.getName());

        try {
            data.setContent(new Blob(location));
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
