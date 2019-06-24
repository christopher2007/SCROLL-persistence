# Installation (Anleitung)



## Voraussetzungen

- benötigt: Java SE Development Kit
  - Version 9 oder höher
  - entwickelt und getestet mit Version `11.0.1`
  - globale Installation auf dem System
- [Scala Build Tool](https://www.scala-sbt.org/) (SBT)
  - Version 1 oder höher
  - entwickelt und getestet mit Version `1.2.8`
  - globale Installation auf dem System nicht notwendig, über die IDE auch möglich
  - Version von SBT im Projekt einzustellen unter: `project/build.properties` 



## IDE Verwendung

- `IntelliJ`: verwende den mitgelieferten SBT Importer  
  1. Entweder auf `New` >> `Project from Existing Sources` oder im Dash-Screen auf `Import Project` klicken
  1. den Ordner des Projektes anpeilen und bestätigen
  1. unter `Import project from external model` dann `sbt` auswählen und bestätigen
  1. die korrekte Java Version wählen und die restlichen Einstellungen nach eigenem Ermessen wählen. Bestätigen.
  1. Hat man alles richtig eingestellt, läuft der Import ohne Fehler durch
- `Eclipse`: verwende das [sbteclipse](https://github.com/typesafehub/sbteclipse) SBT Plugin



## Automatischer Weg

Dieses Repository enthält das `SCROLL-persistence` Projekt, welches sich mittels Dependencies auf das Projekt
`SCROLL` stützt.

Der direkte Weg, das Projekt zu installieren, besteht also aus folgenden Schritten:

1. Repository clonen  
  `git clone https://github.com/christopher2007/SCROLL-persistence.git`
1. In einer beliebigen IDE öffnen (siehe Abschnitt `IDE Verwendung` dieser Anleitung)
1. Projekt mittels SBT importieren und Dependencies laden lassen
1. `corePersistence/src/main/scala/scroll/examples/UniversityEcample.scala` öffnen und starten/bauen lassen



## Datenbank (Hibernate)

Die Hibernate Konfiguration ist zu finden unter:  
`/examples/src/main/resources/hibernate.cfg.xml`  

### Datenbank Typ

Standardmäßig wird MySQL verwendet. Unterstützt wird jedoch jeder relationale Datenbank Typ, der auch von
Hibernate unterstützt wird.  
Eine Anleitung für das Verwenden von Alternativen statt MySQL befindet sich hier:  
[Alternativen statt MySQL](Alternativen-statt-MySQL)  
Hier wird das Einbetten einer anderen relationalen Datenbank beispielhaft anhand von PostgreSQL gezeigt.

### Datenbank Verbindungsdaten

In der Config dringend die Datenbank Verbindungsdaten des aktuellen Systems eintragen:  
Host, Username, Passwort, ...

### Dev vs Prod

#### hibernate.show_sql

In der Config findet man auch Einstellungen für den Debug von SQL Statements:

```xml
<property name="hibernate.show_sql">VALUE</property>
```

Als `VALUE` kann man hier `true` oder `false` eintragen.  
Auf `true` werden in der Console/Terminal alle SQL Statements von Hibernate ausgegeben. Dies ist in der Entwicklung
oder allgemein bei Fehlersuchen hilfreich.  
Empfehlung:  
  
- Im Entwicklungs Modus (**Dev**) auf `true`
- Im Produktivbetrieb (**Prod**) auf `false`

#### hibernate.show_sql

In der Config findet man auch die Einstellung für das Verhalten bei nicht gleichem Datenbank-Schema zum letzten Start:

```xml
<property name="hibernate.hbm2ddl.auto">create</property>
```

Als `VALUE` kann man hier folgende Werte nutzen:

- `create` = Gesamte Datenbank bei jedem Start der Anwendung komplett löschen und neu erstellen (die Daten des letzten Laufes
  der Anwendung werden komplett verloren gehen)
- `update` = Datenbank bei jedem Start der Anwendung überprüfen und ggf updaten wenn Änderungen im Schema erkannt wurden
  (man behält seine Daten vom letzten Lauf der Anwendung)
- `validate` = Datenbank bei jedem Start der Anwendung überprüfen. Sollte sich das Schema zum letzten Start geändert haben,
  so wird ein Fehler ausgegeben, aber nichts automatisch gemacht (man behält seine Daten vom letzten Lauf der Anwendung)

Empfehlung:  
  
- Im Entwicklungs Modus (**Dev**) auf `create`
- Im Produktivbetrieb (**Prod**) auf `validate` und Updates gezielt und geplant durchführen
