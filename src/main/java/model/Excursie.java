package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class Excursie implements IHasID<String> {
    private String id;
    private String obiectiv;
    private String firmaTransport;
    private Time oraPlecarii;
    private Double pretul;
    private Integer locuriDisponibile;

    public Excursie(String id, String obiectiv, String firmaTransport, Time oraPlecarii, Double pretul, Integer locuriDisponibile) {
        this.id = id;
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.oraPlecarii = oraPlecarii;
        this.pretul = pretul;
        this.locuriDisponibile = locuriDisponibile;
    }

    public Excursie(ResultSet result) throws SQLException {
        this.id = result.getString("id");
        this.obiectiv = result.getString("obiectiv");
        this.firmaTransport = result.getString("firmaTransport");
        this.oraPlecarii = Time.valueOf(result.getString("oraPlecarii"));
        this.pretul = result.getDouble("pretul");
        this.locuriDisponibile = result.getInt("locuriDisponibile");

    }

    @Override
    public String getId() {
        return id;
    }

    public String getObiectiv() {
        return obiectiv;
    }

    public String getFirmaTransport() {
        return firmaTransport;
    }

    public Time getOraPlecarii() {
        return oraPlecarii;
    }

    public Double getPretul() {
        return pretul;
    }

    public Integer getLocuriDisponibile() {
        return locuriDisponibile;
    }

    public void setLocuriDisponibile(Integer locuriDisponibile) {
        this.locuriDisponibile = locuriDisponibile;
    }
}
