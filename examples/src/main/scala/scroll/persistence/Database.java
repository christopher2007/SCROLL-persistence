package scroll.persistence;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scroll.persistence.Model.NTRepository;
import scroll.persistence.Model.NT;
import scroll.persistence.Model.Variable;
import scroll.persistence.Util.Serializer;
import scroll.persistence.Util.SessionFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class Database {

    @Autowired
    private NTRepository ntRepository;

    // Singelton Pattern
    private static Database instance;
    private Database () {}
    public static Database getInstance () {
        if (Database.instance == null)
            Database.instance = new Database ();
        return Database.instance;
    }

    public boolean createOrUpdateNT(Object ntObj) throws IllegalAccessException {
//        Serializer.printAllFields(ntObj);

        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

        // Klassen-Spezifische Informationen
        Class class_ = ntObj.getClass();
        String className = ntObj.getClass().getSimpleName();
        String classPackage = ntObj.getClass().getCanonicalName();

        // Objekt-Spezifische Informationen
        int hash = ntObj.hashCode();

        // Gibt es dieses Objekt in der Datenbank schon?
        List<?> allNTs = this.getAllNtByNtEntity("hash", hash);
        NT nt;
        if(allNTs.size() == 0){
            // als neue Entität anlegen
            nt = new NT();
            nt.name = classPackage;
            nt.hash = hash;
        }else{
            // die bereits bestehende Entität nutzen
            nt = (NT) allNTs.get(0); // einfach das erste nehmen, hashes sollten nicht öfters existieren

            // Alle Variablen löschen, da diese gleich neu gesetzt werden
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

            Variable var = new Variable();
            var.entity = nt;
            var.name = variableName;
            var.value = variableValue;
            nt.variables.add(var);

            session.saveOrUpdate(var);
        }

        session.saveOrUpdate(nt);

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        return false;
    }

    private List<?> getAllNtByNtEntity(String variableName, Object value){
        // Session und Transaktion ermitteln bzw. initialisieren
        Session session = SessionFactory.getNewOrOpenSession();
        SessionFactory.openTransaction();

//        List<NT> allNt = this.ntRepository.findAllByHash(hash);
        Query query = session.createQuery("select nt from NT as nt where nt."+variableName+" = :value ");
        query.setParameter("value", value);
        List<?> list = query.list();

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

        return list;
    }

    public void selectNt(Object nt, String variableName, Object value) throws Exception {
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
        NT selectedNt = (NT) allNTs.get(0);
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

            fieldOriginalNt.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
            fieldOriginalNt.set(nt, variablesSelected.get(fieldOriginalNt.getName()));
        }

        // Den Hashcode des übergebenen NTs neu setzen, damit das Objekt gleich dem alten ist und nicht als neues gild
        //TODO

        // Transaktion und Session schließen bzw. committen
        SessionFactory.closeTransaction();
//        session.close();

//        // Rückgabe
//        return nt;
    }

}
