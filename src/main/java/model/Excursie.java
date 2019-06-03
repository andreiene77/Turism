package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
//
//@Converter(autoApply = true)
//class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Time>{
//    @Override
//    public Time convertToDatabaseColumn(LocalTime localTime) {
////        DateTimeFormatter parser = DateTimeFormatter.ofPattern("h[:mm]");
//        return (localTime == null ? null : Time.valueOf(localTime));
//    }
//
//    @Override
//    public LocalTime convertToEntityAttribute(Time time) {
////        DateTimeFormatter parser = DateTimeFormatter.ofPattern("h[:mm]");
//        return (time == null ? null : time.toLocalTime());
//    }
//
//}

@Entity
@Table(name = "Excursii")
public class Excursie implements IHasID<String>, Serializable {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "obiectiv")
    private String obiectiv;
    @Column(name = "firmaTransport")
    private String firmaTransport;

    //    @Convert(converter = LocalTimeAttributeConverter.class)
    @Column(name = "oraPlecarii")
    private String oraPlecarii;
    @Column(name = "pretul")
    private Double pretul;
    @Column(name = "locuriDisponibile")
    private Integer locuriDisponibile;

    public Excursie(String id, String obiectiv, String firmaTransport, String oraPlecarii, Double pretul, Integer locuriDisponibile) {
        this.id = id;
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.oraPlecarii = oraPlecarii;
        this.pretul = pretul;
        this.locuriDisponibile = locuriDisponibile;
    }

    public Excursie(String id, String obiectiv, String firmaTransport, LocalTime oraPlecarii, Double pretul, Integer locuriDisponibile) {
        this.id = id;
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.oraPlecarii = oraPlecarii.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.pretul = pretul;
        this.locuriDisponibile = locuriDisponibile;
    }

    public Excursie(ResultSet result) throws SQLException {
        this.id = result.getString("id");
        this.obiectiv = result.getString("obiectiv");
        this.firmaTransport = result.getString("firmaTransport");
        this.oraPlecarii = result.getString("oraPlecarii");
        this.pretul = result.getDouble("pretul");
        this.locuriDisponibile = result.getInt("locuriDisponibile");

    }

    public Excursie() {
        this.id = "";
        this.obiectiv = "";
        this.firmaTransport = "";
        this.oraPlecarii = "";
        this.pretul = 0.0;
        this.locuriDisponibile = 0;
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

    public LocalTime getOraPlecarii() {
        return LocalTime.parse(oraPlecarii, DateTimeFormatter.ofPattern("HH:mm:ss"));
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
