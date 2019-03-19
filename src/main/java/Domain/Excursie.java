package Domain;

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
}
