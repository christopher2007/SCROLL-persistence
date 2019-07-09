package scroll.persistence.Inheritance

import java.util.UUID

/**
  * Alle Variablen public, da man es mir Reflection eh jederzeit ändern könnte und vor allem, da es so einfacher geht.
  * (Zudem gleich mit einem default Wert. Kann später natürlich noch überschrieben werden.)
  */
trait _meta {

  /**
    * Die UUID, um Objekte mit UNIQUE Identifiern eindeutig zuordnen zu können.
    */
  var uuid_ : UUID = UUID.randomUUID

//  /**
//    * Die erbenden Klassen müssen den Inhalt füllen, nur Zulässig: "nt", "rt" oder "ct"
//    */
//  var metaType_ : String

}
