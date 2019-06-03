package service;

import model.Agentie;
import model.Excursie;
import model.Rezervare;
import repository.IRepository;

public class RezervareManagementService {
    private IRepository<String, Excursie> repoExcursie;
    private IRepository<String, Rezervare> repoRezervare;
    private Agentie userCurent;

    public RezervareManagementService(IRepository<String, Excursie> repoExcursie, IRepository<String, Rezervare> repoRezervare) {
        this.repoExcursie = repoExcursie;
        this.repoRezervare = repoRezervare;
    }

    private Agentie getUserCurent() {
        return userCurent;
    }

    public void setUserCurent(Agentie userCurent) {
        this.userCurent = userCurent;
    }

    public void rezerva(Excursie excursie, String numeClient, String telefon, Integer nrBilete) throws Exception {
        if (excursie.getLocuriDisponibile() >= nrBilete) {
            String id = String.valueOf(excursie.hashCode() + numeClient.hashCode() + nrBilete.hashCode());
            excursie.setLocuriDisponibile(excursie.getLocuriDisponibile() - nrBilete);
            repoExcursie.update(excursie.getId(), excursie);
            Rezervare rez = new Rezervare(id, getUserCurent(), excursie, numeClient, telefon, nrBilete);
            repoRezervare.save(rez);
        } else
            throw new Exception("Locuri insuficiente!");
    }
}
