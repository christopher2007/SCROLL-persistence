package scroll.examples

import java.util
import java.util.Date

import scroll.persistence.Database
import scroll.persistence.Inheritance.{MetaPersistenceCt, MetaPersistenceNt, MetaPersistenceRt}
import scroll.persistence.Util.{ReturnRT, Serializer}

object UniversityExample_small {

  // ===== NTs

  class Person(var name: String) extends MetaPersistenceNt {
    var birthday: Date = _

    def talk(): Unit = {
      println("----- I am a person")
    }
  }

  // ===== CTs mit deren enthaltenen RTs

  class University extends MetaPersistenceCt {
    var name: String = _
    var country: String = _
    var city: String = _

    class Student extends MetaPersistenceRt {
      var matNr: Int = _

      def talk(): Unit = {
        println("----- I am a student")
      }
    }

    class Professor extends MetaPersistenceRt {
      var graduation: String = _

      def teach(student: Person): Unit = student match {
        case s if (+s).isPlaying[Student] =>
          val studentName: String = (+student).name
          println("----- Teaching: " + studentName)
        case _ => println("Nope! I am only teaching students!")
      }

      def talk(): Unit = {
        println("----- I am a professor")
      }
    }

    class Mailbox extends MetaPersistenceRt {
      var newMessages: Boolean = _

      def talk(): Unit = {
        println("----- I am a mailbox")
      }
    }

    class Classroom(var seatCount: Int) extends MetaPersistenceRt {
      def talk(): Unit = {
        println("----- I am a classroom")
      }
    }
  }

  // ===== Ein Szenario auf Instanzebene erzeugen und damit spielen

  def main(args: Array[String]): Unit = {
    println("===== START =====")

    // einen CT erzeugen
    val uni = new University {
      // In dem CT Dinge tun ...

      // NT create
      val hans = new Person("Hans Günter")
      Database.nt.createOrUpdate(hans)

      // NT select
      var hansSelectList: util.List[Person] = Database.nt.select(
        classOf[Person], "name", "Hans Günter").asInstanceOf[util.List[Person]]
      println("----- Anzahl der gefundenen NTs (wir erwarten 1): " + hansSelectList.size())

      // RT create
      val studentHans = new Student
      studentHans.matNr = 123
      hans play studentHans
      // Speichert Spielpartner und enthaltenen CT direkt mit, da die letzten beiden Übergabeparameter so gesetzt sind
      Database.rt.createOrUpdate(studentHans, true, true)

      // RT select
      var studentHansSelectList: util.List[ReturnRT] = Database.rt.select(
        classOf[Student], "matNr", 123, true, this) // Zurück kommt immer eine Liste von `ReturnRT`. Enthalten auf jeden falld er RT und ggf alle Spieler, falls angefragt
      println("----- Anzahl der gefundenen RTs (wir erwarten 1): " + studentHansSelectList.size())
      println("----- Anzahl der gefundenen RTs - Spieler (wir erwarten 1): " + studentHansSelectList.get(0).players.size())
      println("----- Anzahl der gefundenen RTs - Name des Spielers (wir erwarten 'Hans Günter'): " + studentHansSelectList.get(0).players.get(0).asInstanceOf[Person].name)

      // NT delete
      if(Database.nt.delete(hans))
        println("----- erfolgreich einen Eintrag gelöscht")
      else
        println("----- nichts gelöscht")

      // NT select
      var hansSelectList3: util.List[Person] = Database.nt.select(
        classOf[Person], "name", "Hans Jürgen").asInstanceOf[util.List[Person]]
      println("----- Anzahl der gefundenen NTs (wir erwarten jetzt nach dem Löschen 0): " + hansSelectList3.size())
    }

    // Anwendung vollständig beenden
    System.exit(0) // anderenfalls läuft der Prozess weiter (danke Spring)
  }
}
