package scroll.persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import scroll.persistence.Inheritance.MetaPersistenceNtRt;
import scroll.persistence.Model.Variable;
import scroll.persistence.Util.Serializer;
import scroll.persistence.Util.SessionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class NT {

    // Singelton Pattern
    private static NT instance;
    private NT () {}
    // protected, nur für aktuelles Package
    protected static NT getInstance () {
        if (NT.instance == null)
            NT.instance = new NT ();
        return NT.instance;
    }


    /**
     * Ruft die gleiche Methode der Klasse auf und setzt dabei den Parameter `alsoSavePlayingRTs` auf `false`.
     * @param ntObj
     * @return
     * @throws Exception
     */
    public boolean createOrUpdate(Object ntObj) throws Exception {
        return this.createOrUpdate(ntObj, false);
    }

    /**
     * Speichert erstmalig oder updatet einen bereits bestehenden NT in der Datenbank.
     * Wird ein NT oder ein RT gespeichert und existiert der Spielpartner bereits in der Datenbank, so wird diese Spielbeziehung mit gespeichert.
     * Speichert man allerdings einen NT oder einen RT, der zwar in der Laufzeit des Programms einen Spielpartner hat, dieser aber noch nicht in der
     * Datenbank existiert, so wird diese Information nicht mit gespeichert und die Datenbank weiß nichts über die Spielbeziehung.
     * Erst, wenn der Partner auch gespeichert wird, wird die Spielbeziehung nachgetragen.
     * Spielbeziehungen müssen somit nicht selbst gespeichert werden, dies wird automatisch erkannt.
     *
     * @param ntObj Der zu speichernde NT
     * @param alsoSavePlayingRTs true=für jeden RT Spielpartner wird die Methode `createOrUpdateRT` aufgerufen; false=RT Spielpartner werden ignoriert
     * @return
     * @throws Exception
     */
    public boolean createOrUpdate(Object ntObj, boolean alsoSavePlayingRTs) throws Exception {
//        Serializer.printAllFields(ntObj);

        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNtRt.class.isAssignableFrom(ntObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen
        Class class_ = ntObj.getClass();
        String className = ntObj.getClass().getSimpleName();
        String classPackage = ntObj.getClass().getCanonicalName();

        // UUID ermitteln
        UUID uuid_;
        try {
            Field f = MetaPersistenceNtRt.class.getField("uuid_");
            f.setAccessible(true);
            uuid_ = (UUID) f.get(ntObj);
        }catch(Exception e){
            // Keine UUID gefunden, was nicht sein darf
            throw e;
        }

        // Eventuell gibt es das Objekt schon in der Datenbank, daher Abfrage starten
//        List<?> allNTs = this.getAllNtByNtEntity("uuid_", uuid_);
//        List<NT> allNt = this.ntRepository.findAllByHash(hash);
        Query query = session.createQuery("select nt from NT as nt where nt.uuid_ = :value ");
        query.setParameter("value", uuid_);
        List<?> allNTs = query.list();

        // Gibt es dieses Objekt in der Datenbank schon?
        scroll.persistence.Model.NT nt;
        if(allNTs.size() == 0){
            // als neue Entität anlegen
            nt = new scroll.persistence.Model.NT();
            nt.name = classPackage;
            nt.uuid_ = uuid_;
        }else{
            // die bereits bestehende Entität nutzen
            nt = (scroll.persistence.Model.NT) allNTs.get(0); // einfach das erste nehmen, hashes sollten nicht öfters existieren

            // Alle Variablen löschen, da diese gleich neu gesetzt werden
            //TODO hohe Laufzeit, sollte man später ändern
            for(Variable var : nt.variables){
                session.delete(var);
            }
        }
        nt.variables = new HashSet<Variable>(); // auch bei bereits bestehenden Entitäten leeren = alles löschen
        session.saveOrUpdate(nt);

        Collection<Field> fields = Serializer.getAllFields(ntObj.getClass());
        for(Field field : fields){
//            String className = field.getDeclaringClass().getSimpleName();
            field.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
            String variableName = field.getName();
            Object variableValue = null;
            try {
                variableValue = field.get(ntObj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            // Die Variable `uuid_` ignorieren wir, da sie auf Ebene der Entity selbst gespeichert werden soll
            if(variableName == "uuid_")
                continue;

            Variable var = new Variable();
            var.entity = nt;
            var.name = variableName;
            var.value = variableValue;
            nt.variables.add(var);

            System.out.println("Variablen Name: " + variableName);

            session.saveOrUpdate(var);
        }

        // Gibt es in der Datenbank RT's, die aktuell zur Laufzeit gespielt werden und somit ein UPDATE für die Spielbeziehung bekommen müssen?
        try{
//            UniversityExample.Person test = new UniversityExample.Person("hallo");
            Method method = ntObj.getClass().getMethod("roles");
            Object test = method.invoke(ntObj);
            System.out.println("aaaaaaaaaaaa = " + test);
        }catch(Exception e){
            throw e;
        }

        // Eigentlichen NT speichern
        session.saveOrUpdate(nt);

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        // Positive Rückgabe
        return true;
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

    public void select(Object nt, String variableName, Object value) throws Exception {
        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNtRt.class.isAssignableFrom(nt.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Entität aus der Datenbank ermitteln
//        List<NT> allNt = this.ntRepository.findAllByHash(hash);
        Query query = session.createQuery("select nt from NT as nt inner join nt.variables as variables " +
                "where variables.name = :name and variables.value = :value ");
        query.setParameter("name", variableName);
        query.setParameter("value", value);
        List<?> allNTs = query.list();
        if(allNTs.size() == 0)
            throw new Exception("Keinen Eintrag gefunden");
        if(allNTs.size() > 1)
            throw new Exception("Zu viele Einträge gefunden, nicht eindeutig genug");

        // Es kann nur noch ein Ergebnis geben
        scroll.persistence.Model.NT selectedNt = (scroll.persistence.Model.NT) allNTs.get(0);
//        selectedNt = (NT) session.merge(selectedNt); // re-attach

        // Nur eine Klassenvariable neu setzen
//        Field field = nt.getClass().getDeclaredField(variableName);
//        field.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
//        field.set(nt, value);

        // Die Variablen der ermittelten Entität aufbereiten
        HashMap<String, Object> variablesSelected = new HashMap<String, Object>();
        for(Variable var : selectedNt.variables){
            variablesSelected.put(var.name, var.value);
        }

        // Alle Klassenvariablen durchgehen und setzen
        Collection<Field> fields = Serializer.getAllFields(nt.getClass());
        for(Field fieldOriginalNt : fields){
//            Field fieldOriginalNt2 = nt.getClass().getDeclaredField(fieldOriginalNt.getName());
//            fieldOriginalNt2.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
//            fieldOriginalNt2.set(nt, fieldOriginalNt.get(fieldOriginalNt));

            // auch `privat` Variablen müssen veränderbar sein
            fieldOriginalNt.setAccessible(true);

            // Das Feld `uuid_` speziell behandeln,
            if(fieldOriginalNt.getName() == "uuid_"){
                fieldOriginalNt.set(nt, selectedNt.uuid_);
                continue;
            }

            // normales setzen eines Attributs
            fieldOriginalNt.set(nt, variablesSelected.get(fieldOriginalNt.getName()));
        }

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

//        // Rückgabe
//        return nt;
    }

}
