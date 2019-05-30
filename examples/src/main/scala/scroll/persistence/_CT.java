package scroll.persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import scroll.persistence.Inheritance.MetaPersistenceCt;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Inheritance.MetaPersistenceRt;
import scroll.persistence.Model.CT;
import scroll.persistence.Model.Entity;
import scroll.persistence.Model.RT;
import scroll.persistence.Model.Variable;
import scroll.persistence.Util.*;
import java.lang.reflect.Field;
import java.util.*;
import scala.collection.JavaConverters;

public class _CT {

    // Singelton Pattern
    private static _CT instance;
    private _CT() {}
    // protected, nur für aktuelles Package
    protected static _CT getInstance () {
        if (_CT.instance == null)
            _CT.instance = new _CT();
        return _CT.instance;
    }

    /**
     * Ruft die gleichnamige Methode der Klasse auf und setzt den Übergabeparameter `createOrUpdateAllContainingRT` und auch
     * `createOrUpdateAllPlayersFromContainingRT` auf `false`.
     *
     * @param ctObj Der zu speichernde CT
     * @throws Exception
     */
    public void createOrUpdate(MetaPersistenceCt ctObj) throws Exception {
        this.createOrUpdate(ctObj, false, false);
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden CT in der Datenbank.
     *
     * Steht `createOrUpdateAllContainingRT` auf `true`, so werden für alle RTs in dem übergebenen CT ein `createOrUpdate` ausgeführt. Somit werden
     * alle beinhalteten RTs gespeichert, wenn nicht bereits in der Datenbank vorhanden.
     * Jedoch werden nur RTs beachtet, die von mindestens einem rigiden Typen gespielt werden. Alle nicht gespielten werden ignoriert.
     *
     * Steht `createOrUpdateAllPlayersFromContainingRT` auf `true`, so wird für alle Player von den in dem CT enthaltenen RTs ein `createOrUpdate`
     * ausgeführt.
     *
     * `createOrUpdateAllContainingRT` und `createOrUpdateAllPlayersFromContainingRT` sind unabhängig voneinander und können in beliebigen
     * Kombinationen gesetzt werden (kann aber natürlich zu geworfenen Exceptions führen, wenn man die Playing Relationen speichern möchte
     * ohne dass die Spieler bereits in der Datenbank existieren).
     *
     * Wird beides nur eine Ebene nach unten angewendet. Alles tiefere wird nicht beachtet. Also enthaltene RTs und deren rigide Typen-Partner. Nicht
     * aber weitere RT partner dieser rigiden Typen. Oder, falls ein rigider Typ-Partner ein CT ist, nicht auch noch dessen Inhalte, usw.
     *
     * @param ctObj Der zu speichernde CT
     * @param createOrUpdateAllContainingRT `true`=Alle in dem CT enthaltenen RTs werden iterativ an `createOrUpdate` weitergegeben; `false`=nicht
     * @param createOrUpdateAllPlayersFromContainingRT `true`=Für alle im CT enthaltenen RTs werden alle Player ermittelt und für jeden ein
     *                                                 `createOrUpdate` ausgeführt; `false`=nicht
     * @throws Exception
     */
    public void createOrUpdate(MetaPersistenceCt ctObj, boolean createOrUpdateAllContainingRT, boolean createOrUpdateAllPlayersFromContainingRT) throws Exception {
//        Serializer.printAllFields(ctObj);

//        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
//        if(!MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(ctObj)))
//            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen ermitteln
        BasicClassInformation classInfos = new BasicClassInformation(ctObj);

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(ctObj);
        UUID uuid_ = ((MetaPersistenceCt) ctObj).uuid_();

        // Eventuell gibt es das Objekt schon in der Datenbank, daher Abfrage starten
        Query query = session.createQuery("select ct from CT as ct where ct.uuid_ = :uuid ");
        query.setParameter("uuid", uuid_);
        List<?> allCTs = query.list();

        // Gibt es dieses Objekt in der Datenbank schon?
        CT ct;
        if(allCTs.size() == 0){
            // als neue Entität anlegen
            ct = new CT();
            ct.classPackage = classInfos.classPackage;
            ct.uuid_ = uuid_;
        }else{
            // die bereits bestehende Entität nutzen
            ct = (CT) allCTs.get(0); // einfach das erste nehmen, UUIDs sind UNIQUE

            // Alle Variablen löschen, da diese gleich neu gesetzt werden
            //TODO hohe Laufzeit, sollte man später ändern
            for(Variable var : ct.variables){
                session.delete(var);
            }
        }
        ct.variables = new HashSet<Variable>(); // auch bei bereits bestehenden Entitäten leeren = alles löschen
        session.saveOrUpdate(ct);

        // Alle Variablen hinzufügen
        String[] variableExceptions = {"plays", "roleEquivalents", "roleImplications", "roleProhibitions", "thisComp",
                "AND$module", "MatchAny$module", "NOT$module", "OR$module", "PlayerEquality$module", "Relationship$module", "RoleGroup$module",
                "Types$module", "WithProperty$module", "WithResult$module", "XOR$module", "bitmap$0",
                "scroll$internal$support$RoleGroups$$roleGroups", "scroll$internal$support$RoleRestrictions$$restrictions",
                "cached", "checkForCycles"};
        DatabaseHelper.addAllVariablesToEntity(ctObj, ct, session, variableExceptions);

        // Eigentlichen CT speichern
        session.saveOrUpdate(ct);

        // Folgender Block nur, wenn mindestens eine Erweiterung gewünscht wird
        if(createOrUpdateAllContainingRT || createOrUpdateAllPlayersFromContainingRT){

            // alle in dem CT enthaltenen RT ermitteln, die von mindestens einem rigiden Typen gespielt werden, inklusive dem rigiden Typen
            Collection allContainings = JavaConverters.asJavaCollection(((MetaPersistenceCt) ctObj).allPlayers());
            List<Object> rigidTypes = new ArrayList<>();
            List<Object> rtTypes = new ArrayList<>();
            for(Object o : allContainings){
                // Was genau haben wir gefunden?
                if(MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // rigiden Typen, genauer: CT
                    rigidTypes.add(o);
                else if(MetaPersistenceNt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // rigiden Typen, genauer: NT
                    rigidTypes.add(o);
                else if(MetaPersistenceRt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // Spieler, also RT
                    rtTypes.add(o);
                else // nichts, was wir erwarten würden
                    throw new Exception("Ein enthaltenes Objekt erbt nicht von der benötigten Persistierungs Struktur.");
            }

            // Jetzt erst rigide Typen speichern lassen, falls gewünscht, und dann erst die RTs (um zu vermeiden, RTs speichern zu wollen
            // ohne deren rigide Typen bereits in der Datenbank zu haben)

            // Sollen alle rigiden Typen von den in dem CT enthaltenen RTs gepseichert werden?
            if(createOrUpdateAllPlayersFromContainingRT){
                for(Object o : rigidTypes){
                    if(MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // rigiden Typen, genauer: CT
                        Database.ct().createOrUpdate((MetaPersistenceCt) o, false, false);
                    else if(MetaPersistenceNt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // rigiden Typen, genauer: NT
                        Database.nt().createOrUpdate((MetaPersistenceNt) o);
                    else // nichts, was wir erwarten würden
                        throw new Exception("Unerwartete Vererbung (rigide).");
                }
            }

            // Sollen alle in dem CT enthaltenen RTs mit gespeichert werden?
            if(createOrUpdateAllContainingRT){
                for(Object o : rtTypes){
                    if(MetaPersistenceRt.class.isAssignableFrom(BasicClassInformation.getClass(o))) // rigiden Typen, genauer: CT
                        Database.rt().createOrUpdate((MetaPersistenceRt) o, false, false);
                    else // nichts, was wir erwarten würden
                        throw new Exception("Unerwartete Vererbung (role).");
                }
            }
        }

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden CT in der Datenbank.
     * Dabei versucht die Methode an so viele weitere Objekte zu gelangen wie nur irgend möglich. Dabei wird sich von dem initialen CT entlang
     * gehangelt zu allen enthaltenen RTs, den rigiden Spielpartnern dieser RTs und falls davon ein CT dabei ist, wird damit exakt auch
     * so verfahren.
     * Diese Methode versucht so tief nur irgend möglich zu gelangen um einem "alles speichern" ähnlich zu sein.
     *
     * Eigentlich wäre der primitive Ansatz, die UUIDs von allen Objekten zwischenzuhalten, die man schon gespeichert hat. Allerdings
     * könnte es folgendes Szenario geben:
     * NT 1 spielt RT 1
     * NT 1 wird dann gespeichert und neu geladen, dadurch entsteht NT 1', was die gleiche UUID hat wie NT 1, aber eine andere Instanz
     * zur Laufzeit darstellt (anderer Arbeitsspeicherplatz).
     * NT 1' spielt nun RT 2
     * Hat man also NT 1 schon gespeichert und sich gemerkt, dass dies gemacht wurde, was nötig ist für Zyklenerkennung, um keine
     * Endlosschleife zu generieren (CT 1 beinhaltet RT 1 und RT 1 spielt CT 1), so würde NT 1' mit RT 2 nie gespeichert werden.
     *
     * Lösung: HashWerte in Java statt UUIDs zwischenhalten, da diese den Arbeitsspeicherplatz angeben und somit wirklich gleiche Instanzen
     * angeben, unabhängig deren UUIDs.
     * Die von dieser Methode aufgerufenen weiteren Methoden kümmern sich dann um die UUIDs und die Persistenzebene mit Dopplungen, dies
     * kann daher hier ignoriert werden.
     *
     * TODO ist nicht ganz so schön, da Schleifen in Schleifen. Wären als zwei oder drei Methoden, die sich rekursiv aufrufen, wohl um einiges schöner, solange eine META Instanz den Cache für alle bereitstellt. Aber da es so geht ist das "verschönern" wohl etwas für die Zukunft.
     *
     * @param ctObj Der zu speichernde CT, welcher als Ausgangspunkt genutzt wird
     * @throws Exception
     */
    public void createOrUpdateRecursive(MetaPersistenceCt ctObj) throws Exception {
        // Cachen aller Hash Werte, um sich zu merken, was man schon persistiert hat
        Set<Integer> alreadySaved_hashCodes = new HashSet<>(); //TODO

        // Liste aller CTs, die noch mit allen enthaltenen RTs und deren Spielern durchgegangen werden muss (statt rekursivem Methodenaufruf) wegen Rückgabestack ,da einer Überblick über alles halten muss
        Map<Integer, Object> ctToSave = new HashMap<>();
        ctToSave.put(ctObj.hashCode(), ctObj); // Ausgangspunkt ist der übergebene CT

        // Solange es noch CTs gibt, die noch nicht durchgegangen wurden
        while (ctToSave.size() > 0){
            // Infos zum nächsten CT, der jetzt durchgegangen werden soll
            Map.Entry<Integer, Object> ctCurrent_trash = ctToSave.entrySet().iterator().next();
            int ctCurrent_hashCode = ctCurrent_trash.getKey();
            MetaPersistenceCt ctCurrent_entity = (MetaPersistenceCt) ctCurrent_trash.getValue();

            // CT wird jetzt durchgegangen, kann aber jetzt schon als abgeschlossen angesehen werden
            alreadySaved_hashCodes.add(ctCurrent_hashCode);
            ctToSave.remove(ctCurrent_hashCode);

            // Den CT selbst persistieren
            Database.ct().createOrUpdate(ctCurrent_entity, false, false);

            // Alle enthaltenen RT mit deren spielenden rigiden Typen ermitteln
            Collection allContainings = JavaConverters.asJavaCollection(ctCurrent_entity.allPlayers());
            for(Object o : allContainings){
                // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
                if(alreadySaved_hashCodes.contains(o.hashCode()))
                    continue;

                // Was genau haben wir gefunden?
                //TODO müsste eigentlich wie in `createOrUpdate` getrennt werden, damit erst rigide Typen persistiert werden und erst DANACH die RTs. Wegen Wohlgeformtheit und Fremdschlüsseln, aber das führt mittlerweile nicht mehr zu fehlern und daher ist es so eleganter. Ggf in der anderen Methode noch vereinfachen?
                if(MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(o))) { // rigiden Typen, genauer: CT
                    // Wurde oder wird dieser CT durchgegangen?
                    if(!alreadySaved_hashCodes.contains(o.hashCode()) && !ctToSave.containsKey(o.hashCode())) // noch nie gesehen
                        ctToSave.put(o.hashCode(), o);
                }else if(MetaPersistenceNt.class.isAssignableFrom(BasicClassInformation.getClass(o))) { // rigiden Typen, genauer: NT
                    alreadySaved_hashCodes.add(o.hashCode());
                    Database.nt().createOrUpdate((MetaPersistenceNt) o);
                }else if(MetaPersistenceRt.class.isAssignableFrom(BasicClassInformation.getClass(o))) { // Spieler, also RT
                    // RT selbst speichern
                    alreadySaved_hashCodes.add(o.hashCode());
                    Database.rt().createOrUpdate((MetaPersistenceRt) o, false, false);

                    // alle rigiden Spieler des RT ermitteln
                    Collection allPlayers = JavaConverters.asJavaCollection(ctCurrent_entity.getRolesFromHash(o.hashCode()));
                    for(Object player : allPlayers){
                        // Wurde genau dieses Objekt schon ein mal angefasst (überprüfung durch HashCode) überspringen wir es
                        if(alreadySaved_hashCodes.contains(o.hashCode()))
                            continue;

                        if(MetaPersistenceNt.class.isAssignableFrom(BasicClassInformation.getClass(player))) { // NT
                            alreadySaved_hashCodes.add(o.hashCode());
                            Database.nt().createOrUpdate((MetaPersistenceNt) player);
                        }else if(MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(player))) { // CT
                            // Wurde oder wird dieser CT durchgegangen?
                            if(!alreadySaved_hashCodes.contains(o.hashCode()) && !ctToSave.containsKey(o.hashCode())) // noch nie gesehen
                                ctToSave.put(o.hashCode(), o);
                        }else if(MetaPersistenceRt.class.isAssignableFrom(BasicClassInformation.getClass(player))) { // RT
                            throw new Exception("Ein RT darf keinen RT spielen.");
                        }else // nichts
                            throw new Exception("Der Player scheint kein NT, CT oder RT zu sein.");
                    }
                }else // nichts, was wir erwarten würden
                    throw new Exception("Ein enthaltenes Objekt erbt nicht von der benötigten Persistierungs Struktur.");
            }
        }
    }

    /**
     * Löscht einen CT. Dabei werden auch alle Spielrelationen dieses CT zu RTs gelöscht.
     * ACHTUNG: Dadurch können RT entstehen, die von niemandem mehr gespielt werden.
     * TODO Sollte man mit einem Garbage Collector dessen RT in der Zukunft löschen?
     *
     * @param ctObj Der CT, der gelöscht werden soll.
     * @return true=Objekt wurde in der Datenbank gefunden und auch gelöscht; false=nicht
     * @throws Exception
     */
    public boolean delete(MetaPersistenceCt ctObj) throws Exception {
//        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
//        if(!MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(ctObj)))
//            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(ctObj);
        UUID uuid_ = ((MetaPersistenceCt) ctObj).uuid_();

        // DELETE auf der Datenbank ausführen
        Query query = session.createQuery("delete from CT as ct where ct.uuid_ = :uuid ");
        query.setParameter("uuid", uuid_);
        int numberRowsChanged = query.executeUpdate();

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        // Wurde etwas gelöscht?
        if(numberRowsChanged > 0)
            // Mindestens einen Fund, und da UUID UNIQUE ist, wohl genau einen
            return true;

        // Objekt wurde nicht gefunden und wurde daher auch nicht gelöscht
        return false;
    }

    /**
     * Selektiert CTs.
     *
     * @param ctObjClass Die Klasse des CT, in welchem die Instanz gesucht werden soll
     * @param variableName Nach diesem Attribut wird in der Datenbank gesucht (key)
     * @param value Der Wert des Attributes, nach dem gesucht werden soll (value)
     * @return List<?> Eine Liste der CTs die auf die Bedingung zutreffen
     * @throws Exception
     */
    public List<?> select(Class ctObjClass, String variableName, Object value) throws Exception {
        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceCt.class.isAssignableFrom(ctObjClass))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen ermitteln
        BasicClassInformation classInfos = new BasicClassInformation(ctObjClass);

        // Entität aus der Datenbank ermitteln
//        List<CT> allCt = this.ctRepository.findAllByHash(hash);
        Query query = session.createQuery("select ct from CT as ct inner join ct.variables as variables " +
                "where ct.classPackage = :classPackage and variables.name = :name and variables.value = :value ");
        query.setParameter("classPackage", classInfos.classPackage);
        query.setParameter("name", variableName);
        query.setParameter("value", value);
        List<?> allCTs = query.list();

        // Rückgabe Liste initialisieren
//        List<?> results = new ArrayList<Object>();
//        ArrayList<?> results = ListHelper.listOf((Class<?>) ctObjClass);
        ArrayList<Object> results = ListHelper.listOf(ctObjClass);

        // Über alle gefundenen Entitäten iterieren
        if(allCTs.size() > 0){
            for(CT ct : (List<CT>) allCTs){
//                ct = (CT) session.merge(ct); // re-attach

                // Aus der Entität aus der Datenbank eine Instanz der eigentlich echten Anwendung machen
                Object newObj = Serializer.getInstanceOfEntity(ct, ctObjClass);

                // Das fertige neue Objekt der Rückgabe Liste hinzufügen
                results.add(newObj);
            }
        }

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        // Rückgabe der Ergebnisse
        return results;
    }

}
