package scroll.persistence.Util;

import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import scroll.persistence.Model.Entity;
import scroll.persistence.Model.Variable;

import java.lang.reflect.Field;
import java.util.*;

public class Serializer {

    public static Collection<Field> getAllFields(Class<?> type) {
        TreeSet<Field> fields = new TreeSet<Field>(
                new Comparator<Field>() {
                    @Override
                    public int compare(Field o1, Field o2) {
                        int res = o1.getName().compareTo(o2.getName());
                        if (0 != res) {
                            return res;
                        }
                        res = o1.getDeclaringClass().getSimpleName().compareTo(o2.getDeclaringClass().getSimpleName());
                        if (0 != res) {
                            return res;
                        }
                        res = o1.getDeclaringClass().getName().compareTo(o2.getDeclaringClass().getName());
                        return res;
                    }
                });
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static void printAllFields(Object obj) {
        for (Field field : getAllFields(BasicClassInformation.getClass(obj))) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
//            System.out.printf("%s %s.%s = %s;\n", value==null?" ":"*", field.getDeclaringClass().getSimpleName(), name, value);
            System.out.printf("%s %s.%s = %s;\n", value==null?" ":"*", field.getDeclaringClass().getName(), name, value);
        }
    }

    /**
     * Aus der Entität aus der Datenbank eine Instanz der eigentlich echten Anwendung machen.
     *
     * @param e Die Entität aus der Datenbank, die in eine Instanz der realen Anwendung umgewandelt werden soll.
     * @param aimClass Die Ziel-Klasse. Hiervon soll eine neue Instanz mit den Daten aus der Datenbank erzeugt werden.
     * @return Eine Instanz des Objektes in der ursprünglichen Form der Klasse, die in der realen Anwendung genutzt wird.
     * @throws Exception
     */
    public static Object getInstanceOfEntity(Entity e, Class aimClass) throws Exception {
        // Eine Instanz der Zielklasse erzeugen
        Objenesis o = new ObjenesisStd(false); // cache disabled
        Object newObj = o.newInstance(aimClass);

        // Nur eine Klassenvariable neu setzen
//        Field field = newObj.getClass().getDeclaredField(variableName);
//        field.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
//        field.set(newObj, value);

        // Die Variablen der ermittelten Entität aufbereiten
        HashMap<String, Object> variablesSelected = new HashMap<String, Object>();
        for(Variable var : e.variables){
            variablesSelected.put(var.name, var.value);
        }

        // Alle Klassenvariablen durchgehen und setzen
        Collection<Field> fields = Serializer.getAllFields(BasicClassInformation.getClass(newObj));
        for(Field fieldOriginalNt : fields){
//            Field fieldOriginalNt2 = newObj.getClass().getDeclaredField(fieldOriginalNt.getName());
//            fieldOriginalNt2.setAccessible(true); // auch `privat` Variablen müssen veränderbar sein
//            fieldOriginalNt2.set(newObj, fieldOriginalNt.get(fieldOriginalNt));

            // auch `privat` Variablen müssen veränderbar sein
            fieldOriginalNt.setAccessible(true);

            // Das Feld `uuid_` speziell behandeln, da es als Variablen-Entity nicht existiert, sondern im NT-Entity gespeichert wurde
            if(fieldOriginalNt.getName() == "uuid_"){
                fieldOriginalNt.set(newObj, e.uuid_);
                continue;
            }

            // normales setzen eines Attributs
            fieldOriginalNt.set(newObj, variablesSelected.get(fieldOriginalNt.getName()));
        }

        // Rückgabe
        return newObj;
    }

}
