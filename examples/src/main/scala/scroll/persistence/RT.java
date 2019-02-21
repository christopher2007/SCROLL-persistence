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

public class RT {

    // Singelton Pattern
    private static RT instance;
    private RT() {}
    // protected, nur für aktuelles Package
    protected static RT getInstance () {
        if (RT.instance == null)
            RT.instance = new RT();
        return RT.instance;
    }

    public boolean createOrUpdate(Object rtObj) throws Exception {
//        Serializer.printAllFields(rtObj);

        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNtRt.class.isAssignableFrom(rtObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        System.out.println("--------------------------------");

        Serializer.printAllFields(rtObj);

        System.out.println("--------------------------------");

        // Positive Rückgabe
        return true;
    }

}
