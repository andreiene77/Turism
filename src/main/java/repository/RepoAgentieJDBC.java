package repository;

import Utils.HibernateUtil;
import model.Agentie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RepoAgentieJDBC implements IRepository<String, Agentie> {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public int size() {
        logger.traceEntry();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaQuery<Agentie> criteria = session.getCriteriaBuilder().createQuery(Agentie.class);
        criteria.from(Agentie.class);
        int size = session.createQuery(criteria).getResultList().size();
        session.getTransaction().commit();
        session.close();

        logger.traceExit();
        return size;
    }

    @Override
    public void save(Agentie entity) {
        logger.traceEntry("saving agentie {} ", entity);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public void delete(String string) {
        logger.traceEntry("deleting agentie with {}", string);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Agentie entity = findOne(string);
        session.delete(entity);
        session.getTransaction().commit();
        session.close();
        logger.traceExit();
    }

    @Override
    public void update(String string, Agentie entity) {
        //TODO: Update Agentie
    }

    @Override
    public Agentie findOne(String string) {
        logger.traceEntry("finding agentie with username {} ", string);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Agentie agentie = session.get(Agentie.class, string);
        session.getTransaction().commit();
        session.close();
        if (agentie != null)
            return agentie;
        logger.traceExit("No agentie found with username {}", string);

        return null;
    }

    @Override
    public Iterable<Agentie> findAll() {
        logger.traceEntry();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaQuery<Agentie> criteria = session.getCriteriaBuilder().createQuery(Agentie.class);
        criteria.from(Agentie.class);
        List<Agentie> agentii = session.createQuery(criteria).getResultList();
        session.getTransaction().commit();
        session.close();

        logger.traceExit(agentii);
        return agentii;
    }
}
