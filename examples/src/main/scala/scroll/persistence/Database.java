package scroll.persistence;

import org.springframework.stereotype.Service;

@Service
public class Database {

    /**
     * @return Instanc für die Arbeit mit NT's.
     */
    public static _NT nt(){
        return _NT.getInstance();
    }

    /**
     * @return Instanc für die Arbeit mit RT's.
     */
    public static _RT rt(){
        return _RT.getInstance();
    }

    /**
     * @return Instanc für die Arbeit mit CT's.
     */
    public static _CT ct(){
        return _CT.getInstance();
    }

}
