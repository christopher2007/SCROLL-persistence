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
Das UML Klassendiagramm beinhaltet auch die vier Ansätze für den Projektaufbau. Der letzte ist der
neuste und umgesetzte Ansatz.
