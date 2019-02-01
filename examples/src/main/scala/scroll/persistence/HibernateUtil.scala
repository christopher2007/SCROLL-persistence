package scroll.persistence

import org.hibernate.SessionFactory
import java.util
import org.hibernate.cfg.Configuration
import com.github.fluent.hibernate.cfg.scanner.EntityScanner
import org.hibernate.boot.MetadataSources
import scala.collection.JavaConverters._
import java.util.Properties
import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.SessionFactory


object HibernateUtil {

//  private var sessionFactory: SessionFactory = null
//
//  def buildSessionFactory(classes: List[Class[_]]) = {
//    try {
//      // Create the SessionFactory from hibernate.cfg.xml
//      val sessionFactory = new Configuration().configure().addPackage("scroll.persistence.Model")
////      sessionFactory.addAnnotatedClass(Variable.class)
//
////      // auto scan packages
//////      val classes = EntityScanner.scanPackages("my.com.entities", "my.com.other.entities").result
////      val classes = EntityScanner.scanPackages("scroll.persistence.Models").result
////      val metadataSources = new MetadataSources
////      for (annotatedClass <- classes.asScala) {
////        metadataSources.addAnnotatedClass(annotatedClass)
////        System.out.println(annotatedClass);
////      }
////      val sessionFactory = metadataSources.buildMetadata
//
////      // drei
////      val prop = new Properties()
////      prop.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
////      prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/scroll?serverTimezone=UTC")
////      prop.setProperty("hibernate.connection.username", "scroll")
////      prop.setProperty("hibernate.connection.password", "geheim100")
////      prop.setProperty("dialect", "org.hibernate.dialect.MySQLDialect")
////
////      val sessionFactory = new Configuration()
////        .addPackage("scroll.persistence.Model")
////        .addProperties(prop)
//////        .addAnnotatedClass(User.class)
//
////      // vier
////      val prop = new Properties()
////      prop.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
////      prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/scroll?serverTimezone=UTC")
////      prop.setProperty("hibernate.connection.username", "scroll")
////      prop.setProperty("hibernate.connection.password", "geheim100")
//////      prop.setProperty("hibernate.connection.pool_size", "1")
////      prop.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect")
////      prop.setProperty("hibernate.show_sql", "true")
////      prop.setProperty("hibernate.hbm2ddl.auto", "create")
////      val sessionFactory = new Configuration()
//////        .addPackage("scroll.persistence.Model")
////        .addProperties(prop)
//////      val classes: List[Class[_]] = EntityScanner.scanPackages("scroll.persistence.Models").result
////      println("suche")
////      for (annotatedClass <- classes) {
////        println(annotatedClass.toString)
////        sessionFactory.addAnnotatedClass(annotatedClass)
////      }
////      println("suche ende")
//
//
//      // return
////      return sessionFactory.buildSessionFactory
//      this.sessionFactory = sessionFactory.buildSessionFactory
//
//    } catch {
//      case ex: Throwable => {
//        // Make sure you log the exception, as it might be swallowed
//        System.err.println("Initial SessionFactory creation failed.\r\n" + ex)
//        throw new ExceptionInInitializerError(ex)
//      }
//    }
//  }
//
//  def getSessionFactory: SessionFactory = sessionFactory
//
//  def shutdown(): Unit = {
//    // Close caches and connection pools
//    getSessionFactory.close()
//  }

}
