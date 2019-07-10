# SCROLL persistence

Persistence framework für die Rollen-basierte Programmiersprache [SCROLL](https://github.com/max-leuthaeuser/SCROLL) (_SCala ROLes Language_)
  
Siehe Wiki für API und Dokumentation:

- Wiki von [SCROLL](https://github.com/max-leuthaeuser/SCROLL/wiki) (SCROLL selbst, Grundlegendes, **externes Repository**)
- Wiki von [SCROLL-persistence](https://github.com/christopher2007/SCROLL-persistence/wiki) (Persistierung von SCROLL, **dieses Repository**)



## Beispiele

Dem Projekt liegen zwei Beispiele im Ordner `/src/main/scala/scroll/examples` bei:

- `UniversityExample_small` Ein kleines Beispiel einer Universität. Sehr übersichtlich und gut für Einsteiger.
- `UniversityExample_large` Ein größeres Beispiel einer Universität. Komplexer und gut für Fortgeschrittene.



## Weiterentwicklung

Jeder, der gerne an der Weiterentwicklung mitwirken möchte, möge sich einfach bei mir melden. (Github Issue, E-Mail, ...)



## Stand des Projektes `SCROLL-persistence`

Durchgestrichene Features wurden bewusst nicht entwickelt. Dies kann einen von folgenden zwei Gründen haben, was jedoch dokumentiert
hinter den entsprechenden Einträgen steht:

- Feature nicht möglich aufgrund fehlender Informationsstrukturen des zugrunde liegenden Frameworks `SCROLL`. Mehr dazu in der Wiki unter `Grundlage` -> `Grundregel`
- Feature nicht notwendig, da ...
  - es aus mehreren anderen zusammengesetzt werden kann und daher nicht Atomar und notwendig ist
  - es gegen den Grundgedanken der rollenbasierten Welt arbeitet
  - es sich um Feature-Creep handelt
  - ...



### Feature-Liste

1. [x] Konzeption und Klassenentwurf, Ergebnis: Klassendiagramm
1. [x] Einbetten von Hibernate in das Projekt, inklusive Session Management
1. [x] Implementieren der Klassen für die Abbildung der Persistenz
1. [x] Reflection und Serializer programmieren -> Example Entitäten aufsplitten, Variablen mit Werten ermittelbar

1. [x] Herangehensweise, Ausprobieren von verschiedenen Ansätzen
   1. [x] Primitives Laden von NTs, alle auf ein Mal, Variablen Mapping nur über vorherige Angabe
   1. [x] Komplexes Laden von NTs, konkrete Ergebnisse zurück geben (Abfrage nach Values), Variablen Mapping automatisiert



1. [x] NT

   1. [x] NT INSERT / UPDATE
      1. [x] NT speichern/updaten, RT Playing ignorieren, keine Duplikatserkennung semantisch gleicher Instanzen
      1. [x] NT speichern/updaten, RT Playing ignorieren, Duplikatserkennung semantisch gleicher Instanzen
      1. [ ] ~~NT speichern/updaten, RT Playing mit speichern/updaten~~  
         **In SCROLL erhält man von einem NT ausgehend nicht die gespielten RT.**

   1. [x] NT SELECT
      1. [x] NT laden, RT Spielpartner ignorieren
      1. [ ] ~~NT laden, RT Spielpartner mit laden, ohne Played-By Beziehung in Realanwendung~~  
         **Müsste für jeden zu ladenden RT auch den containing CT kennen, was unbegrenzt viele sein könnten. Zudem soll im rollenbasierten
         Grundgedanken ein NT keine Aktionen auf RTs ausüben können. Auch kommt man in SCROLL von einem NT nicht auf seine playing RTs und
         der Konsistenz wegen hier im persistence Modell auch nicht. Außerdem eine Kombination aus anderen Features.**
      1. [ ] ~~NT laden, RT Spielpartner mit laden, mit Played-By Beziehung in Realanwendung~~  
         **Analog zum vorherigen Punkt**
         
   1. [x] NT DELETE
      1. [x] NT löschen, RT Played-By Beziehungen ignorieren, RT Spielpartner ignorieren
      1. [x] NT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner ignorieren
      1. [ ] ~~NT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner mit löschen~~  
         **Im rollenbasierten Grundgedanken soll ein NT keine Aktionen auf RTs, die er spielt, ausüben können. Außerdem eine Kombination aus anderen Features.**



1. [x] RT

   1. [x] RT INSERT / UPDATE
      1. [x] RT speichern/updaten, NT/CT Player ignorieren, keine Duplikatserkennung semantisch gleicher Instanzen
      1. [x] RT speichern/updaten, NT/CT Player ignorieren, Duplikatserkennung semantisch gleicher Instanzen
      1. [x] RT speichern/updaten, NT/CT Player mit speichern/updaten
      1. [x] RT speichern/updaten, enthaltener CT mit speichern/updaten

   1. [x] RT SELECT
      1. [x] RT laden, NT/CT Spielpartner ignorieren
      1. [x] RT laden, NT/CT Spielpartner mit laden, ohne Played-By Beziehung in Realanwendung
      1. [x] RT laden, NT/CT Spielpartner mit laden, mit Played-By Beziehung in Realanwendung

   1. [x] RT DELETE
      1. [x] RT löschen, Played-By Beziehungen ignorieren, NT/CT Spielpartner ignorieren
      1. [x] RT löschen, Played-By Beziehungen mit löschen, NT/CT Spielpartner ignorieren
      1. [ ] ~~RT löschen, Played-By Beziehungen mit löschen, NT/CT Spielpartner mit löschen~~  
         **Eine Kombination aus anderen Features.**



1. [x] CT

   1. [x] CT INSERT / UPDATE
      1. [x] CT speichern/updaten, RT Playing ignorieren, keine Duplikatserkennung semantisch gleicher Instanzen
      1. [x] CT speichern/updaten, RT Playing ignorieren, Duplikatserkennung semantisch gleicher Instanzen
      1. [ ] ~~CT speichern/updaten, RT Playing mit speichern/updaten~~  
         **In SCROLL erhält man von einem CT ausgehend nicht die gespielten RT**
      1. [x] CT speichern/updaten, enthaltene RT mit speichern/updaten, NT/CT Player dieser RT nicht mit speichern/updaten
      1. [x] CT speichern/updaten, enthaltene RT mit speichern/updaten, NT/CT Player dieser RT mit speichern/updaten

   1. [x] CT SELECT
      1. [x] CT laden, RT Spielpartner ignorieren
      1. [ ] ~~CT laden, RT Spielpartner mit laden, ohne Played-By Beziehung in Realanwendung~~  
         **Müsste für jeden zu ladenden RT auch den containing CT kennen, was unbegrenzt viele sein könnten. Zudem soll im rollenbasierten
         Grundgedanken ein CT keine Aktionen auf RTs ausüben können. Auch kommt man in SCROLL von einem CT nicht auf seine playing RTs und
          der Konsistenz wegen hier im persistence Modell auch nicht. Außerdem eine Kombination aus anderen Features.**
      1. [ ] ~~CT laden, RT Spielpartner mit laden, mit Played-By Beziehung in Realanwendung~~  
         **Analog zum vorherigen Punkt**
      1. [ ] ~~CT laden, enthaltene RTs mit laden, Spielpartner dieser RTs ignorieren~~  
         **SCROLL selbst ermöglicht es nicht, RTs ohne Spielpartner in einen CT einzufügen, daher geht das leider nicht**
      1. [x] CT laden, enthaltene RTs mit laden, Spielpartner dieser RTs mit laden

   1. [x] CT DELETE
      1. [x] CT löschen, RT Played-By Beziehungen ignorieren, RT Spielpartner ignorieren
      1. [x] CT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner ignorieren
      1. [ ] ~~CT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner mit löschen~~  
         **Im rollenbasierten Grundgedanken soll ein CT keine Aktionen auf RTs, die er spielt, ausüben können. Außerdem eine Kombination aus anderen Features.**
      1. [x] CT löschen, enthaltene RT mit all deren Played-By Beziehungen zu anderen löschen, Spielpartner ignorieren
      1. [ ] ~~CT löschen, enthaltene RT mit all deren Played-By Beziehungen zu anderen löschen, Spielpartner mit löschen~~  
         **nicht implementieren, da bei RT DELETE Spielpartner auch nicht mit gelöscht werden und es hier dann inkonsequent wäre.**  



1. [x] Massenoperationen (Annäherung an `alles speichern` und `alles laden`)

   1. [x] von der globalen Ansicht ausgehend
      1. [ ] ~~alle CT speichern/updaten, dabei auch alle enthaltenen RT, alle Spielbeziehungen, ...~~  
         **nicht möglich, da es in SCROLL selbst keine Möglichkeit gibt, alle CTs zu erhalten.**
      1. [x] alle CT laden, dabei auch alle enthaltenen RT, alle Spielbeziehungen, ...
   
   1. [x] von einem CT ausgehend
      1. [x] einen CT übergeben, diesen speichern/updaten, dabei alles persistieren, an das man dabei irgendwie ran kommt (enthaltene CT, deren Spieler, ...)  
         **Alternative, da alle CT speichern/updaten nicht geht, siehe Problem oben.**
      1. [ ] ~~einen CT laden, alles herausziehen, an das man dabei irgendwie ran kommt~~  
         **Redundant, da ohne großen Aufwand für einen Entwickler später selbst möglich zu implementieren, da das normale Laden eines CT schon alles
         nötige bereitstellt. Zudem gefährlich, da nur Teilansichten geladen werden und ein wirkliches `alles laden` (siehe oben) mehr Sinn macht.**

