# RoDEL - Eine Evolutionssprache für Rollenbasierte Datenbanken

Bei `RoDEL` handelt es sich um meine Bachelorarbeit aus dem Jahr 2017.  
Im Rahmen dieser entwickelte ich eine komplett neue Datenbanksprache für rollenbasierte Datenbanken und nannte diese `RoDEL`.  
Da RoDEL und SCROLL sich sehr ähnlich sind, SCROLL dabei aber eine Implementierung des Rollenansatzes darstellt und RoDEL eine
Beschreibungssprache ist, können die Beispiele hier verknüpft werden, um ein besseres Verständnis zu liefern.  
  
Ich verweise an dieser Stelle auf meine Arbeit

```
RoDEL - Eine Evolutionssprache für Rollenbasierte Datenbanken
```

und lege auch den in diesem Zuge entstandenen Prototypen sehr ans Herz.  
Mit ihm ist es möglich, einfach und schnell CROM Modelle grafisch auf Typebene zu realisieren.

Die Hauptbeispiele, welches in `/uml/example project/` auch als UML Diagramme vorliegt, können in RoDEL wie im folgenden
aufgezeigt abgebildet werden.    
(einfach copy & paste in den Prototyp einfügen, um schnellere Änderungen vollziehen zu können)

In diesen Beispielen wurde die Mächtigkeit von RoDEL nicht voll ausgenutzt, da einiges (Variablen auf UNIQUE setzen,
RST Verbindungen erstellen, ...) nicht in SCROLL abgebildet werden kann und somit hier nicht nötig ist.

Zudem besitzt RoDEL ein großen Konzeptionellen Unterschied zu SCROLL:  
Namen von RTs müssen UNIQUE sein. Dies ist in meiner Arbeit `RoDEL - Eine Evolutionssprache für Rollenbasierte Datenbanken`
detailliert begründet aufgeführt.  
SCROLL jedoch hat diese Einschränkung nicht und so muss auf diesen Unterschied geachtet werden, wenn man
z.B. den RoDEL Prototypen für eine grafische Darstellung der SCROLL Beispiele nutzt.

Dennoch ist mit den nachfolgenden Codes ein schnelleres Verständniss beider Universitäts-Beispiele dank grafischer
Unterstützung durch RoDEL möglich.



## Universität Beispiel klein

```
// Datenbank leeren
prune db;
// NT Person erstellen
add concept nt.Person;
add variable nt.Person.name String;
add variable nt.Person.birthday Date;
// CT University erstellen
add concept ct.University;
add variable ct.University.name String;
add variable ct.University.country String;
add variable ct.University.city String;
// RT Student erstellen
add concept rt.Student;
add variable rt.Student.matNr int;
move concept rt.Student to ct.University;
// RT Professor erstellen
add concept rt.Professor;
add variable rt.Professor.graduation String;
move concept rt.Professor to ct.University;
// Played By Beziehungen erstellen
add played by nt.Person playing rt.Student;
add played by nt.Person playing rt.Professor;
```



## Universität Beispiel groß

```
// Datenbank leeren
prune db;
// NT Person erstellen
add concept nt.Person;
add variable nt.Person.name String;
add variable nt.Person.birthday Date;
// NT Animal erstellen
add concept nt.Animal;
add variable nt.Animal.name String;
add variable nt.Animal.birthday Date;
add variable nt.Animal.race Date;
// NT Furniture erstellen
add concept nt.Furniture;
add variable nt.Furniture.color String;
add variable nt.Furniture.material String;
// CT University erstellen
add concept ct.University;
add variable ct.University.name String;
add variable ct.University.country String;
add variable ct.University.city String;
// CT Room erstellen
add concept ct.Room;
add variable ct.Room.size float;
add variable ct.Room.windowCount int;
// RT Student erstellen
add concept rt.Student;
add variable rt.Student.matNr int;
move concept rt.Student to ct.University;
// RT Professor erstellen
add concept rt.Professor;
add variable rt.Professor.graduation String;
move concept rt.Professor to ct.University;
// RT Mailbox erstellen
add concept rt.Mailbox;
add variable rt.Mailbox.newMessages boolean;
move concept rt.Mailbox to ct.University;
// RT Classroom erstellen
add concept rt.Classroom;
add variable rt.Classroom.seatCount int;
move concept rt.Classroom to ct.University;
// RT Chair erstellen
add concept rt.Chair;
add variable rt.Chair.footCount int;
move concept rt.Chair to ct.Room;
// RT Table erstellen
add concept rt.Table;
add variable rt.Table.footCount int;
move concept rt.Table to ct.Room;
// Played By Beziehungen erstellen
add played by nt.Person playing rt.Student;
add played by nt.Person playing rt.Professor;
add played by nt.Animal playing rt.Student;
add played by nt.Furniture playing rt.Mailbox;
add played by nt.Furniture playing rt.Chair;
add played by nt.Furniture playing rt.Table;
add played by ct.Room playing rt.Classroom;
```
