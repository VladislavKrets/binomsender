package online.omnia.binomsender;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by lollipop on 12.07.2017.
 */
public class MySQLDaoImpl implements MySQLDao {
    private static Configuration configuration;
    private static SessionFactory sessionFactory;
    private static MySQLDaoImpl instance;

    static {
        configuration = new Configuration()
                .addAnnotatedClass(PostBackEntity.class)
                .addAnnotatedClass(TrackerEntity.class)
                .configure("/hibernate.cfg.xml");
        Map<String, String> properties = FileWorkingUtils.iniFileReader();
        configuration.setProperty("hibernate.connection.password", properties.get("password"));
        configuration.setProperty("hibernate.connection.username", properties.get("username"));
        configuration.setProperty("hibernate.connection.url", properties.get("url"));
        while (true) {
            try {
                sessionFactory = configuration.buildSessionFactory();
                break;
            } catch (PersistenceException e) {
                try {
                    System.out.println("Can't connect to db");
                    System.out.println("Waiting for 30 seconds");
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    private MySQLDaoImpl() {
    }
    public List<PostBackEntity> getPostbackByClickId(String clickId, String status) {
        Session session = sessionFactory.openSession();
        List<PostBackEntity> postBackEntities = session.createQuery("from PostBackEntity where clickid=:clickId and status=:status and advname=:advName", PostBackEntity.class)
                .setParameter("clickId", clickId)
                .setParameter("status", status)
                .setParameter("advName", "CityAds2")
                .getResultList();
        session.close();
        return postBackEntities;
    }
    @Override
    public List<PostBackEntity> getPostbacks(String status, String advName, int from, int offset) {
        Session session = sessionFactory.openSession();
        List<PostBackEntity> entities = session.createQuery("from PostBackEntity where status=:status and advname=:advname", PostBackEntity.class)
                .setParameter("status", status)
                .setParameter("advname", advName)
                .setFirstResult(from)
                .setMaxResults(offset)
                .getResultList();
        session.close();
        return entities;
    }
    public TrackerEntity getTracker(String prefix) {
        Session session = null;
        TrackerEntity trackerEntity = null;
        while (true) {
            try {
                session = sessionFactory.openSession();
                trackerEntity = session.createQuery("from TrackerEntity where prefix=:prefix", TrackerEntity.class)
                        .setParameter("prefix", prefix)
                        .getSingleResult();
                break;
            } catch (PersistenceException e) {
                try {
                    System.out.println("Can't connect to db");
                    System.out.println("Waiting for 30 seconds");
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        session.close();
        return trackerEntity;
    }
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static synchronized MySQLDaoImpl getInstance() {
        if (instance == null) instance = new MySQLDaoImpl();
        return instance;
    }

}
