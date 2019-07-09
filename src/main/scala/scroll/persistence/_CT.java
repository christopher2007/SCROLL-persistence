package scroll.persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import scala.reflect.ClassTag;
import scroll.persistence.Inheritance.MetaPersistenceCt;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Inheritance.MetaPersistenceRt;
import scroll.persistence.Inheritance._meta;
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

            //TODO muss nicht mehr getrennt werden, da neues Datenmodell, kann also in der Zukunft mal refaktoriert werden um Laufzeit zu bessern

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
                    if(MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(o)))
                        Database.ct().createOrUpdate((MetaPersistenceCt) o, false, false);
                    else if(MetaPersistenceNt.class.isAssignableFrom(BasicClassInformation.getClass(o)))
                        Database.nt().createOrUpdate((MetaPersistenceNt) o);
                    else // nichts, was wir erwarten würden
                        throw new Exception("Unerwartete Vererbung (rigide).");
                }
            }

            // Sollen alle in dem CT enthaltenen RTs mit gespeichert werden?
            if(createOrUpdateAllContainingRT){
                for(Object o : rtTypes){
                    if(MetaPersistenceRt.class.isAssignableFrom(BasicClassInformation.getClass(o)))
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
     * Löscht einen CT. Dabei werden auch alle Spielrelationen dieses CT zu RTs gelöscht.
     * ACHTUNG: Löscht auch alle RTs, die in diesem CT enthalten sind, wodurch auch alle Played-By Relationen dieser RTs verloren gehen.
     * ACHTUNG: Dadurch können RT entstehen, die von niemandem mehr gespielt werden.
     * TODO Sollte man mit einem Garbage Collector dessen RT in der Zukunft löschen?
     *
     * @param ctObj Der CT, der gelöscht werden soll.
     * @param deleteOnlyIfEmpty `true`=nur löschen, wenn der CT keine RT enthält (wirft Exception, wenn doch); `false`=Löscht auch, wenn noch RT
     *                          enthalten sind. RT werden dabei auch gelöscht und alle Played-By Beziehungen von diesen.
     * @return true=Objekt wurde in der Datenbank gefunden und auch gelöscht; false=nichts gefunden und auch nichts gelöscht
     * @throws Exception
     */
    public boolean delete(MetaPersistenceCt ctObj, boolean deleteOnlyIfEmpty) throws Exception {
//        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
//        if(!MetaPersistenceCt.class.isAssignableFrom(BasicClassInformation.getClass(ctObj)))
//            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(ctObj);
        UUID uuid_ = ((MetaPersistenceCt) ctObj).uuid_();

//        // DELETE auf der Datenbank ausführen
//        Query query = session.createQuery("delete from CT as ct where ct.uuid_ = :uuid ");
//        query.setParameter("uuid", uuid_);
//        int numberRowsChanged = query.executeUpdate();

        // Entität aus der Datenbank ermitteln
        Query query = session.createQuery("select ct from CT as ct where ct.uuid_ = :uuid ");
        query.setParameter("uuid", ctObj.uuid_());
        List<?> allCTs = query.list();

        // Mindestens ein Eintrag muss gefunden werden
        if(allCTs.size() == 0)
            return false;
        CT ct = (CT) allCTs.get(0); // kann ja nur einer gefunden werden, da UUIDs UNIQUE sind

        // Hat der CT enthaltene RTs?
        if(ct.containing.size() > 0){
            // Möchte der Entwickler diese überhaupt mit löschen?
            if(deleteOnlyIfEmpty)
                throw new Exception("Der zu löschende CT ist nicht leer, soll aber nur gelöscht werden, wenn er es ist.");

            // Alle RTs löschen
            for(Entity rt : ct.containing){
                session.delete(rt);
            }
        }

        // CT selbst löschen
        session.delete(ct);

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        // Positive Rückgabe
        return true;

//        // Wurde etwas gelöscht?
//        if(numberRowsChanged > 0)
//            // Mindestens einen Fund, und da UUID UNIQUE ist, wohl genau einen
//            return true;
//
//        // Objekt wurde nicht gefunden und wurde daher auch nicht gelöscht
//        return false;
    }

    /**
     * Selektiert CTs.
     *
     * @param ctObjClass Die Klasse des CT, in welchem die Instanz gesucht werden soll
     * @param variableName Nach diesem Attribut wird in der Datenbank gesucht (key)
     * @param value Der Wert des Attributes, nach dem gesucht werden soll (value)
     * @param alsoSelectContainingRTs `true`=in dem CT enthaltene RTs werden mit geladen; `false`=nicht
     * @param alsoSelectPlayersFromContainingRTs `true`=Selektiert auch die Spieler welche mittels Played-By mit den in dem
     *                                          CT enthaltenen RT verbunden sind; `false`=nicht
     * @return List<?> Eine Liste der CTs die auf die Bedingung zutreffen
     * @throws Exception
     */
    public List<?> select(Class ctObjClass, String variableName, Object value, boolean alsoSelectContainingRTs,
                          boolean alsoSelectPlayersFromContainingRTs) throws Exception {
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

        // Einen Cache für Objekte gleicher UUIDs initialisieren
        CacheHelperUUID cache = new CacheHelperUUID();

        // Über alle gefundenen Entitäten iterieren
        if(allCTs.size() > 0){
            for(CT ct : (List<CT>) allCTs){
//                ct = (CT) session.merge(ct); // re-attach

                // Aus der Entität aus der Datenbank eine Instanz der eigentlich echten Anwendung machen
                Object newObj = Serializer.getInstanceOfEntity(ct, ctObjClass);
                _meta realCt = cache.get((_meta) newObj);

                // Sollen in diesem CT auch die enthaltenen RT ermittelt werden?
                if(alsoSelectContainingRTs){
                    for(RT rt : ct.containing){
                        // Anwendungs-Objekt erzeugen (Realanwendung)
                        Object roleObj = Serializer.getInstanceOfEntity(rt, Class.forName(rt.classPackage));
                        _meta role = cache.get((_meta) roleObj);

                        // rigide Spielpartner mit ermitteln?
                        if(alsoSelectPlayersFromContainingRTs){
                            for(Entity e : rt.playedBy){
                                // Anwendungs-Objekt erzeugen (Realanwendung)
                                Object rigidObj = Serializer.getInstanceOfEntity(e, Class.forName(e.classPackage));
                                _meta rigid = cache.get((_meta) rigidObj);

                                // In dem CT, in dem sich der RT befindet, auch die played By Beziehungen setzen
                                _CT.addPlayedByInCT((MetaPersistenceCt) realCt, rigid, role);
                            }
                        }else{
                            //TODO
                            throw new Exception("RT ohne Spielpartner können nicht in einen CT hinzugefügt werden, hier fehlt in SCROLL selbst " +
                                    "eine entsprechende Möglichkeit.");
                        }
                    }
                }

                // Das fertige neue Objekt der Rückgabe Liste hinzufügen
                results.add((MetaPersistenceCt) realCt);
            }
        }

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        // Rückgabe der Ergebnisse
        return results;
    }

    /**
     * Erstellt in einem übergebenen CT eine neue Played-By Beziehung zwischen einem Spieler und einem RT.
     *
     * @param ct In diesem CT soll die neue Played-By Beziehung erzeugt werden.
     * @param rigid Dies ist der Spieler, der den RT spielen soll.
     * @param rt Dies ist der RT, der vom Spieler gespielt werden soll.
     */
    protected static void addPlayedByInCT(MetaPersistenceCt ct, Object rigid, Object rt) {
        BasicClassInformation classInfoRigid = new BasicClassInformation(rigid);
        BasicClassInformation classInfoRt = new BasicClassInformation(rt);

        //ClassTag<LocalUser> tag = scala.reflect.ClassTag$.MODULE$.apply(LocalUser.class);

        ClassTag classTagRigid = scala.reflect.ClassTag$.MODULE$.apply(classInfoRigid.class_);
        ClassTag classTagRt = scala.reflect.ClassTag$.MODULE$.apply(classInfoRt.class_);

        ct.addPlaysRelation(rigid, rt, classTagRigid, classTagRt);
    }

}
