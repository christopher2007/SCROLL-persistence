package scroll.persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Model.NT;
import scroll.persistence.Model.Variable;
import scroll.persistence.Util.*;

import java.lang.reflect.Field;
import java.util.*;

public class _NT {

    // Singelton Pattern
    private static _NT instance;
    private _NT () {}
    // protected, nur für aktuelles Package
    protected static _NT getInstance () {
        if (_NT.instance == null)
            _NT.instance = new _NT ();
        return _NT.instance;
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden NT in der Datenbank.
     * Kein Beachten von CTs oder RTs, nur der reine NT. Auch keine Spielrelationen.
     *
     * @param ntObj Der zu speichernde NT
     * @throws Exception
     */
    public void createOrUpdate(Object ntObj) throws Exception {
//        Serializer.printAllFields(ntObj);

        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNt.class.isAssignableFrom(ntObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen ermitteln
        BasicClassInformation classInfos = new BasicClassInformation(ntObj);

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(ntObj);
        UUID uuid_ = ((MetaPersistenceNt) ntObj).uuid_();

        // Eventuell gibt es das Objekt schon in der Datenbank, daher Abfrage starten
//        List<?> allNTs = this.getAllNtByNtEntity("uuid_", uuid_);
//        List<NT> allNt = this.ntRepository.findAllByHash(hash);
        Query query = session.createQuery("select nt from NT as nt where nt.uuid_ = :uuid ");
        query.setParameter("uuid", uuid_);
        List<?> allNTs = query.list();

        // Gibt es dieses Objekt in der Datenbank schon?
        scroll.persistence.Model.NT nt;
        if(allNTs.size() == 0){
            // als neue Entität anlegen
            nt = new scroll.persistence.Model.NT();
            nt.classPackage = classInfos.classPackage;
            nt.uuid_ = uuid_;
        }else{
            // die bereits bestehende Entität nutzen
            nt = (scroll.persistence.Model.NT) allNTs.get(0); // einfach das erste nehmen, UUIDs sind UNIQUE

            // Alle Variablen löschen, da diese gleich neu gesetzt werden
            //TODO hohe Laufzeit, sollte man später ändern
            for(Variable var : nt.variables){
                session.delete(var);
            }
        }
        nt.variables = new HashSet<Variable>(); // auch bei bereits bestehenden Entitäten leeren = alles löschen
        session.saveOrUpdate(nt);

//        // Über alle Variablen des übergebenen NT iterieren und diese in der Datenbank speichern
//        Collection<Field> fields = Serializer.getAllFields(ntObj.getClass());
//        for(Field field : fields){
//            field.setAccessible(true); // auch `privat` Variablen müssen lesbar und schreibbar sein
//            String variableName = field.getName();
//            Object variableValue = null;
//            try {
//                variableValue = field.get(ntObj);
//            } catch (IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//
//            // Die Variable `uuid_` ignorieren wir, da sie auf Ebene der NT-Entity selbst gespeichert werden soll
//            if(variableName == "uuid_")
//                continue;
//
//            // Eine Variablen Entity erstellen
//            Variable var = new Variable();
//            var.entity = nt;
//            var.name = variableName;
//            var.value = variableValue;
//            nt.variables.add(var);
//
////            System.out.println("Variablen Name: " + variableName);
//
//            // Variablen Entity speichern
//            session.saveOrUpdate(var);
//        }

        // Alle Variablen hinzufügen
        DatabaseHelper.addAllVariablesToEntity(ntObj, nt, session);


        // Eigentlichen NT speichern
        session.saveOrUpdate(nt);

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();
    }

//    private List<?> getAllNtByNtEntity(String variableName, Object value){
//        // Session und Transaktion ermitteln bzw. initialisieren
//        Session session = SessionFactory.getNewOrOpenSession();
//        SessionFactory.openTransaction();
//
////        List<NT> allNt = this.ntRepository.findAllByHash(hash);
//        Query query = session.createQuery("select nt from NT as nt where nt."+variableName+" = :value ");
//        query.setParameter("value", value);
//        List<?> list = query.list();
//
//        // Transaktion und Session schließen bzw. committen
//        SessionFactory.closeTransaction();
////        session.close();
//
//        return list;
//    }

//    /**
//     * Selektiert einen NT.
//     *
//     * @param ntObj Die Instanz eines Natürlichen Typen, auf den die Ergebnisse geschrieben werden sollen
//     * @param variableName Nach diesem Attribut wird in der Datenbank gesucht (key)
//     * @param value Der Wert des Attributes, nach dem gesucht werden soll (value)
//     * @throws Exception
//     */
//    public void select(Object ntObj, String variableName, Object value) throws Exception {
//        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
//        if(!MetaPersistenceNt.class.isAssignableFrom(ntObj.getClass()))
//            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");
//
//        // Session und Transaktion ermitteln bzw. initialisieren
//        Session session = SessionFactory.getNewOrOpenSession();
//        SessionFactory.openTransaction();
//
//        // Entität aus der Datenbank ermitteln
////        List<NT> allNt = this.ntRepository.findAllByHash(hash);
//        Query query = session.createQuery("select nt from NT as nt inner join nt.variables as variables " +
//                "where variables.name = :name and variables.value = :value ");
//        query.setParameter("name", variableName);
//        query.setParameter("value", value);
//        List<?> allNTs = query.list();
//        if(allNTs.size() == 0)
//            throw new Exception("Keinen Eintrag gefunden");
//        if(allNTs.size() > 1)
//            throw new Exception("Zu viele Einträge gefunden, nicht eindeutig genug");
//
//        // Es kann nur noch ein Ergebnis geben
//        scroll.persistence.Model.NT selectedNt = (scroll.persistence.Model.NT) allNTs.get(0);
////        selectedNt = (NT) session.merge(selectedNt); // re-attach
//
//        // Nur eine Klassenvariable neu setzen
////        Field field = ntObj.getClass().getDeclaredField(variableName);
////        field.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
////        field.set(ntObj, value);
//
//        // Die Variablen der ermittelten Entität aufbereiten
//        HashMap<String, Object> variablesSelected = new HashMap<String, Object>();
//        for(Variable var : selectedNt.variables){
//            variablesSelected.put(var.name, var.value);
//        }
//
//        // Alle Klassenvariablen durchgehen und setzen
//        Collection<Field> fields = Serializer.getAllFields(ntObj.getClass());
//        for(Field fieldOriginalNt : fields){
////            Field fieldOriginalNt2 = ntObj.getClass().getDeclaredField(fieldOriginalNt.getName());
////            fieldOriginalNt2.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
////            fieldOriginalNt2.set(ntObj, fieldOriginalNt.get(fieldOriginalNt));
//
//            // auch `privat` Variablen müssen veränderbar sein
//            fieldOriginalNt.setAccessible(true);
//
//            // Das Feld `uuid_` speziell behandeln, da es als Variablen-Entity nicht existiert, sondern im NT-Entity gespeichert wurde
//            if(fieldOriginalNt.getName() == "uuid_"){
//                fieldOriginalNt.set(ntObj, selectedNt.uuid_);
//                continue;
//            }
//
//            // normales setzen eines Attributs
//            fieldOriginalNt.set(ntObj, variablesSelected.get(fieldOriginalNt.getName()));
//        }
//
//        // Transaktion und Session schließen bzw. committen
//        SessionFactory.closeTransaction();
////        session.close();
//    }

    /**
     * Selektiert NTs.
     *
     * @param ntObjClass Die Instanz eines Natürlichen Typen, auf den die Ergebnisse geschrieben werden sollen
     * @param variableName Nach diesem Attribut wird in der Datenbank gesucht (key)
     * @param value Der Wert des Attributes, nach dem gesucht werden soll (value)
     * @return List<?> Eine Liste der NTs die auf die Bedingung zutreffen
     * @throws Exception
     */
    public List<?> select(Class ntObjClass, String variableName, Object value) throws Exception {
        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNt.class.isAssignableFrom(ntObjClass))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen ermitteln
        BasicClassInformation classInfos = new BasicClassInformation(ntObjClass);

        // Entität aus der Datenbank ermitteln
//        List<NT> allNt = this.ntRepository.findAllByHash(hash);
        Query query = session.createQuery("select nt from NT as nt inner join nt.variables as variables " +
                "where nt.classPackage = :classPackage and variables.name = :name and variables.value = :value ");
        query.setParameter("classPackage", classInfos.classPackage);
        query.setParameter("name", variableName);
        query.setParameter("value", value);
        List<?> allNTs = query.list();

        // Rückgabe Liste initialisieren
//        List<?> results = new ArrayList<Object>();
//        ArrayList<?> results = ListHelper.listOf((Class<?>) ntObjClass);
        ArrayList<Object> results = ListHelper.listOf(ntObjClass);

        // Über alle gefundenen Entitäten iterieren
        if(allNTs.size() > 0){
            for(NT nt : (List<NT>) allNTs){
//                nt = (NT) session.merge(nt); // re-attach

//                // Eine Instanz der Zielklasse erzeugen
                Objenesis o = new ObjenesisStd(false); // cache disabled
                Object newObj = o.newInstance(ntObjClass);

                // Nur eine Klassenvariable neu setzen
//                Field field = newObj.getClass().getDeclaredField(variableName);
//                field.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
//                field.set(newObj, value);

                // Die Variablen der ermittelten Entität aufbereiten
                HashMap<String, Object> variablesSelected = new HashMap<String, Object>();
                for(Variable var : nt.variables){
                    variablesSelected.put(var.name, var.value);
                }

                // Alle Klassenvariablen durchgehen und setzen
                Collection<Field> fields = Serializer.getAllFields(newObj.getClass());
                for(Field fieldOriginalNt : fields){
//                    Field fieldOriginalNt2 = newObj.getClass().getDeclaredField(fieldOriginalNt.getName());
//                    fieldOriginalNt2.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
//                    fieldOriginalNt2.set(newObj, fieldOriginalNt.get(fieldOriginalNt));

                    // auch `privat` Variablen müssen veränderbar sein
                    fieldOriginalNt.setAccessible(true);

                    // Das Feld `uuid_` speziell behandeln, da es als Variablen-Entity nicht existiert, sondern im NT-Entity gespeichert wurde
                    if(fieldOriginalNt.getName() == "uuid_"){
                        fieldOriginalNt.set(newObj, nt.uuid_);
                        continue;
                    }

                    // normales setzen eines Attributs
                    fieldOriginalNt.set(newObj, variablesSelected.get(fieldOriginalNt.getName()));
                }

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

    /**
     * Löscht einen NT. Dabei werden auch alle Spielrelationen dieses NT zu RTs gelöscht.
     * ACHTUNG: Dadurch können RT entstehen, die von niemandem mehr gespielt werden.
     * TODO Sollte man mit einem Garbage Collector diese RT in der Zukunft löschtn?
     *
     * @param ntObj Der NT, der gelöscht werden soll.
     * @return true=Objekt wurde in der Datenbank gefunden und auch gelöscht; false=nicht
     * @throws Exception
     */
    public boolean delete(Object ntObj) throws Exception {
        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNt.class.isAssignableFrom(ntObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // UUID ermitteln
//        UUID uuid_ = HelperGetUUID.getUUID(ntObj);
        UUID uuid_ = ((MetaPersistenceNt) ntObj).uuid_();

        // DELETE auf der Datenbank ausführen
        Query query = session.createQuery("delete from NT as nt where nt.uuid_ = :uuid ");
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
