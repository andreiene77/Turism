package network;

import server.Server;
import server.Server.ConnectionToClient;

public interface ServerListener {

    void clientConnected(Server server, ConnectionToClient client);

    void messageReceived(Server server, ConnectionToClient client, Object msg);

    void commandReceived(Server server, ConnectionToClient client, Command cmd);

    void clientDisconnected(Server server, ConnectionToClient client);

    void messageSent(Server server, ConnectionToClient toClient, Object msg);

    void commandSent(Server server, ConnectionToClient toClient, Command cmd);

}
