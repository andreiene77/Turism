package service;

import model.Agentie;
import repository.IRepository;

public class LoginService {
    private IRepository<String, Agentie> repoAgentie;

    public LoginService(IRepository<String, Agentie> repoAgentie) {
        this.repoAgentie = repoAgentie;
    }

    public Agentie LoginUser(String username, String pass) {
        if (VerifyCredentials(username, pass))
            return repoAgentie.findOne(username);
        return null;
    }

    private Boolean VerifyCredentials(String username, String pass) {
        Agentie agentie = repoAgentie.findOne(username);
        if (agentie == null)
            return false;
        return agentie.getPassword().equals(pass);
    }
}
