package scroll.persistence.Inheritance

import java.util.UUID

import scroll.internal.Compartment

trait MetaPersistenceCt
  extends Compartment with _meta {

  //TODO sollte direkt in `Compartment` übernommen werden.
  /**
    * Gibt alle Spielpartner eines RT zurück.
    *
    * @param hash Der Hash-Wert der RT, dessen Spielpartner zurück gegeben werden sollen.
    * @return
    */
  def getRolesFromHash(hash: Int): Seq[AnyRef] = {
//    plays.allPlayers.foreach { p =>
    this.allPlayers.foreach { p =>
      if (p.hashCode() == hash)
      {
        return p.predecessors()
      }
    }
    null
  }

}
