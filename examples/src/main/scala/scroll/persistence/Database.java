package scroll.persistence;

import org.springframework.stereotype.Service;
import scroll.persistence.Inheritance.MetaPersistenceNtRt;
import scroll.persistence.Util.Serializer;

@Service
public class Database {

    // Singelton Pattern
    private static Database instance;
    private Database () {}
    public static Database getInstance () {
        if (Database.instance == null)
            Database.instance = new Database ();
        return Database.instance;
    }

    /**
     * @return Instanc für die Arbeit mit NT's.
     */
    public NT nt(){
        return NT.getInstance();
    }

    /**
     * @return Instanc für die Arbeit mit RT's.
     */
    public RT rt(){
        return RT.getInstance();
    }

//    /**
//     * @return Instanc für die Arbeit mit CT's.
//     */
//    public CT ct(){
//        return CT.getInstance();
//    }

}
