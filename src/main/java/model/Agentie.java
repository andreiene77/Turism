package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Entity
@Table(name = "Agentii")
public class Agentie implements IHasID<String>, Serializable {
    @Id
    @Column(name = "usrname")
    private String username;

    @Column(name = "pswd")
    private String password;

    public Agentie(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Agentie(ResultSet result) throws SQLException {
        username = result.getString("usrname");
        password = result.getString("pswd");
    }

    public Agentie() {
        username = "";
        password = "";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agentie agentie = (Agentie) o;
        return username.equals(agentie.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
