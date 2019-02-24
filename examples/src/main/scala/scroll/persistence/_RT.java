package scroll.persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import scala.collection.Seq;
import scroll.internal.Compartment;
import scroll.internal.IPlayer;
import scroll.persistence.Inheritance.MetaPersistenceCt;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Inheritance.MetaPersistenceRt;
import scroll.persistence.Model.CT;
import scroll.persistence.Model.Entity;
import scroll.persistence.Model.RT;
import scroll.persistence.Model.Variable;
import scroll.persistence.Util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class _RT {

    // Singelton Pattern
    private static _RT instance;
    private _RT() {}
    // protected, nur für aktuelles Package
    protected static _RT getInstance () {
        if (_RT.instance == null)
            _RT.instance = new _RT();
        return _RT.instance;
    }

    /**
     * Ruft die gleichnamige Methode der Klasse auf und setzt den Übergabeparameter `createOrupdatePlayers` und auch `createOrUpdateContainingCT`
     * auf `false`.
     *
     * @param rtObj Der zu speichernde RT
     * @throws Exception
     */
    public void createOrUpdate(Object rtObj) throws Exception {
        this.createOrUpdate(rtObj, false, false);
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden RT in der Datenbank.
     *
     * Steht `createOrupdatePlayers` auf `false`, so müssen die Spieler alle schon in der Datenbank existieren, andernfalls werden Fehler geworfen.
     * Steht `createOrupdatePlayers` jedoch auf `true`, so wird für jeden Spieler ein `createOrUpdate` in den zu den Spielern gehörigen
     * Persistance Klassen ausgeführt.
     * NT, RT und CT (alles Spieler) können RT (gespielter) spielen.
     * Grundlegend werden die Spielrelationen mit dem Speichern der RT auch persistiert, da diese `played by` an den RT hängen. NT & CT können
     * ohne Spielrelationen gespeichert werden.
     *
     * Steht `createOrUpdateContainingCT` auf `false`, so muss der CT, in dem der zu speichernde RT liegt, schon in der Datenbank existieren,
     * andernfalls werden Fehler geworfen.
     * Steht `createOrUpdateContainingCT` jedoch auf `true`, so wird für den CT ein `createOrUpdate` aufgerufen. Allerdings wird dabei nur
     * der CT sehr grundlegend selbst gespeichert, keine weiteren enthaltenen RT, keine Spielerelationen des CT, ...
     *
     * @param rtObj Der zu speichernde RT
     * @param createOrUpdatePlayers `true`=Spieler werden alle auch mit einem `createOrUpdate` iterativ weitergegeben; `false`=nicht
     * @param createOrUpdateContainingCT `true`=Das Compartment in dem der übergebene RT enthalten ist wird mit gespeichert, aber nur das
     *                                   Compartment selbst, keine weiteren enthaltenen RTs; `false`=nicht
     * @throws Exception
     */
    public void createOrUpdate(Object rtObj, boolean createOrUpdatePlayers, boolean createOrUpdateContainingCT) throws Exception {
//        Serializer.printAllFields(rtObj);

        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceRt.class.isAssignableFrom(rtObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen ermitteln
        BasicClassInformation classInfos = new BasicClassInformation(rtObj);

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(rtObj);
        UUID uuid_ = ((MetaPersistenceRt) rtObj).uuid_();

        // Das Compartment ermitteln, das um den RT liegt
        Class<?> c = rtObj.getClass();
        Field f = c.getDeclaredField("$outer");
        f.setAccessible(true);
//        Compartment compartment = (Compartment) f.get(rtObj);
        MetaPersistenceCt compartment = (MetaPersistenceCt) f.get(rtObj);
//        UUID compartmentUUID = HelperGetUUID.getUUID(compartment);
        UUID compartmentUUID = compartment.uuid_();

        // Eventuell muss für den CT ein `createOrUpdate` aufgerufen werden
        if(createOrUpdateContainingCT)
            Database.ct().createOrUpdate(compartment);

        // Alle Spieler des aktuellen RT ermitteln
//        List<Object> allPlayers = (List<Object>) compartment.getRolesFromHash(rtObj.hashCode()).toList();
        List<Object> allPlayers = scala.collection.JavaConversions.seqAsJavaList(compartment.getRolesFromHash(rtObj.hashCode()));

        // Eventuell sollen die Spieler, die diesen RT spielen, mit persistiert werden
        // (andernfalls wird einfach angenommen, dass sie schon in der Datenbank existieren)
        if(createOrUpdatePlayers){
            for(Object player : allPlayers){
                // Der Spieler kann ein NT, CT oder RT sein, daher muss unterschieden werden
                if(MetaPersistenceNt.class.isAssignableFrom(player.getClass())) // NT
                    Database.nt().createOrUpdate(player);
                else if(MetaPersistenceCt.class.isAssignableFrom(player.getClass())) // CT
                    Database.ct().createOrUpdate(player);
                else if(MetaPersistenceRt.class.isAssignableFrom(player.getClass())) // RT
                    Database.rt().createOrUpdate(player);
                else // nichts
                    throw new Exception("Der Player scheint kein NT, CT oder RT zu sein.");
            }
        }

        // === Jetzt existieren der übergeordnete CT, in dem der übergebene RT sich befindet, und alle Spieler des RT in der Datenbank, daher
        // === können wir nun den RT selbst mit allen Fremdschlüsseln speichern, ohne in Fehler zu laufen (außer der spätere Entwickler hat
        // === etwas falsch gemacht, aber er hat alle Hilfsmittel, um es Fehlerfrei zu bewerkstelligen)

        // Den CT, in dem der übergebene RT liegt, aus der Datenbank ermitteln
        Query query = session.createQuery("select ct from CT as ct where ct.uuid_ = :uuid");
        query.setParameter("uuid", compartmentUUID);
        List<?> allCTs = query.list();
        CT ct = (CT) allCTs.get(0); // Da UUIDs UNIQUE sind

        // Eine Liste mit allen UUIDs der Spieler erzeugen
        List<UUID> allPlayersUUIDs = new ArrayList<>();
        for(Object player : allPlayers){
            UUID trash = HelperGetUUID.getUUID(player);
            allPlayersUUIDs.add(trash);
        }

        // Alle Spieler aus der Datenbank ermitteln
        Query query2 = session.createQuery("select entity from Entity as entity where entity.uuid_ IN :uuids");
        query2.setParameter("uuids", allPlayersUUIDs);
        List<?> allPlayersEntities = query2.list();

        // Ist der aktuelle Aufruf ein erstmaliger INSERT oder ein UPDATE?
        //TODO das werde ich vorerst nicht unterscheiden, da der Umfang der gesamten Aufgabe auch ohne schon gigangisch ist, daher einfach ein DELETE vorab, dann ist es auch jeden fall ein neuer INSERT
        this.delete(rtObj);

        // Den neuen RT erstellen und speichern
        RT rt = new RT();
        rt.classPackage = classInfos.classPackage;
        rt.uuid_ = uuid_;
        rt.variables = new HashSet<Variable>();
        session.saveOrUpdate(rt);

        // Alle Variablen hinzufügen
        String[] variableExceptions = {"$outer"};
        DatabaseHelper.addAllVariablesToEntity(rtObj, rt, session, variableExceptions);

        // Die Relation speichern, in welchem CT der übergebene RT liegt
        rt.containedIn = ct;

        // Die Spielrelationen hinzufügen
        rt.playedBy = (Set<Entity>) allPlayersEntities;

        // Eigentlichen RT speichern
        session.saveOrUpdate(rt);

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();
    }

    /**
     * Löscht einen RT. Dabei werden auch alle Spielrelationen dieses RT zu RTs gelöscht.
     * ACHTUNG: Dadurch können RT entstehen, die von niemandem mehr gespielt werden.
     * TODO Sollte man mit einem Garbage Collector diese RT in der Zukunft löschtn?
     *
     * @param rtObj Der RT, der gelöscht werden soll.
     * @return true=Objekt wurde in der Datenbank gefunden und auch gelöscht; false=nicht
     * @throws Exception
     */
    public boolean delete(Object rtObj) throws Exception {
        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceRt.class.isAssignableFrom(rtObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(rtObj);
        UUID uuid_ = ((MetaPersistenceRt) rtObj).uuid_();

        // DELETE auf der Datenbank ausführen
        Query query = session.createQuery("delete from RT as rt where rt.uuid_ = :uuid ");
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
