package scroll.persistence;

import org.springframework.stereotype.Service;

@Service
public class Database {

    /**
     * @return Instanz für die Arbeit mit NT's.
     */
    public static _NT nt(){
        return _NT.getInstance();
    }

    /**
     * @return Instanz für die Arbeit mit RT's.
     */
    public static _RT rt(){
        return _RT.getInstance();
    }

    /**
     * @return Instanz für die Arbeit mit CT's.
     */
    public static _CT ct(){
        return _CT.getInstance();
    }

    /**
     * @return Instanz für die Arbeit mittels Massenoperationen.
     */
    public static _groundOperations groundOperations(){
        return _groundOperations.getInstance();
    }

}
