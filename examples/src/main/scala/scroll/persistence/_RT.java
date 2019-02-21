package scroll.persistence;

import scroll.internal.Compartment;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Util.Serializer;

import java.lang.reflect.Field;

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

    public boolean createOrUpdate(Object rtObj) throws Exception {
//        Serializer.printAllFields(rtObj);

        // Das übergebene Objekt muss von einem der Metaklassen erweitert worden sein
        if(!MetaPersistenceNt.class.isAssignableFrom(rtObj.getClass()))
            throw new Exception("Das übergebene Objekt erbt nicht von einer Metaklasse der Persistierung.");

        System.out.println("--------------------------------");

        Serializer.printAllFields(rtObj);

        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

//        IPlayer test = (IPlayer) rtObj;
//        System.out.println(test.roles());

//        Compartment.Player test = (Compartment.Player) rtObj;
//        System.out.println(test.roles());

//        System.out.println(test.getCompartment().plays());
//        System.out.println(test.getCompartment().allPlayers());

//        Method method = rtObj.getClass().getMethod("roles");
//        Object test = method.invoke(rtObj);
//        System.out.println("aaaaaaaaaaaa = " + test);

        Class<?> c = rtObj.getClass();
        Field f = c.getDeclaredField("$outer");
        f.setAccessible(true);
//        String valueOfMyColor = (String) f.get(rtObj);
        Compartment compartment = (Compartment) f.get(rtObj);
        System.out.println(compartment.allPlayers());

        System.out.println("--------------------------------");

        // Positive Rückgabe
        return true;
    }

}
