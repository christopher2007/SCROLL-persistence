package scroll.persistence.Util;

import java.lang.reflect.Field;
import java.util.UUID;

public class HelperGetUUID {

    /**
     * Ermittle die `uuid_` Variable eines beliebigen übergebenen Objektes.
     *
     * @param o Das Objekt, von dem die Variable ermittelt werden soll
     * @return Die ermittelte UUID, wenn kein Fehler geworfen wurde und die Variable im Objekt wirklich existiert
     * @throws Exception wenn das Objekt keine Variable mit dem Namen `uuid_` besitzt.
     */
    public static UUID getUUID(Object o) throws NoSuchFieldException, IllegalAccessException {
        // UUID ermitteln
        UUID uuid_;
        try {
//            Field f = o.getClass().getField("uuid_"); // Nur public und nur direkte Variablen
            Field f = o.getClass().getDeclaredField("uuid_"); // auch private und protected und auch nach Vererbungen
            f.setAccessible(true);
            uuid_ = (UUID) f.get(o);
        }catch(Exception e){
            // Keine UUID gefunden, was nicht sein darf
            throw e;
        }
        return uuid_;
    }

}
