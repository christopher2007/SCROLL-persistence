package scroll.persistence.Util;

import org.hibernate.Session;
import scroll.persistence.Model.Entity;
import scroll.persistence.Model.Variable;

import java.lang.reflect.Field;
import java.util.Collection;

public class DatabaseHelper {

    public static void addAllVariablesToEntity(Object obj, Entity e, Session session) throws IllegalArgumentException, IllegalAccessException {
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
}
