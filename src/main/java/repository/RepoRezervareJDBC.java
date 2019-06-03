package repository;

import Utils.HibernateUtil;
import model.Excursie;
import model.Rezervare;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RepoRezervareJDBC implements IRepository<String, Rezervare> {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public int size() {
        logger.traceEntry();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaQuery<Excursie> criteria = session.getCriteriaBuilder().createQuery(Excursie.class);
        criteria.from(Excursie.class);
        int size = session.createQuery(criteria).getResultList().size();
        session.getTransaction().commit();
        session.close();

        logger.traceExit();
        return size;
    }

    @Override
    public void save(Rezervare entity) {
        logger.traceEntry("saving rezervare {} ", entity);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public void delete(String string) {
        logger.traceEntry("deleting rezervare with {}", string);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Rezervare entity = findOne(string);
        session.delete(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public void update(String string, Rezervare entity) {
        //TODO: update Rezervare
    }

    @Override
    public Rezervare findOne(String string) {
        logger.traceEntry("finding rezervare with id {} ", string);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Rezervare rezervare = session.get(Rezervare.class, string);
        session.getTransaction().commit();
        session.close();
        if (rezervare != null)
            return rezervare;
        logger.traceExit("No rezervare found with id {}", string);

        return null;
    }

    @Override
    public Iterable<Rezervare> findAll() {
        logger.traceEntry();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaQuery<Rezervare> criteria = session.getCriteriaBuilder().createQuery(Rezervare.class);
        criteria.from(Rezervare.class);
        List<Rezervare> rezervari = session.createQuery(criteria).getResultList();
        session.getTransaction().commit();
        session.close();
        logger.traceExit(rezervari);
        return rezervari;
    }
}
