package server;

import model.Agentie;
import model.Excursie;
import network.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.ExcursiiManagementService;
import service.LoginService;
import service.RezervareManagementService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private static final int TIMEOUT = 10000;
    private static Random rand = new Random();
    private ServerSocket serverSocket;
    private Map<Integer, ConnectionToClient> clients;
    private LinkedBlockingQueue<Message> messages;
    private int port;
    private int messageHandlingThreadCount;
    private volatile boolean running;
    private volatile boolean alive;
    private int clientLimit;
    private List<ServerListener> listeners;
    private volatile boolean started = false;
    private List<Agentie> loggedInUsers;

    private Server(int port, int messageHandlingThreadCount) {
        this.port = port;
        clients = Collections.synchronizedMap(new HashMap<>());
        messages = new LinkedBlockingQueue<>();
        listeners = Collections.synchronizedList(new ArrayList<>());
        loggedInUsers = Collections.synchronizedList(new ArrayList<>());
        this.messageHandlingThreadCount = Math.max(1, messageHandlingThreadCount);
        clientLimit = -1;
        alive = true;
    }

    public Server(int port) {
        this(port, 1);
    }

    private static void force(ObjectOutputStream out) throws IOException {
        out.flush();
        out.reset();
    }

    public List<Agentie> getLoggedInUsers() {
        return loggedInUsers;
    }

    private ConnectionToClient getClient(int id) {
        return clients.get(id);
    }

    private Collection<ConnectionToClient> getClients() {
        return clients.values();
    }

    public boolean running() {
        return running;
    }

    private boolean isAlive() {
        return alive;
    }

    private ConnectionToClient connectionInit(int id, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        return new ConnectionToClient(id, socket, in, out);
    }

    private boolean containsId(int id) {
        return clients.containsKey(id);
    }

    public void setClientLimit(int limit) {
        clientLimit = limit;
    }

    private boolean send(Serializable msg, int id) {
        if (!running() || msg == null)
            return false;
        ConnectionToClient ctc = getClient(id);
        if (ctc == null)
            return false;
        return ctc.send(msg);
    }

    private boolean sendToAll(Serializable msg) {
        if (!running())
            return false;
        boolean passed = true;
        for (ConnectionToClient c : getClients())
            passed &= c.send(msg);
        return passed; // all went through
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

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        running = true;

        Thread acception = new Thread(new Acception(), "Client acception thread");

        for (int i = 0; i < messageHandlingThreadCount; i++) {
            Thread t = new Thread(new MessageHandling(), "Received messages handler thread #" + i);
            t.setDaemon(true);
            t.start();
        }

        acception.setDaemon(true);
        acception.start();

        return true;
    }

    public void shutDown() {
        if (!isAlive())
            return;

        synchronized (this) {
            if (!isAlive())
                return;
            alive = false;
            running = false;
            unsync();
        }

        for (ConnectionToClient c : getClients())
            c.localShutDown();
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

    private void sync(int timeout) throws InterruptedException {
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

    public void addServerListener(ServerListener sl) {
        listeners.add(sl);
    }

    public void removeServerListener(ServerListener sl) {
        listeners.remove(sl);
    }

    @Override
    protected void finalize() {
        shutDown();
    }

    private static class Message {
        Object msg;
        int id;

        Message(Object msg, int id) {
            this.msg = msg;
            this.id = id;
        }
    }

    private class Acception implements Runnable {
        public void run() {
            while (running() && !serverSocket.isClosed()) {
                Socket socket;
                ObjectInputStream in;
                ObjectOutputStream out = null;
                try {
                    socket = serverSocket.accept();
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    authenticate(socket, in, out);
                } catch (IOException e) {
                    try {
                        if (out != null)
                            out.close();
                    } catch (IOException ignored) {
                    }
                } catch (RuntimeException rte) {
                    rte.printStackTrace();
                    shutDown(); // Programmers error, why continue ??
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        private void authenticate(final Socket socket, final ObjectInputStream in, final ObjectOutputStream out) {
            Thread authentication = new Thread("Authentication thread") {
                public void run() {
                    if (!running())
                        return;

                    ConnectionToClient ctc = null;
                    int id = 0;

                    try {
                        socket.setSoTimeout(TIMEOUT);
                    } catch (SocketException e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
                        out.writeObject(ServerResponse.HANDSHAKE);
                        force(out);
                        Object handShake = in.readObject();

                        if (handShake == ClientRequest.HANDSHAKE) {
                            while (id == 0 || containsId(id))
                                id = rand.nextInt();

                            out.writeInt(id);
                            force(out);

                            ctc = connectionInit(id, socket, in, out);
                        }
                        if (ctc != null && (clientLimit < 0 || clientLimit > clients.size())) {
                            clients.put(id, ctc);
                            out.writeObject(ServerResponse.CONNECTED);
                            force(out);
                        } else {
                            if (!socket.isClosed()) {
                                out.writeObject(ServerResponse.REJECT_CONNECTION);
                                force(out);
                            }
                            socket.close();
                            return;
                        }
                    } catch (Throwable t) {
//                        System.out.println(t);
                        try {
                            if (!socket.isClosed())
                                out.writeObject(ServerResponse.ERROR);
                            in.close();
                            out.close();
                        } catch (IOException ignored) {
                        }
                        return;
                    }

                    try {
                        socket.setSoTimeout(0);
                    } catch (SocketException ignored) {
                    }

                    ctc.localStart();
                    for (ServerListener sl : listeners) sl.clientConnected(Server.this, ctc);
                }
            };

            authentication.setDaemon(true);
            authentication.start();
        }
    }

    private class MessageHandling implements Runnable {
        LoginService loginService;
        ExcursiiManagementService excursiiManagementService;
        RezervareManagementService rezervareManagementService;

        public void run() {
            ApplicationContext context = new ClassPathXmlApplicationContext("TurismApp.xml");
            loginService = context.getBean(LoginService.class);
            excursiiManagementService = context.getBean(ExcursiiManagementService.class);
            rezervareManagementService = context.getBean(RezervareManagementService.class);
            while (running())
                try {
                    Message m = messages.take();
                    if (m.msg instanceof Command) {
                        listeners.forEach(sl -> sl.commandReceived(Server.this, getClient(m.id), (Command) m.msg));
                        handleCommand(m);
                    } else {
                        if (m.msg != null) {
                            listeners.forEach(sl -> sl.messageReceived(Server.this, getClient(m.id), m.msg));
                            handleRequest(m);
                        }
                    }
                } catch (InterruptedException ignored) {
                } catch (RuntimeException rte) {
                    rte.printStackTrace();
                    shutDown();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
        }

        private void handleRequest(Message msg) {
            Request request = (Request) msg.msg;
            switch (request.type()) {
                case LOGIN:
                    Agentie agentie;
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> data = (ArrayList<Object>) request.data();
                    String user = (String) data.get(0);
                    String pass = (String) data.get(1);
                    agentie = loginService.LoginUser(user, pass);
                    if (agentie == null || loggedInUsers.contains(agentie)) {
                        send(ServerResponse.ERROR, msg.id);
                        return;
                    }
                    clients.get(msg.id).setUser(agentie);
                    rezervareManagementService.setUserCurent(agentie);
                    loggedInUsers.add(agentie);
                    send(ServerResponse.LOGGED_IN, msg.id);
                    break;
                case GET_EXCURSII_LIST:
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> filters = (ArrayList<Object>) request.data();
                    String obiectiv = (String) filters.get(0);
                    LocalTime start = (LocalTime) filters.get(1);
                    LocalTime end = (LocalTime) filters.get(2);
                    Response response1 = new Response(ServerResponse.GIVE_SEARCH_LIST, excursiiManagementService.searchExcursii(obiectiv, start, end));
                    send(response1, msg.id);
                    break;
                case MAKE_REZERVARE:
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> rezervareParameters = (ArrayList<Object>) request.data();
                    Excursie excursie = (Excursie) rezervareParameters.get(0);
                    String nume = (String) rezervareParameters.get(1);
                    String telefon = (String) rezervareParameters.get(2);
                    int nrBilete = (int) rezervareParameters.get(3);

                    try {
                        rezervareManagementService.setUserCurent(getClient(msg.id).user);
                        rezervareManagementService.rezerva(excursie, nume, telefon, nrBilete);
                        send(ServerResponse.REZERVARE_MADE, msg.id);
                        Response response2 = new Response(ServerResponse.UPDATE_NOTIFICATION, excursiiManagementService.getAllExcursii());
                        if (sendToAll(response2))
                            System.out.println("Notificare trimisa cu succes");
                        else
                            System.out.println("Eroare la trimitere notificare");

                    } catch (Exception e) {
                        send(ServerResponse.ERROR, msg.id);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + request.type());
            }
        }

        private void handleCommand(Message msg) {
            ClientRequest request = (ClientRequest) msg.msg;
            switch (request) {
                case LOGOUT:
                    loggedInUsers.remove(clients.get(msg.id).user);
                    send(ServerResponse.LOGGED_OUT, msg.id);
                    break;
                case GET_EXCURSII_LIST:
                    Response response = new Response(ServerResponse.GIVE_EXCURSII_LIST, excursiiManagementService.getAllExcursii());
                    send(response, msg.id);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + request);
            }
        }
    }

    public class ConnectionToClient {
        private Agentie user;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Socket socket;
        private int clientId;
        private volatile boolean localRunning;
        private volatile boolean localAlive;
        private volatile boolean localStarted = false;

        ConnectionToClient(int id, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            if (socket.isClosed())
                throw new IllegalStateException("Socket for client " + id + " is closed.");
            clientId = id;
            this.socket = socket;
            this.in = in;
            this.out = out;
            this.user = null;
            localAlive = true;
        }

        void setUser(Agentie user) {
            this.user = user;
        }

        private void localStart() {
            if (!localAlive || localRunning)
                return;
            if (localStarted)
                return;
            synchronized (this) {
                if (localStarted)
                    return;
                started = true;
                localRunning = true;
            }

            Thread read = new Thread(new Reading(), "Client: " + clientId + " reading thread");
            read.setDaemon(true);
            read.start();

        }

        public int getClientId() {
            return clientId;
        }

        boolean localRunning() {
            return localRunning;
        }

        boolean send(Serializable msg) {
            if (!localRunning || socket.isClosed()) return false;

            msg = sendInit(msg);
            if (msg == null) return false;

            try {
                out.writeObject(msg);
                force(out);

                if (msg instanceof Command) for (ServerListener sl : listeners) {
                    sl.commandSent(Server.this, this, (Command) msg);
                }
                else for (ServerListener sl : listeners)
                    sl.messageSent(Server.this, this, msg);

                return true;
            } catch (IOException e) {
                localShutDown();
                return false;
            }
        }

        Serializable sendInit(Serializable msg) {
            return msg;
        }

        void localShutDown() {
            if (!localAlive) return;
            synchronized (this) {
                if (!localAlive) return;
                localAlive = false;
            }

            try {
                if (!socket.isClosed()) out.writeObject(ServerResponse.DISCONNECTED);
            } catch (IOException ignored) {
            }
            try {
                in.close();
                out.close();
            } catch (IOException ignored) {
            }

            boolean init = localRunning;

            localRunning = false;
            if (init) listeners.forEach(sl -> sl.clientDisconnected(Server.this, this));

            clients.remove(clientId);
            loggedInUsers.remove(user);
        }

        public int hashCode() {
            return clientId * 31;
        }

        @Override
        protected void finalize() {
            localShutDown();
        }

        private class Reading implements Runnable {
            public void run() {
                try {
                    while (localRunning()) {
                        Object obj;
                        try {
                            obj = in.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            try {
                                if (!socket.isClosed()) {
                                    out.writeObject(ServerResponse.ERROR);
                                    force(out);
                                }
                            } catch (IOException ignored) {
                            }
                            localShutDown();
                            break;
                        }

                        try {
                            messages.put(new Message(obj, clientId));
                        } catch (InterruptedException ignored) {
                        }
                    }
                } catch (RuntimeException rte) {
                    rte.printStackTrace();
                    localShutDown();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
