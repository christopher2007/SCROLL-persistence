# Grundlage



## Rahmenbedingung von SCROLL festgelegt

Ein CT kennt die RTs, die in ihm sind und mindestens eine Spielrelation haben. Hat ein RT keine einzige Played-By Beziehung,
so kennt der CT diesen nicht. Zudem kennt der CT alle Spielpartner von den in ihm enthaltenen RTs.  
Ein RT kennt seine Spielpartner und den CT in dem er sich befindet.  
Ein NT kennt nichts.  
  
Nach diesem Prinzip funktioniert auch das Persistieren von Entitäten in diesem Projekt:

- CT kann seine RTs und deren Spielpartner (CT oder NT) speichern, nicht aber seine RT die niemanden spielen
- RT kann seine Spielpartner (CT oder NT) und auch den CT, in dem er sich befindet, speichern
- NT kann nur sich selbst speichern

Beim Laden jedoch sieht es etwas anders aus, denn sobald Entitäten persistiert wurde weiß das Persistierungsframework
alles. Und mit diesem globalen Wissen kann man nun beispielsweise auch einen NT mit allen Spielpartnern laden.
    
Aber genau das ist der Grund, warum beim Speichern nicht jede Kombination möglich ist und manche Features
in der Aufgabenliste (=`README.md`) durchgestrichen sind. Dies ist schlichtweg nicht möglich, da das zugrunde liegende
Framework `SCROLL` diese Operationen aufgrund fehlender Informationsstruktur nicht liefert. 

`SCROLL` selbst hat keine globale Sicht der Anwendung.  
`SCROLL-persistence` hat diese globale Sicht dank der Möglichkeit, alles aus der Datenbank per Kleneestern zu laden.



## Wohlgeformtheitsregeln

Die Wohlgeformtheitsregeln werden von dieser Persistenzerweiterung **nicht** überprüft.  
Dies wurde bei dem Projektbeginn so festgelegt und auch durchgehend eingehalten.  
Zwar gibt es einige Stellen, an denen ungewollt auf Wohlgeformtheit überprüft wird (z.B. Fremdschlüssel einfügen, ohne
Referenzobjekt gespeichert zu haben, etc.), aber explizit angeführt oder programmiert wurde es nicht.  
Dies kann unter anderem nun dazu führen, dass RTs ohne ihre rigiden Typen persistiert werden können, RTs nicht in CTs
enthalten sein müssen, ...  
Hier ist der Entwickler selbst in der Verantwortung.  



## Herangehen an den Lösungsansatz

Wie persistiert man das rollenbasierte Framework `SCROLL`?

- etwas komplett eigenes ist unklug, da man heutzutage das Rad nicht mehr neu erfinden muss. Es gibt schon so vieles, was man
  verwenden kann. Daher ist eine von Grundauf neu entwickelte Speichermöglichkeit (Flatfiles, ...) nicht ratsam und somit ausgeschlossen.
- Hibernate ist ein starkes Framework für Java, welches auf relationale Tabellen herunter bricht. Zwar befindet sich SCROLL in
  einem rollenbasierten Kontext, was das Mappen der beiden Welten aufeinander jedoch nicht ausschließt.  
  Zudem sind relationale Tabellen schon sehr stark optimiert (Indizes, Buckets, ...) und bieten viele Handwerkzeuge (Joins, ...).  
  Eine vollständige Nutzung von Hibernate ist jedoch nicht möglich, da dies bedeuten würde, Entitäten schon in der Entwicklung als
  solche zu kennzeichnen. Es würde den Entwickler zwingen, Annotationen an Klassen zu setzen, um mit diesen die Tabellen zu spezifizieren,
  Entitäten zu kennzeichnen, IDs festzulegen, Fremdschlüssel, ...  
  Es reißt den Entwickler aus der rollenbasierten Welt zu sehr heraus und er müsste plötzlich in relationales Denken verfallen.
- Die Lösung stellt daher ein Hybrid da:
  Hibernate mit einer relationalen Datenbank nutzen, um vorher festgelegte Grundstrukturen abzubilden.  
  Es gibt für alle wichtigen Bereiche der rollenbasierten Welt angepasste und gut durchdachte Mappings von Entitäten. So gibt es eine
  Entität, die NTs abbilden kann, eine andere für CTs und RTs, welche wiederum mit den NTs verknüpft seien
  können usw. (siehe UML Klassendiagramm)  
  Alles so variabel, dass man später mit richtiger Serialisierung alles ablegen kann. Realisiert wird dies in Java mittels Reflections.
  


## Scala vs Java

`SCROLL` ist in Scala geschrieben.  
Scala ist eine Erweiterung von Java, wie es auch Kotlin ist. Problematisch bei Scala ist jedoch, dass die äußerst starke Typsicherheit
unheimlich einschränkt. So hat man, selbst mit Reflections und Serialisierung innerhalb von Scala einiges an Problemen.  
Da der Ansatz von `SCROLL-persistence` jedoch eben auf diese Grundlagen aufbaut, um Objekte dynamisch zur Laufzeit zerlegen und neu
erstellen zu können, musste auf das zugrundeliegende, unveränderte Java zurück gegriffen werden.  
Da Scala aber nur eine Entwicklungs-Erweiterung für Java ist und am Ende auf normalen Java Code kompiliert, sind die Sprachen und somit
auch beide SCROLL Projekte 100% kompatibel zueinander und können sogar innerhalb einer IDE ohne Probleme vermischt werden. 

