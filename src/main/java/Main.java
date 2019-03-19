import Domain.Agentie;
import Domain.Excursie;
import Domain.Rezervare;
import Repository.RepoAgentieJDBC;
import Repository.RepoExcursieJDBC;
import Repository.RepoRezervareJDBC;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");

        java.util.Date today = new java.util.Date();
        Properties serverProps = new Properties();
        try {
            serverProps.load(new FileReader("bd.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
            //System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        RepoAgentieJDBC repo_ag = new RepoAgentieJDBC(serverProps);
//        Agentie ag1 = new Agentie("2", "ceva2", "cevap2");
//        repo.save(ag1);
        repo_ag.findAll().forEach(System.out::println);
        repo_ag.delete("2");
        repo_ag.findAll().forEach(System.out::println);

        RepoExcursieJDBC repo_ex = new RepoExcursieJDBC(serverProps);
        Excursie ex1 = new Excursie("1", "ceva_ob", "ceva_firma", new java.sql.Time(today.getTime()), 5.5, 5);
        Excursie ex2 = new Excursie("2", "ceva_ob2", "ceva_firma2", new java.sql.Time(today.getTime()), 5.6, 6);
        repo_ex.save(ex1);
        repo_ex.findAll().forEach(System.out::println);
        repo_ex.delete("2");
        repo_ex.findAll().forEach(System.out::println);

        Agentie ag = repo_ag.findOne("1");
        Excursie ex = repo_ex.findOne("1");

        RepoRezervareJDBC repo_rez = new RepoRezervareJDBC(serverProps);
        Rezervare rez1 = new Rezervare("1", ag, ex, "ceva_nume", "0700000000", 2);
        Rezervare rez2 = new Rezervare("2", ag, ex, "ceva_nume2", "0700000002", 3);
        repo_rez.save(rez1);
        repo_rez.findAll().forEach(System.out::println);
        repo_rez.delete("2");
        repo_rez.findAll().forEach(System.out::println);
    }
}
