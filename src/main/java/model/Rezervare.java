package model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Rezervari")
public class Rezervare implements IHasID<String>, Serializable {
    @Id
    @Column(name = "id")
    private String id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "username_ag")
    private Agentie agentie;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ide")
    private Excursie excursie;
    @Column(name = "numeClient")
    private String numeClient;
    @Column(name = "telefon")
    private String telefon;
    @Column(name = "nrBilete")
    private Integer nrBilete;

    public Rezervare(String id, Agentie agentie, Excursie excursie, String numeClient, String telefon, Integer nrBilete) {
        this.id = id;
        this.agentie = agentie;
        this.excursie = excursie;
        this.numeClient = numeClient;
        this.telefon = telefon;
        this.nrBilete = nrBilete;
    }

    public Rezervare() {
        this.id = "";
        this.agentie = null;
        this.excursie = null;
        this.numeClient = "";
        this.telefon = "";
        this.nrBilete = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    public Agentie getAgentie() {
        return agentie;
    }

    public Excursie getExcursie() {
        return excursie;
    }

    public String getNumeClient() {
        return numeClient;
    }

    public String getTelefon() {
        return telefon;
    }

    public Integer getNrBilete() {
        return nrBilete;
    }
}
