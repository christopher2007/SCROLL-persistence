## Installationsanleitung

TODO

## Stand

1. [x] Konzeption und Klassenentwurf, Ergebnis: Klassendiagramm
1. [x] Einbetten von Hibernate in das Projekt, inklusive Session Management
1. [x] Implementieren der Klassen für die Abbildung der Persistenz
1. [x] Reflection und Serializer programmieren -> Example Entitäten aufsplitten, Variablen mit Werten ermittelbar
1. [x] Primitives Speichern von NTs, keine Duplikatserkennung
1. [x] Primitives Laden von NTs, alle auf ein Mal, Variablen Mapping nur über vorherige Angabe
1. [x] Komplexeres Speichern von NTs, Duplikatserkennung
1. [x] Komplexes Laden von NTs, konkrete Ergebnisse zurück geben, Variablen Mapping automatisiert
1. [ ] Primitives Speichern von CTs, ohne Zusammenhang mit NTs
1. [ ] Primitives Laden von CTs, ohne Zusammenhang mit NTs
1. [ ] Komplexes Speichern von CTs, mit Zusammenhang mit NTs
1. [ ] Komplexes Laden von CTs, mit Zusammenhang mit NTs
1. [ ] Primitives Speichern von RTs, ohne Zusammenhang mit NTs
1. [ ] Primitives Laden von RTs, ohne Zusammenhang mit NTs
1. [ ] Komplexes Speichern von RTs, mit Zusammenhang mit NTs
1. [ ] Komplexes Laden von RTs, mit Zusammenhang mit NTs



## Aktuelle Limitierungen

- kein Spring Kontext möglich, daher leider keine Repositories
- 



## Lösungsansatz

Komplett eigen ist blöd, rad neu erfinden ist nicht ratsam.  
100% hibernate nutzen ist unrealistisch, da die Klassen der späteren Anwendung im Vorfeld nicht
bekannt sind und man daher dafür keine Entitäten entwerfen kann. Und den Entwickler zwingen,
Annotationen für Entitäten, ID's, Fremdschlüssel, ... selbst zu schreiben, reißt ihn aus der
rollenbasierten Welt zu sehr heraus. Zudem würde der Entwickler dann in seiner rollenbasierten
Welt plötzlich in relationales Denken verfallen.  
Daher ein Hybrid als Lösung:  
Hibernate mit einer relationalen Datenbank nutzen, um vorher festgelegte Grundstrukturen abzubilden.  
Es gibt für alle wichtigen Bereiche der rollenbasierten Welt angepasste und gut durchdachte Mappings
von Entitäten. So gibt es eine Entität, die NTs abbilden kann, eine andere für CTs und RTs, welche
wiederum mit den NTs verknüpft seien können usw.  
Alles so variabel, dass man später mit richtiger Serialisierung alles ablegen kann.  
-> siehe Klassendiagramm


## Entscheidungen der Konzeption

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
  Lösung: HashCode des zu speichernden Objektes ermitteln und diesen als eindeutigen Identifikator
  nutzen. Bei jedem Speichern also überprüfen, ob dieser HashCode schon vorhanden ist und wenn ja,
  die Operation als UPDATE betrachten.
- SELECT  
  Eigentlich wollte ich hier den Ansatz gehen, dass der ausgeführte SELECT ein Objekt
  der gewünschten Klasse in Scala erzeugt und zurück gibt. Jedoch muss mein Skript wissen,
  von welcher Klasse das Objekt sein soll, das erstellt werden soll. Denn dies wird ja hoch
  variabel sein und kann aktuell noch nicht festgelegt werden.  
  Daher war eine mögliche Lösung, ein Dummy Objekt zu erzeugen, nur um davon die Klasse per
  `getClass()` zu bekommen und das Objekt selbst danach wegzuwerfen:
  ```
  val hansSelect = Database.getInstance().selectNt((new Person("Max")).getClass, "name", "Hans Jürgen").asInstanceOf[Person]
  ```
  Hier muss das Ergebnis auch noch auf die korrekte Klasse gecastet werden und auch in eine
  weitere Instanz der Klasse gespeichert werden. Und da das nicht nur umständlich ist, sondern
  Java auch nicht bekannt für gutes Ressourcenmanagement des Arbeitsspeichers ist, habe ich mich
  für folgenden dritten und auch letzten Ansatz entschieden:
  ```
  var hansSelect = new Person("Max")
  Database.getInstance().selectNt(hansSelect, "name", "Hans Jürgen")
  ```
  Hier wird nun eine Instanz der Klasse erzeugt, welche dann aber mit den abzufragenden
  Werten aus der Datenbank komplett überschrieben wird. Die Person heißt hier zwar kurz `Max`,
  aber da alle Klassenvariablen in der nächsten Zeile überschrieben werden, heißt er danach
  dann wieder richtig und hat auch seine alten bekannten Werte.



## große Hürden und Probleme

- Klassenvariablen, die auf `private` stehen, sowohl ermitteln als auch setzen
- Da bei einem SELECT ein neues Objekt erstellt wird, dessen Klassenvariablen dann überschrieben
  werden, hat es danach einen anderen HashCode, als das original gespeicherte Objekt. Würde man
  also ein Objekt speichern, es dann laden, verändern und erneut speichern, so wäre dies kein
  Update, sondern ein neuer INSERT.  
  Lösung: HashCode hässlich manuell überschreiben und somit aus dem INSERT in dem Szenario
  ein gewünschtes UPDATE machen
- In dem Projekt wird Hibernate verwendet, und Ansätze von Spring, aber leider nicht alles aus der
  Welt von Spring, da in meinem Persistierungs-Ansatz kein Einfluss auf die `main` des späteren
  Programms nehmen kann. Daher läuft die gesamte Anwendung in keinem vollsätndigen Spring
  Kontext und viele Möglichkeiten fallen weg: Kein Autowired, keine Repositories, keine richtigen
  Beans, ...  
  Also leider alles sehr stark limitiert in den Möglichkeiten. So sind manche Lösungen sehr
  schwerfällig und oft nicht unbedingt schön in der Programmierung.  
  Das nervigste: Es geht nur HQL als Abfragesprache, keine Spring oder JPA Repositories
