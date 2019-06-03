import model.Agentie;
import network.Logger;
import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerStarter {
    public static void main(String[] args) {
        Server server = new Server(55555);
        if (server.start()) {
            Logger logger = new Logger();
            server.addServerListener(logger);
            System.out.println("Server started.");
            server.setClientLimit(5);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (server.running()) {
                System.out.println("1. See online users");
                System.out.println("2. Exit");
                try {
                    String cmd = br.readLine();
                    switch (cmd) {
                        case "1":
                            for (Agentie user :
                                    server.getLoggedInUsers()) {
                                System.out.println(user.getId());
                            }
                            break;
                        case "2":
                            server.shutDown();
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + cmd);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else
            System.out.println("Problem Starting the server");
    }
}
