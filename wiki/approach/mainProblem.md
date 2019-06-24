# Haupt-Problem/-Kritik

Bei dem Laden, Speichern und Modifizieren einer Entität ist diese aus Sicht der `SCROLL-persistence` semantisch das selbe Objekt, wenn es die
gleiche UUID hat. Eine neue UUID wird einer Entität gegeben, wenn diese vom Entwickler erzeugt wird. Lädt man eine Entität aus der Datenbank,
so erhält die geladene Entität die gleiche UUID, wie die originale Entität hatte, welche zum Speichern verwendet wurde.  
So wird sichergestellt, dass, sollte man mehrere Instanzen einer Entität zur Laufzeit haben, diese von der Persistenz alle semantisch als
das selbe betrachtet werden.  
  
Das Problem ist nun aber, dass aus der Sicht von `SCROLL` selbst diese Entitäten unterschiedlich sind.  
So kann man z.B. einen NT erzeugen als `nt1`, ihn dann speichern und als `nt2` laden.  
Die Persistenz behandelt nun beide aufgrund der selben UUID gleich. Egal welches der beiden Objekte man für ein UPDATE nutzt, in der Datenbank
gibt es nur ein Objekt für diese Entität.  
In SCROLL jedoch sind es zwei völlig unterschiedliche Instanzen, die auch unterschiedliche Played-By Beziehungen besitzen können.  
(Zur Laufzeit unterschiedliche Speicherplätze im Arbeitsspeicher und somit unterschiedliche Hash-Codes.)  
  
Dies müsste man jedoch im Kern von SCROLL selbst behandeln. Ein `Garbage Collector` müsste sich darum kümmern, gleiche Objetke zusammenzufassen,
sobald diese erzeugt werden.  
  
Es gibt drei Gründe, warum ich dieses Problem nicht selbst angegangen bin:

1. Leider gibt es keine entsprechende Schnittstelle in SCROLL.

1. Eine solche Erweiterung war nicht Teil meiner Aufgabe.

1. Auch große etablierte Persistenz Lösungen in der relationalen Datenbank Welt befassen dieses Thema nur Oberflächlich. Denn auch Frameworks
   wie Doctrine (PHP), JPA+Hibernate (Java) oder gar Spring (Java) befassen sich mit dieser Problematik nicht, da es nicht zu jedem Zeitpunkt
   in der Anwendung garantiert sein kann, von einer übergeordneten Instanz alle Objekte einzusehen und überwachen zu können. Eine solche
   übergeordnete Instanz müsste entweder alle Objekte erzeugen, oder zumindest nach deren Erzeugen direkt Austausche im Arbeitsspeicher
   übernehmen. Und dies ist schlichtweg nicht möglich.  
   Man kann dem Entwickler in diesem Bereich nicht alles abnehmen und so muss er selbst sicherstellen, bei parallelen Prozessen keine
   Redundanten Dopplungen mit Fehlern entstehen zu lassen.

Dennoch könnte das Augenmerk der zukünftigen Entwicklung hier liegen. In SCROLL wäre zumindest die Überwachung innerhalb von Compartments ohne
all zu großen Aufwand möglich, da Compartments eine genau solche übergeordnete Instanz bieten. Zumindest für deren Inhalte.
