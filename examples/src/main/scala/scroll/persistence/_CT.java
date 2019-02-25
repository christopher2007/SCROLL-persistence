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
    public void createOrUpdate(Object ctObj) throws Exception {
        this.createOrUpdate(ctObj, false, false);
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden CT in der Datenbank.
     *
     * Steht `createOrUpdateAllContainingRT` auf `true`, so werden für alle RTs in dem übergebenen CT ein `createOrUpdate` ausgeführt. Somit werden
     * alle beinhalteten RTs gespeichert, wenn nicht bereits in der Datenbank vorhanden.
     *
     * Steht `createOrUpdateAllPlayersFromContainingRT` auf `true`, so wird für alle Player von den in dem CT enthaltenen RTs ein `createOrUpdate`
     * ausgeführt.
     *
     * `createOrUpdateAllContainingRT` und `createOrUpdateAllPlayersFromContainingRT` sind unabhängig voneinander und können in beliebigen
     * Kombinationen gesetzt werden (kann aber natürlich zu geworfenen Exceptions führen, wenn man die Playing Relationen speichern möchte
     * ohne dass die Spieler bereits in der Datenbakn existieren).
     *
     * @param ctObj Der zu speichernde CT
     * @param createOrUpdateAllContainingRT `true`=Alle in dem CT enthaltenen RTs werden iterativ an `createOrUpdate` weitergegeben; `false`=nicht
     * @param createOrUpdateAllPlayersFromContainingRT `true`=Das Compartment in dem der übergebene RT enthalten ist wird mit gespeichert, aber nur das
     *                                   Compartment selbst, keine weiteren enthaltenen RTs; `false`=nicht
     * @throws Exception
     */
    public void createOrUpdate(Object ctObj, boolean createOrUpdateAllContainingRT, boolean createOrUpdateAllPlayersFromContainingRT) throws Exception {
//        Serializer.printAllFields(ctObj);

        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceCt.class.isAssignableFrom(ctObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

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
                "scroll$internal$support$RoleGroups$$roleGroups", "scroll$internal$support$RoleRestrictions$$restrictions"};
        DatabaseHelper.addAllVariablesToEntity(ctObj, ct, session, variableExceptions);

        // Eigentlichen CT speichern
        session.saveOrUpdate(ct);

        // Sollen alle in dem CT enthaltenen RTs mit gespeichert werden?
        if(createOrUpdateAllContainingRT){
            //TODO
        }

        // Sollen alle Player von den in dem CT enthaltenen RTs gepseichert werden?
        if(createOrUpdateAllPlayersFromContainingRT){
            //TODO
        }

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();
    }

    /**
     * Löscht einen CT. Dabei werden auch alle Spielrelationen dieses CT zu RTs gelöscht.
     * ACHTUNG: Dadurch können RT entstehen, die von niemandem mehr gespielt werden.
     * TODO Sollte man mit einem Garbage Collector diese RT in der Zukunft löschtn?
     *
     * @param ctObj Der CT, der gelöscht werden soll.
     * @return true=Objekt wurde in der Datenbank gefunden und auch gelöscht; false=nicht
     * @throws Exception
     */
    public boolean delete(Object ctObj) throws Exception {
        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceCt.class.isAssignableFrom(ctObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

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

}
