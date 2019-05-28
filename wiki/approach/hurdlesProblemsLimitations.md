# Hürden, Probleme und Limitierungen

- kein Spring Kontext möglich, daher leider keine Repositories

- Läd man zwei NT aus der Datenbank, die die gleiche Instanz von RT gespielt haben, so erhält man
  zwei NT zurück, von denen jeder einen eigenen RT besitzt. Diese beiden RT haben zwar die gleiche
  UUID und werden von der `Database` Klasse auch als gleich angesehen, sind in Scala bzw. Java
  jedoch streng genommen zwei unterschiedliche Objekte.

- Die Variablen von NT/RT/CT werden nur persistiert, wenn die Datenstrukturen vom Java
  Serializer als Blob serialisiert werden können. Zu komplexe Kindklassen oder ähnliches können
  aktuell leider nicht gespeichert werden.

- Zyklen nur begrenzt persistierbar (Ein RT 1 spielt RT 2, der wiederum RT 1 spielt).  
  Ich gehe von Zyklenfreiheit in diesen Anwendungsfällen aus.

- Klassenvariablen, die auf `private` stehen, sowohl ermitteln als auch setzen

- Da bei einem SELECT ein neues Objekt erstellt wird, dessen Klassenvariablen dann überschrieben
  werden, hat es danach einen anderen HashCode, als das original gespeicherte Objekt. Würde man
  also ein Objekt speichern, es dann laden, verändern und erneut speichern, so wäre dies kein
  Update, sondern ein neuer INSERT.  
  Lösung: HashCode hässlich manuell überschreiben und somit aus dem INSERT in dem Szenario
  ein gewünschtes UPDATE machen ODER keine HashCodes nutzen und UUIDs einführen

- In dem Projekt wird Hibernate verwendet, und Ansätze von Spring, aber leider nicht alles aus der
  Welt von Spring, da in meinem Persistierungs-Ansatz kein Einfluss auf die `main` des späteren
  Programms nehmen kann. Daher läuft die gesamte Anwendung in keinem vollsätndigen Spring
  Kontext und viele Möglichkeiten fallen weg: Kein Autowired, keine Repositories, keine richtigen
  Beans, ...  
  Also leider alles sehr stark limitiert in den Möglichkeiten. So sind manche Lösungen sehr
  schwerfällig und oft nicht unbedingt schön in der Programmierung.  
  Das nervigste: Es geht nur HQL als Abfragesprache, keine Spring oder JPA Repositories

- Wie persistiert man einen RT, der einen anderen RT spielt, der wiederum den ersten RT spielt?  
  Sehr nerviges gefriemel mit vielen Fallunterscheidungen, aber natürlich möglich.

