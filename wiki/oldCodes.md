# alte Code-Ansätze

## Session Factory

### Scala Ansatz

Verschiedene Ansätze, alle untergebracht in:

```scala
object HibernateUtil {

    private var sessionFactory: SessionFactory = null

    def buildSessionFactory(classes: List[Class[_]]) = {
        try {
            
            // ----- Ansätze von unten -----

            // return
            //return sessionFactory.buildSessionFactory
            this.sessionFactory = sessionFactory.buildSessionFactory

        } catch {
            case ex: Throwable => {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("Initial SessionFactory creation failed.\r\n" + ex)
                throw new ExceptionInInitializerError(ex)
            }
        }
    }

    def getSessionFactory: SessionFactory = sessionFactory

    def shutdown(): Unit = {
        // Close caches and connection pools
        getSessionFactory.close()
    }

}
```

Reflection in Scala jedoch unzureichend und nicht voll funktionsfähig.  
  
Zudem mit notwendigem Code aus dem Compartment in Scala SCROLL notwendig:  
  
```scala
//val classes: List[Class[_]] = List(classOf[Person], classOf[Student])
val classes: List[Class[_]] = List(classOf[Person])
HibernateUtil.buildSessionFactory(classes)
val sessionF: SessionFactory = HibernateUtil.getSessionFactory
val session: Session = sessionF.openSession()
```

bzw.  
  
```scala
// Klassen ermitteln
//val classes: util.List[Class[_]] = EntityScanner.scanPackages("scroll").result
val classes: List[Class[_]] = List(classOf[Person], classOf[Student])
HibernateUtil.buildSessionFactory(classes)

// short
//val session: Session = HibernateUtil.getSessionFactory.openSession()

// long
val sessionF: SessionFactory = HibernateUtil.getSessionFactory
val session: Session = sessionF.openSession()

session.beginTransaction()
val p = new Person("Heinrich")
p.something = "1"
session.save(p)
session.save(student)
session.getTransaction().commit()
println("FINISHED")

HibernateUtil.shutdown()
```



#### aus `hibernate.cfg.xml` erzeugen

```scala
val sessionFactory = new Configuration().configure().addPackage("scroll.persistence.Model")
return sessionFactory.buildSessionFactory
```

#### Packages automatisch scannen

```scala
//val classes = EntityScanner.scanPackages("my.com.entities", "my.com.other.entities").result
val classes = EntityScanner.scanPackages("scroll.persistence.Models").result
val metadataSources = new MetadataSources
for (annotatedClass <- classes.asScala) {
    metadataSources.addAnnotatedClass(annotatedClass)
    System.out.println(annotatedClass);
}
val sessionFactory = metadataSources.buildMetadata
return sessionFactory.buildSessionFactory
```

#### Teilkonfiguration

```scala
val prop = new Properties()
prop.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/scroll?serverTimezone=UTC")
prop.setProperty("hibernate.connection.username", "scroll")
prop.setProperty("hibernate.connection.password", "geheim")
prop.setProperty("dialect", "org.hibernate.dialect.MySQLDialect")
val sessionFactory = new Configuration()
    .addPackage("scroll.persistence.Model")
    .addProperties(prop)
return sessionFactory.buildSessionFactory
```

#### Vollkonfiguration

```scala
val prop = new Properties()
prop.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/scroll?serverTimezone=UTC")
prop.setProperty("hibernate.connection.username", "scroll")
prop.setProperty("hibernate.connection.password", "geheim")
//prop.setProperty("hibernate.connection.pool_size", "1")
prop.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect")
prop.setProperty("hibernate.show_sql", "true")
prop.setProperty("hibernate.hbm2ddl.auto", "create")
val sessionFactory = new Configuration()
    //.addPackage("scroll.persistence.Model")
    .addProperties(prop)
//val classes: List[Class[_]] = EntityScanner.scanPackages("scroll.persistence.Models").result
for (annotatedClass <- classes) {
    println(annotatedClass.toString)
    sessionFactory.addAnnotatedClass(annotatedClass)
}
return sessionFactory.buildSessionFactory
```

### Java Ansatz

Da die Reflection in Scala unzureichend ist, folgten einige Java Ansätze. Diese befinden sich zusammen mit der finalen Lösung in den Klassen `SessionFactory` und `HibernateConfig`.



## Compartment durch einen RT ermitteln

Wird der Database Klasse ein RT übergeben, so muss anhand dieses Objektes das Compartment ermittelt werden.  
Da aber leider die konkrete RT Klasse im Vorfeld nicht bekannt ist (es köntne ein `student` sein, oder ein `professor`, ...), wird nur ein `Object` hardgecoded übergeben. Und von diesem muss nun das Compartment ermittelt werden:

### Ansatz 1

```java
IPlayer test = (IPlayer) rtObj;
```

geht nicht, da ein RT nicht auf `IPlayer` gecast werden kann (Klasse erbt nicht davon). Konkreter Fehler:

```
Exception in thread "main" java.lang.ClassCastException: class scroll.examples.UniversityExample$University$Student cannot be cast to class scroll.internal.IPlayer (scroll.examples.UniversityExample$University$Student and scroll.internal.IPlayer are in unnamed module of loader 'app')
```

### Ansatz 2

```java
Compartment.Player test = (Compartment.Player) rtObj;
```

Exakt gleiches Problem hierbei.

```
Exception in thread "main" java.lang.ClassCastException: class scroll.examples.UniversityExample$University$Student cannot be cast to class scroll.internal.Compartment$Player (scroll.examples.UniversityExample$University$Student and scroll.internal.Compartment$Player are in unnamed module of loader 'app')
```

### Ansatz 3

```java
Class<?> c = rtObj.getClass();
Field f = c.getDeclaredField("$outer");
f.setAccessible(true);
Compartment compartment = (Compartment) f.get(rtObj);
```

Auf diese Art und Weise kann das äußere Compartment ermittelt werden.
