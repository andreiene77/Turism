package repository;

import model.Agentie;
import model.Excursie;
import model.Rezervare;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoRezervareJDBC implements IRepository<String, Rezervare> {
    private static final Logger logger = LogManager.getLogger();
    private JdbcUtils dbUtils;

    public RepoRezervareJDBC(Properties props) {
        logger.info("Initializing RezervareRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select count(*) as [SIZE] from Rezervari")) {
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return 0;
    }

    @Override
    public void save(Rezervare entity) {
        logger.traceEntry("saving rezervare {} ", entity);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("insert into Rezervari values (?,?,?,?,?,?)")) {
            preStmt.setString(1, entity.getId());
            preStmt.setString(2, entity.getAgentie().getId());
            preStmt.setString(3, entity.getExcursie().getId());
            preStmt.setString(4, entity.getNumeClient());
            preStmt.setString(5, entity.getTelefon());
            preStmt.setInt(6, entity.getNrBilete());
            preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();

    }

    @Override
    public void delete(String string) {
        logger.traceEntry("deleting rezervare with {}", string);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Rezervari where id=?")) {
            preStmt.setString(1, string);
            preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(String string, Rezervare entity) {
        //TODO: update Rezervare
    }

    @Override
    public Rezervare findOne(String string) {
        logger.traceEntry("finding rezervare with id {} ", string);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Rezervari where id=?")) {
            preStmt.setString(1, string);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Rezervare rezervare = rowToRezervare(con, result);
                    logger.traceExit(rezervare);
                    return rezervare;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No rezervare found with id {}", string);

        return null;
    }

    @Override
    public Iterable<Rezervare> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Rezervare> rezervari = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Rezervari")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    rezervari.add(rowToRezervare(con, result));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(rezervari);
        return rezervari;
    }

    private Rezervare rowToRezervare(Connection con, ResultSet result) throws SQLException {
        String id = result.getString("id");
        String ida = result.getString("username_ag");
        String ide = result.getString("ide");
        String numeClient = result.getString("numeClient");
        String telefon = result.getString("telefon");
        Integer nrBilete = result.getInt("nrBilete");

        PreparedStatement preStmt1 = con.prepareStatement("select * from Agentii where usrname=?");
        preStmt1.setString(1, ida);
        ResultSet ag_result = preStmt1.executeQuery();
        Agentie agentie = new Agentie(ag_result);

        PreparedStatement preStmt2 = con.prepareStatement("select * from Excursii where id=?");
        preStmt2.setString(1, ide);
        ResultSet ex_result = preStmt2.executeQuery();
        Excursie excursie = new Excursie(ex_result);

        return new Rezervare(id, agentie, excursie, numeClient, telefon, nrBilete);
    }
}
