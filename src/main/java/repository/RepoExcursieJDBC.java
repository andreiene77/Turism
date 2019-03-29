package repository;

import model.Excursie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoExcursieJDBC implements IRepository<String, Excursie> {
    private static final Logger logger = LogManager.getLogger();
    private JdbcUtils dbUtils;

    public RepoExcursieJDBC(Properties props) {
        logger.info("Initializing ExcursieRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select count(*) as [SIZE] from Excursii")) {
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
    public void save(Excursie entity) {
        logger.traceEntry("saving excursie {} ", entity);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("insert into Excursii values (?,?,?,?,?,?)")) {
            preStmt.setString(1, entity.getId());
            preStmt.setString(2, entity.getObiectiv());
            preStmt.setString(3, entity.getFirmaTransport());
            preStmt.setString(4, entity.getOraPlecarii().toString());
            preStmt.setDouble(5, entity.getPretul());
            preStmt.setInt(6, entity.getLocuriDisponibile());
            preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();

    }

    @Override
    public void delete(String string) {
        logger.traceEntry("deleting excursie with {}", string);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Excursii where id=?")) {
            preStmt.setString(1, string);
            preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(String string, Excursie entity) {
        //TODO: update Excursie
    }

    @Override
    public Excursie findOne(String string) {
        logger.traceEntry("finding excursie with id {} ", string);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Excursii where id=?")) {
            preStmt.setString(1, string);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Excursie excursie = new Excursie(result);
                    logger.traceExit(excursie);
                    return excursie;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No excursie found with id {}", string);

        return null;
    }

    @Override
    public Iterable<Excursie> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Excursie> excursii = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Excursii")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    excursii.add(new Excursie(result));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(excursii);
        return excursii;
    }
}
