import constant.Config;
import core.TcpServer;

public class Tcp {

    public static void main(String[] args) {
//        String uri = "mongodb+srv://admin:admin@thesis-a6lvz.mongodb.net/test?retryWrites=true&w=majority";

        try {
            Config.tcpServerPort = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Config.tcpServerPort = 5000;
        }

        String uri = "mongodb://localhost:27017";

        TcpServer.run(uri);
    }
}

