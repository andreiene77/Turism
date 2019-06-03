package Utils;

import model.Agentie;
import model.Excursie;
import model.Rezervare;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Agentie.class);
        configuration.addAnnotatedClass(Excursie.class);
        configuration.addAnnotatedClass(Rezervare.class);
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());

    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
