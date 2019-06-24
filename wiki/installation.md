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
- `Eclipse`: verwende das [sbteclipse](https://github.com/typesafehub/sbteclipse) SBT Plugin



## Automatischer Weg

Das Repository enthält beide ineinander verschlungene Projekte:

- `SCROLL`
- `SCROLL-persistence`

Da `SCROLL-persistence` vollständig auf `SCROLL` aufbaut, ist es wichtig, kompatible Versionen zu nutzen.

Der direkte Weg, das Projekt zu installieren, besteht also aus folgenden Schritten:

1. Repository clonen  
  `git clone https://github.com/christopher2007/SCROLL-persistence.git`
1. In einer beliebigen IDE öffnen (siehe Abschnitt `IDE Verwendung` dieser Anleitung)
1. Projekt mittels SBT importieren und Dependencies laden lassen
1. `examples/src/main/scala/scroll/examples/UniversityEcample.scala` öffnen und starten/bauen lassen



## Manueller Weg und SCROLL Update

Möchte man die zugrunde liegende SCROLL Version updaten, um eine neuere Version nutzen zu können, so ist eine
manuelle Zusammenführung beider Projekte nötig. Dies wird im folgenden beschrieben.

### aktuell eingebettetes SCROLL entfernen

Folgende Dateien und Ordner müssen entfernt werden:

```
.
+-- benchmark/
+-- core/
+-- project/
+-- tests/
+-- .gitattributes
+-- .gitlab-ci.yml
+-- .travis.yml
+-- README_from-SCROLL.md
+-- scalastyle-config.xml
```

### neue SCROLL Version integrieren

Folgende Projekte müssen in ein und den selben Ordner kopiert werden:

- zuerst: https://github.com/max-leuthaeuser/SCROLL
- nachfolgend: https://github.com/christopher2007/SCROLL-persistence

Folgende Dateien und Ordner verursachen dabei Konflikte und Probleme:

- `./examples/`  
  Den Ordner vollständig aus dem Projekt `SCROLL-persistence` nehmen.
- `./README.md`  
  Diese Datei aus `SCROLL` umbenennen zu `README_from-SCROLL.md` und neben die `README.md` aus `SCROLL-persistence` legen.
- `./build.sbt`  
  Manuell kombinieren, vorsichtig vorgehen, scharfes Hinsehen (!!!)
- `./LICENSE`  
  Egal, welche man nimmt



## Dependency Weg
  
`SCROLL` als Dependency mittels
```
"com.github.max-leuthaeuser" %% "scroll" % "1.8"
```



## Datenbank (Hibernate)

Die Hibernate Konfiguration ist zu finden unter:  
`/examples/src/main/resources/hibernate.cfg.xml`  

### Datenbank Typ

Standardmäßig wird MySQL verwendet. Unterstützt wird jedoch jeder relationale Datenbank Typ, der auch von
Hibernate unterstützt wird.  
Eine Anleitung für das Verwenden von PostgreSQL statt MySQL befindet sich hier:  
[PostgreSQL statt MySQL](PostgreSQL-statt-MySQL)  
Dieser Wiki Eintrag ist auch ein guter Leitfaden für die notwendigen Änderungen auf Datenbanksysteme außerhalb
von MySQL und PostgreSQL, da hier alles aufgezeigt wird, was es anzupassen gilt.

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
