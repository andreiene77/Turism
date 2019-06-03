package client;

import controller.MainWindowController;
import model.Excursie;
import network.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static final int TIMEOUT = 10000;
    private ConnectionToServer connection;
    private LinkedBlockingQueue<Object> messages;
    private AtomicInteger id;
    private String serverAddress;
    private int port;
    private volatile boolean running;
    private volatile boolean alive;
    private List<ClientListener> listeners;
    private volatile boolean started = false;
    private MainWindowController mainWindowController;
    private List<Excursie> listExcursii = new ArrayList<>();
    private List<Excursie> listSearchExcursii = new ArrayList<>();
    private volatile boolean listExcursiiModified = false;
    private volatile boolean listSearchModified = false;
    private volatile boolean logged_in = false;
    private volatile boolean error = false;

    public Client(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
        id = new AtomicInteger(0);
        messages = new LinkedBlockingQueue<>();
        listeners = Collections.synchronizedList(new ArrayList<>());
        alive = true;
    }

    private static void force(ObjectOutputStream out) throws IOException {
        out.flush();
        out.reset();
    }

    public boolean start() {
        if (!isAlive() || running())
            return false;

        if (started)
            return false;
        synchronized (this) {
            if (started)
                return false;
            started = true;
        }

        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out = null;
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            connection = authenticate(socket, in, out);

        } catch (IOException e) {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ignored) {
            }
            return false;
        }

        if (connection == null) {
            shutDown();
            return false;
        }
        running = true;

        Thread messageHandling = new Thread(new MessageHandling(), "Message handling thread");
        messageHandling.setDaemon(true);
        messageHandling.start();
        connection.startReading();

        for (ClientListener cl : listeners)
            cl.connected(this);

        return true;
    }

    private void shutDown() {
        if (!isAlive())
            return;
        synchronized (this) {
            if (!alive)
                return;
            alive = false;
            running = false;
        }

        ConnectionToServer con = getConnection();
        if (con == null)
            return;

        try {
            if (!con.socket.isClosed())
                con.out.writeObject(ClientRequest.LOGOUT);
            con.in.close();
            con.out.close();
        } catch (IOException ignored) {
        }

        for (ClientListener cl : listeners)
            cl.disconnected(this);

        id.set(0);
        unsync();
    }

    private boolean running() {
        return running;
    }

    private boolean isAlive() {
        return alive;
    }

    private ConnectionToServer connectionInit(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        return new ConnectionToServer(socket, in, out);
    }

    private boolean send(Serializable msg) {
        return getConnection().send(msg);
    }

    private ConnectionToServer getConnection() {
        return connection;
    }

    public void addClientListener(ClientListener cl) {
        listeners.add(cl);
    }

    public void removeClientListener(ClientListener cl) {
        listeners.remove(cl);
    }

    public void sync() throws InterruptedException {
        if (!running)
            return;
        synchronized (this) {
            if (!running)
                return;
            wait();
        }
    }

    public void sync(int timeout) throws InterruptedException {
        if (!running)
            return;
        synchronized (this) {
            if (!running)
                return;
            wait(timeout);
        }
    }

    private synchronized void unsync() {
        notifyAll();
    }

    private ConnectionToServer authenticate(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
            socket.setSoTimeout(TIMEOUT);
        } catch (SocketException ignored) {
        }

        ConnectionToServer cts;
        try {
            Object handShake = in.readObject();
            if (handShake != ServerResponse.HANDSHAKE) {
                System.out.println("Unknown server. Disconnecting...");
                return null;
            }
            out.writeObject(ClientRequest.HANDSHAKE);
            force(out);
            id.set(in.readInt());

            cts = connectionInit(socket, in, out);

            Object connected = in.readObject();

            if (connected != ServerResponse.CONNECTED) {
                System.out.println(connected);
                return null;
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            socket.setSoTimeout(0);
        } catch (SocketException ignored) {
        }
        return cts;
    }

    public List<Excursie> getListExcursii() {
        listExcursiiModified = false;
        return listExcursii;
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public boolean isListExcursiiModified() {
        return listExcursiiModified;
    }

    public boolean isListSearchModified() {
        return listSearchModified;
    }

    public boolean isLogged_in() {
        return logged_in;
    }

    public void requestLogin(String user, String pass) {
        List<Object> data = new ArrayList<>();
        data.add(user);
        data.add(pass);
        Request request = new Request(ClientRequest.LOGIN, data);
        send(request);
    }

    public void requestLogout() {
        send(ClientRequest.LOGOUT);
    }

    public void requestListExcursii() {
        send(ClientRequest.GET_EXCURSII_LIST);
    }

    public List<Excursie> getListSearchExcursii() {
        listSearchModified = false;
        return listSearchExcursii;
    }

    public void requestRezervare(Excursie excursie, String nume, String nrTelefon, int bilete) {
        ArrayList<Object> data = new ArrayList<>();
        data.add(excursie);
        data.add(nume);
        data.add(nrTelefon);
        data.add(bilete);
        Request request = new Request(ClientRequest.MAKE_REZERVARE, data);
        send(request);
    }

    public void requestSearchList(String obiectiv, String startTime, String endTime) {
        ArrayList<Object> data = new ArrayList<>();
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm");
        data.add(obiectiv);
        LocalTime start = LocalTime.parse(startTime, parser);
        LocalTime end = LocalTime.parse(endTime, parser);
        data.add(start);
        data.add(end);
        Request request = new Request(ClientRequest.GET_EXCURSII_LIST, data);
        send(request);
    }

    public boolean isError() {
        return error;
    }

    private class MessageHandling implements Runnable {
        public void run() {
            while (running()) {
                try {
                    Object msg = messages.take();
                    if (msg instanceof Command) {
                        listeners.forEach(cl -> cl.commandReceived(Client.this, (Command) msg));
                        handleCommand((Command) msg);
                    } else {
                        listeners.forEach(cl -> cl.messageReceived(Client.this, msg));
                        handleResponse(msg);
                    }
                } catch (InterruptedException ignored) {
                } catch (Throwable t) {
                    t.printStackTrace();
                    shutDown();
                }
            }
        }

        private void handleResponse(Object msg) {
            Response response = (Response) msg;
            switch (response.type()) {
                case GIVE_EXCURSII_LIST:
                    listExcursii = (List<Excursie>) response.data();
                    listExcursiiModified = true;
                    break;
                case GIVE_SEARCH_LIST:
                    listSearchExcursii = (List<Excursie>) response.data();
                    listSearchModified = true;
                    break;
                case UPDATE_NOTIFICATION:
                    listExcursii = (List<Excursie>) response.data();
                    listExcursiiModified = true;
                    mainWindowController.updateTableView(listExcursii);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + response.type());
            }
        }

        private void handleCommand(Command cmd) {
            ServerResponse serverResponse = (ServerResponse) cmd;
            switch (serverResponse) {
                case LOGGED_IN:
                    logged_in = true;
                    System.out.println("Log in successful!");
                    break;
                case LOGGED_OUT:
                    logged_in = false;
                    System.out.println("Logged out");
                    break;
                case DISCONNECTED:
                    logged_in = false;
                    System.out.println("Disconnected");
                    break;
                case ERROR:
                    error = true;
                    mainWindowController.handleError();
                    error = false;
                    break;
                case REZERVARE_MADE:
                    mainWindowController.handleRezevareSuccess();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + serverResponse);
            }
        }
    }

    public class ConnectionToServer {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        ConnectionToServer(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            if (socket.isClosed()) throw new IllegalStateException("Server's socket is closed.");
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        private void startReading() {
            Thread read = new Thread(new Reading(), "Message reading thread");
            read.setDaemon(true);
            read.start();
        }

        boolean send(Serializable msg) {
            if (msg == null) return false;

            try {
                if (socket.isClosed()) {
                    shutDown();
                    return false;
                }
                out.writeObject(msg);
                force(out);

                if (msg instanceof Command) listeners.forEach(cl -> cl.commandSent(Client.this, (Command) msg));
                else listeners.forEach(cl -> cl.messageSent(Client.this, msg));

                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void finalize() {
            shutDown();
        }

        private class Reading implements Runnable {
            public void run() {
                while (running()) {
                    try {
                        Object msg = in.readObject();
                        messages.put(msg);
                    } catch (InterruptedException ignored) {
                    } catch (IOException e) {
                        shutDown();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        shutDown();
                    }
                }
            }
        }
    }
}
