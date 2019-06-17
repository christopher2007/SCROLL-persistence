package scroll.persistence;

import scala.collection.JavaConverters;
import scroll.persistence.Inheritance.MetaPersistenceCt;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Inheritance.MetaPersistenceRt;
import scroll.persistence.Util.*;

import java.util.*;

public class _groundOperations {

    // Singelton Pattern
    private static _groundOperations instance;
    private _groundOperations() {}
    // protected, nur für aktuelles Package
    protected static _groundOperations getInstance () {
        if (_groundOperations.instance == null)
            _groundOperations.instance = new _groundOperations();
        return _groundOperations.instance;
    }

    /**
     * Klasse für den Austausch von Informationen im Rekursiven Speichern von "möglichst allem".
     * Beinhaltet die Infos einer Rekursionsstufe, als Rückgabewert für die nächst höhere Ebene.
     */
    private class LevelInformation {
        // Cachen aller Hash Werte, um sich zu merken, was man schon persistiert hat -> schon erledigt (NT, CT, RT)
        Set<Integer> alreadySaved_hashCodes = new HashSet<>();

        // Liste aller CTs, die noch mit allen enthaltenen RTs und deren Spielern durchgegangen werden muss -> noch durchgehen (nur CT)
        Map<Integer, Object> ctToSave = new HashMap<>();

//        private LevelInformation(){}
//
//        private LevelInformation(Set<Integer> alreadySaved_hashCodes, Map<Integer, Object> ctToSave){
//            this.alreadySaved_hashCodes = alreadySaved_hashCodes;
//            this.ctToSave = ctToSave;
//        }
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden CT in der Datenbank.
     * Dabei versucht die Methode an so viele weitere Objekte zu gelangen wie nur irgend möglich. Es wird sich von dem initialen CT entlang
     * gehangelt zu allen enthaltenen RTs, den Spielpartnern dieser RTs und falls davon ein CT dabei ist, wird das gleiche Verfahren dort wiederholt.
     * Diese Methode versucht so tief nur irgend möglich zu gelangen um einem "alles speichern" ähnlich zu sein.
     *
     * Entgegen dem `selectRecursive` ist dies nur eine Annäherung am "alles speichern", da eine wirkliche gesamte Sicht auf die Anwendung in
     * SCROLL selbst leider nicht möglich ist.
     *
     * === Algorithmus mit Problem:
     * Eigentlich wäre der primitive Ansatz, die UUIDs von allen Objekten zwischenzuhalten, die man schon gespeichert hat. Allerdings
     * könnte es folgendes Szenario geben:
     * NT 1 spielt RT 1
     * NT 1 wird dann gespeichert und neu geladen, dadurch entsteht NT 1', was die gleiche UUID hat wie NT 1, aber eine andere Instanz
     * zur Laufzeit darstellt (anderer Arbeitsspeicherplatz).
     * NT 1' spielt nun RT 2
     * Hat man also NT 1 schon gespeichert und sich gemerkt, dass dies gemacht wurde, was nötig ist für Zyklenerkennung, um keine
     * Endlosschleife zu generieren (CT 1 beinhaltet RT 1 und RT 1 spielt CT 1), so würde NT 1' mit RT 2 nie gespeichert werden.
     *
     * === Lösung:
     * HashWerte in Java statt UUIDs zwischenhalten, da diese den Arbeitsspeicherplatz angeben und somit wirklich gleiche Instanzen
     * angeben, unabhängig deren UUIDs.
     * Die von dieser Methode aufgerufenen weiteren Methoden kümmern sich dann um die UUIDs und die Persistenzebene mit Dopplungen, dies
     * kann daher hier ignoriert werden.
     *
     * @param ctObj Der zu speichernde CT, welcher als Ausgangspunkt genutzt wird
     * @throws Exception
     */
    public void createOrUpdateRecursive(MetaPersistenceCt ctObj) throws Exception {
        // Initial gibt es noch keine Informationen für die Rekursion, alles leer erzeugen
        LevelInformation levelInformation = new LevelInformation();

        // Die erste Ebene speichern, Rekursion beginnen
        this.saveCT(levelInformation, ctObj);
    }

    /**
     * Zum Speichern, wenn man nicht weiß, was man genau speichern möchte: NT, CT oder RT.
     * Untersucht das zu speichernde Objekt und übergiebt es an die entsprechende Methode, die dafür zuständig ist:
     * - saveCT
     * - saveNT
     * - saveRT
     *
     * @param levelInformation
     * @param o
     * @param currentCT
     * @return
     * @throws Exception
     */
    private LevelInformation saveUnknown(LevelInformation levelInformation, Object o, MetaPersistenceCt currentCT) throws Exception{
        // Was genau haben wir gefunden?
        if(MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // CT gefunden
            levelInformation = this.saveCT(levelInformation, (MetaPersistenceCt) o);
        else if(MetaPersistenceNt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // NT gefunden
            levelInformation = this.saveNT(levelInformation, (MetaPersistenceNt) o);
        else if(MetaPersistenceRt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // RT gefunden
            levelInformation = this.saveRT(levelInformation, (MetaPersistenceRt) o, currentCT);
        else // nichts, was wir erwarten würden
            throw new Exception("Ein enthaltenes Objekt erbt nicht von der benötigten Persistierungs Struktur.");

        // Rückgabe der veränderten Informationen dieser Ebene
        return levelInformation;
    }

    /**
     * Fügt den übergebenen CT dem Stack hinzu und arbeitet dann den Stack ab. Etwas speziell, da hier die gesamte Rekursion ausgeführt wird, die
     * bis hier gesammelt wurde, auch, wenn es von höheren Ebenen gesammelt wurde.
     * Jeder CT wird gespeichert, alle enthaltenen RTs ermittelt und für diese die `saveUnknown` aufgerufen (sicherheitshalber, falls ein Nicht-RT
     * in dem CT enthalten sein sollte).
     *
     * @param levelInformation
     * @param ct
     * @return
     * @throws Exception
     */
    private LevelInformation saveCT(LevelInformation levelInformation, MetaPersistenceCt ct) throws Exception {
        // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
        if(levelInformation.alreadySaved_hashCodes.contains(ct.hashCode()))
            return levelInformation;

        // Oder wird dieser CT noch in einer anderen Schleife durchgegangen? (Schon registriert und auf dem Arbeitsstack einer anderen Rekursionsstufe)
        if(levelInformation.ctToSave.containsKey(ct.hashCode()))
            return levelInformation;

        // Die Übergabe abspeichern, da wir mit dieser arbeiten sollen
        levelInformation.ctToSave.put(ct.hashCode(), ct);

        // Solange es noch CTs gibt, die noch nicht durchgegangen wurden
        while (levelInformation.ctToSave.size() > 0){
            // Infos zum nächsten CT, der jetzt durchgegangen werden soll
            Map.Entry<Integer, Object> ctCurrent_trash = levelInformation.ctToSave.entrySet().iterator().next();
            int ctCurrent_hashCode = ctCurrent_trash.getKey();
            MetaPersistenceCt ctCurrent_entity = (MetaPersistenceCt) ctCurrent_trash.getValue();

            // CT wird jetzt durchgegangen, kann aber jetzt schon als abgeschlossen angesehen werden
            levelInformation.alreadySaved_hashCodes.add(ctCurrent_hashCode);
            levelInformation.ctToSave.remove(ctCurrent_hashCode);

            // Den CT selbst persistieren ohne enthaltene RTs und deren Spielpartner, da wir in diesen Spielpartnern CTs finden müssen
            Database.ct().createOrUpdate(ctCurrent_entity, false, false);

            // Alle enthaltenen RT mit deren spielenden rigiden Typen ermitteln
            Collection allContainings = JavaConverters.asJavaCollection(ctCurrent_entity.allPlayers());
            for(Object o : allContainings){
                // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
                if(levelInformation.alreadySaved_hashCodes.contains(o.hashCode()))
                    continue;

                // Egal was wir in dem CT gefunden haben, wir wollen es speichern
                levelInformation = this.saveUnknown(levelInformation, o, ctCurrent_entity);
            }
        }

        // Rückgabe der veränderten Informationen dieser Ebene
        return levelInformation;
    }

    /**
     * Speichert stumpf einen NT, mehr nicht.
     *
     * @param levelInformation
     * @param nt
     * @return
     * @throws Exception
     */
    private LevelInformation saveNT(LevelInformation levelInformation, MetaPersistenceNt nt) throws Exception{
        // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
        if(levelInformation.alreadySaved_hashCodes.contains(nt.hashCode()))
            return levelInformation;

        // NT speichern
        levelInformation.alreadySaved_hashCodes.add(nt.hashCode());
        Database.nt().createOrUpdate(nt);

        // Rückgabe der veränderten Informationen dieser Ebene
        return levelInformation;
    }

    /**
     * Speichert einen RT, ermittelt alle Spieler und ruft für jeden Spieler zum Speichern die `saveUnknown` auf.
     *
     * @param levelInformation
     * @param rt
     * @param currentCT
     * @return
     * @throws Exception
     */
    private LevelInformation saveRT(LevelInformation levelInformation, MetaPersistenceRt rt, MetaPersistenceCt currentCT) throws Exception {
        // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
        if(levelInformation.alreadySaved_hashCodes.contains(rt.hashCode()))
            return levelInformation;

        // RT selbst speichern
        levelInformation.alreadySaved_hashCodes.add(rt.hashCode());
        Database.rt().createOrUpdate(rt, false, false); // Spieler selbst durchgehen, da wir CTs suchen

        // alle rigiden Spieler des RT ermitteln
        Collection allPlayers = JavaConverters.asJavaCollection(currentCT.getRolesFromHash(rt.hashCode()));
        for(Object player : allPlayers){
            // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
            if(levelInformation.alreadySaved_hashCodes.contains(player.hashCode()))
                continue;

            // Egal was wir in dem CT gefunden haben, wir wollen es speichern
            levelInformation = this.saveUnknown(levelInformation, player, currentCT);
        }

        // Rückgabe der veränderten Informationen dieser Ebene
        return levelInformation;
    }

    /**
     * Ein Ansatz für "alles laden".
     * Läd alle CTs, die in der Datenbank persistiert sind. Dazu alle enthaltenen RTs und alle Spielpartner dieser RTs.
     *
     * Zwei Dinge werden leider später nicht mehr Zugreifbar sein:
     * - RTs, die von niemandem gespielt werden (können in SCROLL keinem CT zugeordnet werden und werden daher ausgelassen)
     * - NTs oder CTs, die niemanden spielen (Da eine Liste aus CTs zurück gegeben wird, wird man nicht verbundene NTs/CTs nicht wieder finden können,
     *   auch wenn sie eigentlich geladen wurden)
     *
     * @return Liste aller in der Datenbank gefundenen CTs mit den enthaltenen RTs und deren Spielpartnern.
     * @throws Exception
     */
    public List<MetaPersistenceCt> selectRecursive() throws Exception {
        return null;
    }

}
