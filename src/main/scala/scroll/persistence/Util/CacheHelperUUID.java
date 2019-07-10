package scroll.persistence.Util;

import scroll.persistence.Inheritance._meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CacheHelperUUID {

    // Der Cache selbst
    private HashMap<UUID, _meta> cache = new HashMap<>();

    /**
     * Ist die UUID des übergebenen Objektes noch nicht im Cache enthalten, wird das neue Objekt zum Cache hinzugefügt.
     * Andernfalls passiert nichts.
     *
     * @param obj das hinzuzufügende Objekt
     */
    public void put(_meta obj){
        if(!cache.containsKey(obj.uuid_()))
            cache.put(obj.uuid_(), obj);
    }

    /**
     * Existiert die übergebene UUID im Cache, wird das dazugehörige Objekt zurück gegeben.
     * Existiert es nicht, wird ein Fehler geworfen.
     *
     * @param uuid Die UUID des gewünschten Objektes.
     * @return Das Objekt, welches zu der UUID gehört.
     * @throws Exception Wenn die UUID im Cache nicht existiert.
     */
    public _meta get(UUID uuid) throws Exception {
        if(!cache.containsKey(uuid))
            throw new Exception("UUID im Cache nicht gefunden");
        return cache.get(uuid);
    }

    /**
     * Existiert die UUID des übergebenen Objektes im Cache, so wird das dazugehörige Objekt aus dem Cache zurück gegeben.
     * Existiert die UUID des übergebenen Objektes nicht im Cache, so wird das übergebene ganze Objekt im Cache abgespeichert und auch gleich
     * zurück gegeben.
     * (Ermöglicht es, gleiche UUIDs zu cachen, ohne semantisch gleiche Objekte mehrfach im Arbeitsspeicher zu erzeugen. Echte Gleichheit hier
     * auch durch gleiche HashWerte gezeigt.)
     *
     * @param obj Das Objekt, dessen UUID wie oben beschrieben geprüft werden soll.
     * @return Das gesuchte Objekt.
     */
    public _meta get(_meta obj){
        // Key existiert im Cache schon, direkt zurück geben
        if(cache.containsKey(obj.uuid_()))
            return cache.get(obj.uuid_());

        // Key existiert im Cache noch nicht, daher den neuen Wert eintragen und diesen gleich zurück geben
        this.put(obj);
        return obj;
    }

    /**
     * Prüft, ob die übergebene UUID im Cache bereits hinterlegt ist.
     *
     * @param uuid Diese UUID soll überprüft werden.
     * @return true=UUID ist im Cache vorhanden; false=nicht
     */
    public boolean containsKey(UUID uuid){
        return cache.containsKey(uuid);
    }

}
