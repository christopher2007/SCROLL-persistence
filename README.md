# SCROLL persistence

Eine Implementierung für Persistierung des Projektes `SCROLL` (_SCala ROLes Language_):  
https://github.com/max-leuthaeuser/SCROLL  
  
Siehe Wiki für API und Dokumentation:

- für [SCROLL](https://github.com/max-leuthaeuser/SCROLL/wiki) (SCROLL selbst, Grundlegendes, externes Repository)
- für [SCROLL-persistence](https://github.com/christopher2007/SCROLL-persistence/wiki) (nur die Persistierung von SCROLL, dieses Repository)



## Stand des Projektes

Durchgestrichene Features sind aufgrund fehlender Informationsstrukturen des zugrunde liegenden Frameworks `SCROLL` nicht
möglich. Mehr dazu in der Wiki unter `Grundlage` -> `Grundregel`

1. [x] Konzeption und Klassenentwurf, Ergebnis: Klassendiagramm
1. [x] Einbetten von Hibernate in das Projekt, inklusive Session Management
1. [x] Implementieren der Klassen für die Abbildung der Persistenz
1. [x] Reflection und Serializer programmieren -> Example Entitäten aufsplitten, Variablen mit Werten ermittelbar

1. [x] Herangehensweise, Ausprobieren von verschiedenen Ansätzen
   1. [x] Primitives Laden von NTs, alle auf ein Mal, Variablen Mapping nur über vorherige Angabe
   1. [x] Komplexes Laden von NTs, konkrete Ergebnisse zurück geben (Abfrage nach Values), Variablen Mapping automatisiert



1. [ ] NT

   1. [x] NT INSERT / UPDATE
      1. [x] NT speichern/updaten, RT Playing ignorieren, keine Duplikatserkennung semantisch gleicher Instanzen
      1. [x] NT speichern/updaten, RT Playing ignorieren, Duplikatserkennung semantisch gleicher Instanzen
      1. [ ] ~~NT speichern/updaten, RT Playing mit speichern/updaten~~  
         **keine Informationen in SCROLL, geht also nicht, oder irre ich mich?**

   1. [ ] NT SELECT
      1. [x] NT laden, RT Spielpartner ignorieren
      1. [ ] NT laden, RT Spielpartner mit laden, ohne Played-By Beziehung in Realanwendung  
         **lieber weg, da beides speichern nicht geht und es so inkonsequent wäre?**
      1. [ ] NT laden, RT Spielpartner mit laden, mit Played-By Beziehung in Realanwendung  
         **lieber weg, da beides speichern nicht geht und es so inkonsequent wäre?**

   1. [ ] NT DELETE
      1. [x] NT löschen, RT Played-By Beziehungen ignorieren, RT Spielpartner ignorieren
      1. [x] NT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner ignorieren
      1. [ ] NT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner mit löschen  
         **lieber weg, da beides speichern nicht geht und es so inkonsequent wäre? Oder kann ein RT von mehreren NT/CT gespielt werden? @param alsoDeletePlayingRTs `true`=löscht alle RTs, die der zu löschende NT spielt; `false`=nicht**



1. [ ] RT

   1. [x] RT INSERT / UPDATE
      1. [x] RT speichern/updaten, NT/CT Player ignorieren, keine Duplikatserkennung semantisch gleicher Instanzen
      1. [x] RT speichern/updaten, NT/CT Player ignorieren, Duplikatserkennung semantisch gleicher Instanzen
      1. [x] RT speichern/updaten, NT/CT Player mit speichern/updaten
      1. [x] RT speichern/updaten, enthaltener CT mit speichern/updaten

   1. [x] RT SELECT
      1. [x] RT laden, NT/CT Spielpartner ignorieren
      1. [x] RT laden, NT/CT Spielpartner mit laden, ohne Played-By Beziehung in Realanwendung
      1. [x] RT laden, NT/CT Spielpartner mit laden, mit Played-By Beziehung in Realanwendung

   1. [ ] RT DELETE
      1. [x] RT löschen, Played-By Beziehungen ignorieren, NT/CT Spielpartner ignorieren
      1. [x] RT löschen, Played-By Beziehungen mit löschen, NT/CT Spielpartner ignorieren
      1. [ ] RT löschen, Played-By Beziehungen mit löschen, NT/CT Spielpartner mit löschen  
         **lieber nicht, oder? immerhin könnte der NT/CT noch anderes spielen oder beinhalten**



1. [ ] CT

   1. [x] CT INSERT / UPDATE
      1. [x] CT speichern/updaten, RT Playing ignorieren, keine Duplikatserkennung semantisch gleicher Instanzen
      1. [x] CT speichern/updaten, RT Playing ignorieren, Duplikatserkennung semantisch gleicher Instanzen
      1. [ ] ~~CT speichern/updaten, RT Playing mit speichern/updaten~~  
         **gleich zu NT INSERT / UPDATE mit RT Playing speichern**
      1. [x] CT speichern/updaten, enthaltene RT mit speichern/updaten, NT/CT Player dieser RT nicht mit speichern/updaten
      1. [x] CT speichern/updaten, enthaltene RT mit speichern/updaten, NT/CT Player dieser RT mit speichern/updaten

   1. [ ] CT SELECT
      1. [x] CT laden, RT Spielpartner ignorieren
      1. [ ] CT laden, RT Spielpartner mit laden, ohne Played-By Beziehung in Realanwendung
      1. [ ] CT laden, RT Spielpartner mit laden, mit Played-By Beziehung in Realanwendung
      1. [ ] CT laden, enthaltene RT mit laden

   1. [ ] CT DELETE
      1. [x] CT löschen, RT Played-By Beziehungen ignorieren, RT Spielpartner ignorieren
      1. [x] CT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner ignorieren
      1. [ ] CT löschen, RT Played-By Beziehungen mit löschen, RT Spielpartner mit löschen  
         **gleich zu NT DELETE mit RT Playing mit löschen**
      1. [ ] CT löschen, enthaltene RT mit alld eren Played-By Beziehungen zu anderen löschen  
         **müsste dann auch alle Played by droppen, finde ich nicht so gut, sollte der entwickler lieber wirklich wollen und im notfall ggf selbst schreibn**
  
   1. [ ] CT Großflächige Operationen
      1. [x] CT speichern/updaten, dabei alles persistieren, an das man dabei irgendwie ran kommt (möglichst nahe am "alles speichern")
      1. [ ] CT laden, alles herausziehen, an das man dabei irgendwie ran kommt (möglichst nahe am "alles laden")


