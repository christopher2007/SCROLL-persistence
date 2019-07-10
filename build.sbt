mainClass in (Compile, run) := Some("UniversityExample")

lazy val SCROLLPersistence = (project in file(".")).
  settings(
    name := "SCROLLPersistence",
    version := "0.1",
    scalaVersion := "2.12.6",
    sbtVersion := "1.2.8",
    libraryDependencies ++= Seq(
      // SCROLL selbst
      "com.github.max-leuthaeuser" %% "scroll" % "1.9",

      // Datenbank
      "mysql" % "mysql-connector-java" % "8.0.13", // MySQL
//      "org.postgresql" % "postgresql" % "9.4-1200-jdbc41", // PostgreSQL

      // Hibernate
      "org.hibernate" % "hibernate-core" % "5.3.7.Final",
      "org.hibernate" % "hibernate-entitymanager" % "5.3.7.Final",
      "javax.transaction" % "jta" % "1.1",

      "javax.xml.bind" % "jaxb-api" % "2.3.1",
      "javax.activation" % "activation" % "1.1.1",
      "com.sun.xml.bind" % "jaxb-core" % "2.3.0.1",
      "com.sun.xml.bind" % "jaxb-impl" % "2.3.1",

      // Spring
      "org.springframework.boot" % "spring-boot-starter-web" % "1.0.2.RELEASE",
      "org.springframework.boot" % "spring-boot-starter-data-jpa" % "1.0.2.RELEASE",

      // jUnit Tests
      "org.junit.jupiter" % "junit-jupiter-engine" % "5.4.2",

      // Logging
      //      "org.apache.logging.log4j" % "log4j-core" % "2.11.1",
      //      "org.apache.logging.log4j" % "log4j-api" % "2.11.1",
      //      "org.jboss.logging" % "jboss-logging" % "3.3.2.Final",

      //      //"edu.uci.ics" % "crawler4j" % "4.4.0",
      //      "dom4j" % "dom4j" % "1.6.1",
      //      "commons-logging" % "commons-logging" % "1.2",
      //      "commons-collections" % "commons-collections" % "3.2.2",
      //      "cglib" % "cglib" % "3.2.9",
    )
  )
