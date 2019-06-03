package network;

import java.io.Serializable;

public class Request implements Serializable {
    private ClientRequest type;
    private Object data;

    public Request(ClientRequest type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ClientRequest type() {
        return type;
    }

    public Object data() {
        return data;
    }

    private void type(ClientRequest type) {
        this.type = type;
    }

    private void data(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}