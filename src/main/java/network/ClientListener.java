package network;

import client.Client;

public interface ClientListener {

    void messageReceived(Client client, Object msg);

    void commandReceived(Client client, Command cmd);

    void disconnected(Client client);

    void messageSent(Client client, Object msg);

    void commandSent(Client client, Command cmd);

    void connected(Client client);

}
