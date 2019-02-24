package scroll.persistence.Util;

import org.hibernate.Session;
import scroll.persistence.Model.Entity;
import scroll.persistence.Model.Variable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

public class DatabaseHelper {

    /**
     * Ruft die gleichnamige Methode der Klasse auf und setzt dabei den Übergabeparameter `variableExceptions` auf ein leeres String Array.
     *
     * @param obj das originale Objekt des rollenbasierten Kontextes, dessen Variablen ermittelt werden sollen.
     * @param e Die persistierungs Entity, der die Variablen angehängt werden sollen
     * @param session Die aktuelle Datenbank Session, auf der gearbeitet werden soll
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void addAllVariablesToEntity(Object obj, Entity e, Session session)
            throws IllegalArgumentException, IllegalAccessException {
        String[] variableExceptions = {};
        addAllVariablesToEntity(obj, e, session, variableExceptions);
    }

    /**
     * Ermittelt aus einem Objekt aus dem rollenbasierten Kontext alle Variablen und erstellt diese als Abbild im Persistierungs Kontext in
     * Hibernate. Hängt diese dann der übergebenen Hibernate Entity an.
     * (Keine Rückgabe, da das übergebene Objekt erweitert wird und danach erweitert auch in der Parent Methode vorliegt.)
     *
     * Immer ignoriert wird die Variable `uuid_`, da diese nicht als Variable abgespeichert wird, sondern in der Hauptentität selbst.
     * (Siehe UML Klassendiagramm.)
     *
     * @param obj das originale Objekt des rollenbasierten Kontextes, dessen Variablen ermittelt werden sollen.
     * @param e Die persistierungs Entity, der die Variablen angehängt werden sollen
     * @param session Die aktuelle Datenbank Session, auf der gearbeitet werden soll
     * @param variableExceptions Ein Array aus Namen der Variablen, die ignoriert werden sollen (werden dann nicht persistiert)
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void addAllVariablesToEntity(Object obj, Entity e, Session session, String[] variableExceptions)
            throws IllegalArgumentException, IllegalAccessException {
        // Über alle Variablen des übergebenen RT iterieren und diese in der Datenbank speichern
        Collection<Field> fields = Serializer.getAllFields(obj.getClass());
        for(Field field : fields){
            field.setAccessible(true); // auch `privat` Variablen müssen lesbar und schreibbar sein
            String variableName = field.getName();
            Object variableValue = null;
            variableValue = field.get(obj);

            // Die Variable `uuid_` ignorieren wir, da sie auf Ebene der NT-Entity selbst gespeichert werden soll
            if(variableName == "uuid_")
                continue;

            // Die aktuelle Variable soll auch ignoriert werden, wenn sie in der Liste der zu ignorierenden Variablen steht (Übergabeparameter)
            if(Arrays.asList(variableExceptions).contains(variableName))
//            if(Arrays.stream(variableExceptions).anyMatch(variableName::equals))
                continue;

            // Eine Variablen Entity erstellen
            Variable var = new Variable();
            var.entity = e;
            var.name = variableName;
            var.value = variableValue;
            e.variables.add(var);

//            System.out.println("Variablen Name: " + variableName);

            // Variablen Entity speichern
            session.saveOrUpdate(var);
        }
    }

//    public static boolean checkMetaTypeString(Object o, String metaTypeToTest){
//        try {
////            Field f = o.getClass().getField("metaType_"); // Nur public und nur direkte Variablen
//            Field f = o.getClass().getDeclaredField("metaType_"); // auch private und protected und auch nach Vererbungen
//            f.setAccessible(true);
//            String metaType_ = (String) f.get(o);
//            if(metaType_ == metaTypeToTest)
//                return true;
//        }catch(Exception e){ }
//        return false;
//    }

}
