package scroll.persistence.Util;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@Configuration
//@ComponentScan("scroll.persistence.Model")
//@EnableJpaRepositories(basePackages = "scroll.persistence.Model")
public class SessionFactory {

    private static org.hibernate.SessionFactory instance = null;
    private static int transactionCount = 0; // 0=keine Transaktion offen, sonst ist das die Anzahl der Methoden, die die gleiche Transaktion nutzen

//    @Bean
    public static org.hibernate.SessionFactory getSessionFactory(){
        // if already cached, use this
        if (instance != null)
            return instance;

        // new own HibernateConfig class instance
        HibernateConfig configuration = new HibernateConfig();

        // use `hibernate.cfg.xml` file
        configuration.configure();

        // add Properties manually
//        Properties properties = new Properties();
//        properties.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
//        properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/scroll?serverTimezone=UTC");
//        properties.setProperty("hibernate.connection.username", "scroll");
//        properties.setProperty("hibernate.connection.password", "geheim100");
////        properties.setProperty("hibernate.connection.pool_size", "1");
//        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
//        properties.setProperty("hibernate.show_sql", "true");
//        properties.setProperty("hibernate.hbm2ddl.auto", "create");
//        configuration.setProperties(properties);

        // scan packages
        configuration.scanPackages("scroll.persistence.Model");

        // save singelton pattern and return
        instance = configuration.buildSessionFactory();
        return instance;
    }

    public static Session getNewOrOpenSession(){
        if (instance == null)
            getSessionFactory();

//        org.hibernate.SessionFactory mySession = SessionFactory.getSessionFactory();
//        org.hibernate.Session session = SessionFactory.getSessionFactory().openSession();

        Session session = null;
        if(instance.isOpen())
            session = instance.getCurrentSession();
        else
            session = instance.openSession();
        return session;
    }

    public static void openTransaction(){
        Session session = getNewOrOpenSession();
//        if(!session.getTransaction().isActive())
        if(transactionCount == 0)
            session.beginTransaction();
        transactionCount = transactionCount + 1;
    }

    public static void closeTransaction(){
        Session session = getNewOrOpenSession();
        transactionCount = transactionCount - 1;
//        if(!session.getTransaction().isActive())
        if(transactionCount == 0)
            session.getTransaction().commit();
    }

}
