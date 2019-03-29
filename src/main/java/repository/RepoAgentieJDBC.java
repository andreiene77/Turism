package repository;

import model.Agentie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoAgentieJDBC implements IRepository<String, Agentie> {
    private static final Logger logger = LogManager.getLogger();
    private JdbcUtils dbUtils;

    public RepoAgentieJDBC(Properties props) {
        logger.info("Initializing AgentieRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select count(*) as [SIZE] from Agentii")) {
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
    public void save(Agentie entity) {
        logger.traceEntry("saving agentie {} ", entity);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("insert into Agentii values (?,?)")) {
            preStmt.setString(1, entity.getId());
            preStmt.setString(2, entity.getPassword());
            preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();

    }

    @Override
    public void delete(String string) {
        logger.traceEntry("deleting agentie with {}", string);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("delete from Agentii where usrname=?")) {
            preStmt.setString(1, string);
            preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(String string, Agentie entity) {
        //TODO: Update Agentie
    }

    @Override
    public Agentie findOne(String string) {
        logger.traceEntry("finding agentie with username {} ", string);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Agentii where usrname=?")) {
            preStmt.setString(1, string);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Agentie agentie = new Agentie(result);
                    logger.traceExit(agentie);
                    return agentie;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No agentie found with username {}", string);

        return null;
    }

    @Override
    public Iterable<Agentie> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        List<Agentie> agentii = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Agentii")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    agentii.add(new Agentie(result));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(agentii);
        return agentii;
    }
}
