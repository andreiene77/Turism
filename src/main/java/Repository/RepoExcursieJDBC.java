package Repository;

import Domain.Excursie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
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
            int result = preStmt.executeUpdate();
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
            int result = preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(String string, Excursie entity) {
        //To do
    }

    @Override
    public Excursie findOne(String string) {
        logger.traceEntry("finding excursie with id {} ", string);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Excursii where id=?")) {
            preStmt.setString(1, string);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String id = result.getString("id");
                    String obiectiv = result.getString("obiectiv");
                    String firmaTransport = result.getString("firmaTransport");
                    Time oraPlecarii = java.sql.Time.valueOf(result.getString("oraPlecarii"));
                    Double pretul = result.getDouble("pretul");
                    Integer locuriDisponibile = result.getInt("locuriDisponibile");
                    Excursie excursie = new Excursie(id, obiectiv, firmaTransport, oraPlecarii, pretul, locuriDisponibile);
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
                    String id = result.getString("id");
                    String obiectiv = result.getString("obiectiv");
                    String firmaTransport = result.getString("firmaTransport");
                    Time oraPlecarii = java.sql.Time.valueOf(result.getString("oraPlecarii"));
                    Double pretul = result.getDouble("pretul");
                    Integer locuriDisponibile = result.getInt("locuriDisponibile");
                    Excursie excursie = new Excursie(id, obiectiv, firmaTransport, oraPlecarii, pretul, locuriDisponibile);
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
