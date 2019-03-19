package Domain;

public class Rezervare implements IHasID<String> {
    private String id;
    private Agentie agentie;
    private Excursie excursie;
    private String numeClient;
    private String telefon;
    private Integer nrBilete;

    public Rezervare(String id, Agentie agentie, Excursie excursie, String numeClient, String telefon, Integer nrBilete) {
        this.id = id;
        this.agentie = agentie;
        this.excursie = excursie;
        this.numeClient = numeClient;
        this.telefon = telefon;
        this.nrBilete = nrBilete;
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
