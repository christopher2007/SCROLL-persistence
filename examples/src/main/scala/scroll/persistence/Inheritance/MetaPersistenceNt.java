package scroll.persistence.Inheritance;

import java.util.UUID;

public abstract class MetaPersistenceNt {

    /**
     * public, da man es mir Reflection eh jederzeit ändern könnte und vor allem, da es so einfacher geht.
     * Zudem gleich mit einem default Wert. Kann später natürlich noch überschrieben werden.
     */
    public UUID uuid_ = UUID.randomUUID();

}
