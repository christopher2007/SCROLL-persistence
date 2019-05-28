# Entscheidungen der Konzeption

- Allgemein  
  Objekte werden, da die Klassen im Vorfeld unbekannt sind, auf Entitäten gemäß dem entworfenen
  Klassendiagramm zerlegt. Mittels Reflection also eigene Serializer programmiert.
  
- INSERT  
  Wenn ein Objekt gespeichert wird, also ein INSERT ausgeführt wird, es danach verändert wird und
  erneut gespeichert wird, so ist die zweite Operation auch ein INSERT und nicht, wie
  zu erwarten, ein UPDATE. In relationalen Datenbanken wird dies automatisch durch die ID gelöst.
  Ist keine ID vorhanden, existiert die Entity noch nicht in der Datenbank und es muss ein INSERT
  ausgeführt werden. Hat die Entity bereits eine ID, so wird diese für ein UPDATE genutzt, um die
  Entity eindeutig zuweisen zu können.  
  Lösung: UUID des zu speichernden Objektes ermitteln und diesen als eindeutigen Identifikator
  nutzen. Bei jedem Speichern also überprüfen, ob diese UUID schon vorhanden ist und wenn ja,
  die Operation als UPDATE betrachten.
  
- SELECT  
  Eigentlich wollte ich hier den Ansatz gehen, dass der ausgeführte SELECT ein Objekt
  der gewünschten Klasse in Scala erzeugt und zurück gibt. Jedoch muss mein Skript wissen,
  von welcher Klasse das Objekt sein soll, das erstellt werden soll. Denn dies wird ja hoch
  variabel sein und kann aktuell noch nicht festgelegt werden.  
  Daher war eine mögliche Lösung, ein Dummy Objekt zu erzeugen, nur um davon die Klasse per
  `getClass()` zu bekommen und das Objekt selbst danach wegzuwerfen:
  ```scala
  val hansSelect = Database.nt.select((new Person("Max")).getClass, "name", "Hans Jürgen").asInstanceOf[Person]
  ```
  Hier muss das Ergebnis auch noch auf die korrekte Klasse gecastet werden und auch in eine
  weitere Instanz der Klasse gespeichert werden. Und da das nicht nur umständlich ist, sondern
  Java auch nicht bekannt für gutes Ressourcenmanagement des Arbeitsspeichers ist, habe ich mich
  für folgenden dritten und auch letzten Ansatz entschieden:
  ```scala
  var hansSelect = new Person("Max")
  Database.nt.select(hansSelect, "name", "Hans Jürgen")
  ```
  Hier wird nun eine Instanz der Klasse erzeugt, welche dann aber mit den abzufragenden
  Werten aus der Datenbank komplett überschrieben wird. Die Person heißt hier zwar kurz `Max`,
  aber da alle Klassenvariablen in der nächsten Zeile überschrieben werden, heißt er danach
  dann wieder richtig und hat auch seine alten bekannten Werte.  
  Der Finale Ansatz sieht wie folgt aus:
  ```scala
  var hansSelectList: util.List[UniversityExample.Person] = Database.nt.select(
          classOf[UniversityExample.Person], "name", "Hans Jürgen").asInstanceOf[util.List[UniversityExample.Person]]
  ```
  Er ist zwar sehr lang, aber dafür clean, nachvollziehbar und hält sich an die Grundlagen, die
  man aus anderen Persistierungsframeworks gewohnt ist. Vor allem muss man kein Pseusoobjekt
  erstellen, ehe man eine Abfrage starten kann. Man muss jedoch die Klasse mit übergeben, die man
  durchsuchen möchte.  
  Ebenso ist dieser Ansatz in der Lage, mehr als ein Objekt zurück zu geben, denn es ist nirgends
  gesagt, dass es nur eine Person mit dem Suchkriterium geben wird.  
  Ob dann etwas gefunden wurde und wenn ja wie viel kann der Entwickler prüfen, in dem er die Länge
  der zurückgegebenen Liste überprüft.
  
- Damit ein Objekt eindeutig in der Datenbank zugeordnet werden kann, muss jedes Objekt in dem
  Projekt der rollenbasierten Welt einen unique identifier besitzen, wie es in relationalen Ansätzen
  der Fall ist. Solch eine eindeutige Zuordnung von (UU)ID kann in Java leider nicht realisiert
  werden, in dem einem bereits initialisieren Objekt nachträglich eine Variable untergeschoben wird,
  ohne die zugrundeliegende Klasse zu modifizieren. Daher stehen folgende möglichen Ansätze im Raum:
  1. Das persistierende Framework hält eine Zuordnung im Arbeitsspeicher, welcher zur Laufzeit
     die UUID Werte aus Java immer mit der Datenbank abgleicht und aktuell hält.  
     Vorteil: weniger Arbeit für den späteren Entwickler  
     Nachteil: schlechte Laufzeit und ggf später Probleme bei Klonvorgängen oder
     Überschreiben von Objekten. Allgemein immer dann, wenn eine neue UUID von
     Java zugewiesen wird. Kaum Kontrolle für den späteren Entwickler.
  1. Jede Klasse in Scroll, die später persistiert werden soll, muss von einer Metaklasse erben, welche
     genau diese Variable für die UUID bereitstellt.  
     Vorteil: gute Laufzeit  
     Nachteil: Der Entwickler muss immer vererben und eventuelle Vererbungsketten entstehen
  1. Man könnte statt Vererbung eine Annotation nutzen, die dieses Attribut für jede zu
     persistierende Klasse bereitstellt.    
     Vorteil: gute Laufzeit  
     Nachteil: Der spätere Entwickler muss stets an die Annotation für alle Klassen denken, die
     er persistieren möchte. Zudem sehr komplex in der Entwicklung, siehe Projekt `Lombok`, den `AST`
     zu manipulieren ist nicht ohne.
     
- Die UUID wird durch vererbung vergeben. Daher müssen NT, RT und CT im SCROLL ab jetzt von der
  entsprechenden Metaklasse erben. (Siehe Codebeispiel)
  
- Wird bei einer Abfrage (=`select`) kein Eintrag gefunden, so wird kein Fehler geworfen, sondern
  das zugrundeliegende Objekt hat `null` in der `uuid_`. So kann man darauf abfragen und auf diesem
  Weg prüfen, ob ein Ergebnis gefunden wurde.
  
- Spielrelationen werden immer mit dem persistieren der RT mit gespeichert. NT & CT können einfach so
  gespeichert werden, aber bei dem speichern eines RT werden gezwungenermaßen auch die aktuell existierenden
  `played by` mit gespeichert.  
  Ebenso muss bei dem Speichern eines RT auch der CT mit persistiert oder bereits persistiert worden sein,
  in dem sich der zu speichernde RT befindet. Auch hier geht es nicht ohne.
  
- Um einen NT, RT und CT auseinander halten zu können, kann auf die Vererbte Klasse geprüft werden.
  Denn da alle drei von unterschiedlichen Metaklassen erben müssen, kann man sehr schön mittels
  ```java
  MetaPersistenceNt.class.isAssignableFrom(someObject.getClass()) // NT
  MetaPersistenceRt.class.isAssignableFrom(someObject.getClass()) // RT
  MetaPersistenceCt.class.isAssignableFrom(someObject.getClass()) // CT
  ```
  prüfen, von welcher Metaklasse das übergebene Objekt erbt und um was für eine Entity es sich
  dadurch handelt.

- Zu Beginn gan es ein klassisches Singelton, welches für NT, CT und RT Methoden beinhaltete:  
  ```java
  Database.getInstance().selectNt(...);
  ```
  Dies wurde ausgetauscht mit einer Statischen Metaklasse, welche erst ein mal in alle drei
  Entitäten Typen teilt, ehe gleichnamige Methoden verwendet werden können.    
  ```java
  Database.nt.select(...);
  ```
  Dies ist nicht nur wesentlich kürzer, sondern auch übersichtlicher und vom späteren Entwickler
  leichter zu nutzen.

- SCROLL baut in SCALA Compartments mit inneren NTs und RTs. Diese werden zur Laufzeit von Java mittels
  anonymer innerer Klassen umgesetzt. Dies stellt akute Probleme da, da anonyme innere Klassen von
  Java Reflection nut bedingt erreicht werden kann. Jedoch sind Reflections die einzige Möglichkeit,
  Instanzen dynamisch zur Laufzeit erzeugen zu lassen.  
  Daher gehen SELECTs bei Compartments initial nicht und mussten komplett einzeln betrachtet und
  entwickelt werden. Immer über die in Java bekannte Superklasse.  
  In diesem Zuge verwende ich **nicht** das standardmäßige `o.getClass()`, sondern eine eigene
  Implementierung, welche man über `BasicClassInformation.getClass(o)` erreicht.

