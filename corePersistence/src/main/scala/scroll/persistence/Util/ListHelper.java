package scroll.persistence.Util;

import java.util.ArrayList;

public class ListHelper {

    /**
     * Hilfsfunktion um Typsichere Liste von unbekannten Klassen in der Laufzeit zu erzeugen.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> listOf(Class<T> clazz)
    {
        return new ArrayList<T>();
    }

}
