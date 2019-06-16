# Grundlage

Die Wohlgeformtheitsregeln werden von dieser Persistenzerweiterung **nicht** überprüft.  
Dies wurde bei dem Projektbeginn so festgelegt und auch durchgehend eingehalten.  
Zwar gibt es einige Stellen, an denen ungewollt auf Wohlgeformtheit überprüft wird (z.B. Fremdschlüssel einfügen, ohne
Referenzobjekt gespeichert zu haben, etc.), aber explizit angeführt oder programmiert wurde es nicht.  
Dies kann unter anderem nun dazu führen, dass RTs ohne ihre rigiden Typen persistiert werden können, RTs nicht in CTs
enthalten sein müssen, ...  
Hier ist der Entwickler selbst in der Verantwortung.  
  
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
Das UML Klassendiagramm beinhaltet alle Ansätze für den Projektaufbau. Der letzte ist der
neuste und umgesetzte Ansatz.



## Grundregel

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
