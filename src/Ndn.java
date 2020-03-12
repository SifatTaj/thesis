import constant.Config;
import core.NdnProducer;

import java.util.Scanner;

public class Ndn {

  public static void main(String[] args) {
//        String uri = "mongodb+srv://admin:admin@thesis-a6lvz.mongodb.net/test?retryWrites=true&w=majority";

    try {
      Config.nfdAddress = args[0];
    } catch (Exception e) {
      Config.nfdAddress = "localhost";
    }

    try {
      Config.ndnPrefix = args[1];
    } catch (Exception e) {
      Config.ndnPrefix = "/ips";
    }

    String uri = "mongodb://localhost:27017";

    NdnProducer.run(uri);
  }
}

