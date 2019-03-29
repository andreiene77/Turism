package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Agentie implements IHasID<String> {
    private String username;
    private String password;

    public Agentie(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Agentie(ResultSet result) throws SQLException {
        this.username = result.getString("usrname");
        this.password = result.getString("pswd");
    }

    @Override
    public String getId() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Agentie{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
