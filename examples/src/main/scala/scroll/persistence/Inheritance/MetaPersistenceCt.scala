package scroll.persistence.Inheritance

import java.util.UUID

import scroll.internal.Compartment

trait MetaPersistenceCt
  extends Compartment {

  /**
    * public, da man es mir Reflection eh jederzeit ändern könnte und vor allem, da es so einfacher geht.
    * Zudem gleich mit einem default Wert. Kann später natürlich noch überschrieben werden.
    */
  var uuid_ : UUID = UUID.randomUUID

}
