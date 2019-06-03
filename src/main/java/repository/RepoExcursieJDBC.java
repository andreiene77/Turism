package repository;

import Utils.HibernateUtil;
import model.Excursie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RepoExcursieJDBC implements IRepository<String, Excursie> {
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
    public void save(Excursie entity) {
        logger.traceEntry("saving excursie {} ", entity);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public void delete(String string) {
        logger.traceEntry("deleting excursie with {}", string);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Excursie entity = findOne(string);
        session.delete(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public void update(String string, Excursie entity) {
        logger.traceEntry("updating excursie {} ", entity);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        if (entity.getId().equals(string))
            session.update(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public Excursie findOne(String string) {
        logger.traceEntry("finding excursie with id {} ", string);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Excursie excursie = session.get(Excursie.class, string);
        session.getTransaction().commit();
        session.close();
        if (excursie != null)
            return excursie;
        logger.traceExit("No excursie found with id {}", string);

        return null;
    }

    @Override
    public Iterable<Excursie> findAll() {
        logger.traceEntry();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaQuery<Excursie> criteria = session.getCriteriaBuilder().createQuery(Excursie.class);
        criteria.from(Excursie.class);
        List<Excursie> excursii = session.createQuery(criteria).getResultList();
        session.getTransaction().commit();
        session.close();

        logger.traceExit(excursii);
        return excursii;
    }
}
