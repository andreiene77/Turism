package network;

import java.io.Serializable;

public class Response implements Serializable {
    private ServerResponse type;
    private Object data;

    public Response(ServerResponse type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ServerResponse type() {
        return type;
    }

    public Object data() {
        return data;
    }

    private void type(ServerResponse type) {
        this.type = type;
    }

    private void data(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
