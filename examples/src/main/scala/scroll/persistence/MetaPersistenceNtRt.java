package scroll.persistence;

import java.util.UUID;

public abstract class MetaPersistenceNtRt {

    /**
     * public, da man es mir Reflection eh jederzeit ändern könnte und vor allem, da es so einfacher geht.
     * Zudem gleich mit einem default Wert. Kann später natürlich noch überschrieben werden.
     */
    public UUID uuid_ = UUID.randomUUID();

//    /**
//     * public, da man es mir Reflection eh jederzeit ändern könnte und vor allem, da es so einfacher geht.
//     */
//    public UUID uuid_ = null;

//    public UUID getUuid_(){
//        return this.uuid_;
//    }

}
