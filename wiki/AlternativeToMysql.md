# Alternativen statt MySQL

Standardmäßig wird MySQL als Datanbank verwendet. Unterstützt wird jedoch jeder relationale Datenbank Typ, der auch von
Hibernate unterstützt wird.  

Im folgenden wird gezeigt, wie man die eingebettete MySQL Datenbank mit einer Alternative austauschen kann.  
Beispielhaft wird dies an dem Einsatz von PostgreSQL gezeigt.



## PostgreSQL statt MySQL (Beispielhafter Leitfaden)

In der `hibernate.cfg.xml` folgendes ändern:

- Den Wert von `hibernate.connection.driver_class` ändern auf `org.postgresql.Driver`
- Den Wert von `hibernate.dialect` ändern auf `org.hibernate.dialect.PostgreSQLDialect`
- In dem Wert von `hibernate.connection.url` statt des Präfixes `jdbc:mysql` den Präfix `jdbc:postgresql` nutzen
- In dem Wert von `hibernate.connection.url` statt des MySQL Ports `3306` den Port der eigenen PostgreSQL Instanz nutzen, per Default ist dies `5432`

In der `build.sbt` folgendes ändern:

- Die Zeile mit dem Präfix `"mysql" % "mysql-connector-java"` entfernen
- An diese Stelle nun folgendes einfügen: `"org.postgresql" % "postgresql" % "9.4-1200-jdbc41",`  
  Die Version bitte prüfen und ggf eine neuere nutzen.

Zudem verwendet MySQL andere Typ Mappings wie PostgreSQL.  
Grundlegende Informationen dazu: https://www.convert-in.com/mysql-to-postgres-types-mapping.htm  
Konkret bedeutet dies folgende notwendige Änderungen in Entity-Klassen:

- In `Entity.java` von der Variable `public UUID uuid_;` die `columnDefinition` statt `BINARY(16)` ändern zu `BYTEA`
