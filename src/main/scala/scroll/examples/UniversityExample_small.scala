package scroll.examples

import java.util.Date

import scroll.persistence.Database
import scroll.persistence.Inheritance.{MetaPersistenceCt, MetaPersistenceNt, MetaPersistenceRt}
import scroll.persistence.Util.Serializer

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

    }

    // Anwendung vollständig beenden
    System.exit(0) // anderenfalls läuft der Prozess weiter (danke Spring)
  }
}
