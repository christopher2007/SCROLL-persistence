package scroll.persistence.tests.ressources

import java.util
import java.util.{ArrayList, Date, List}

import scroll.persistence.Database
import scroll.persistence.Inheritance.{MetaPersistenceCt, MetaPersistenceNt, MetaPersistenceRt}
import scroll.persistence.Util.Serializer

/**
  * Eine 1:1 Kopie des `UniversityExample_large`, nur:
  * - ohne die `def main(args: Array[String]): Unit = { }` und
  * - stattdessen einige Instanzen als Beispiele erzeugt
  *
  * So kann das echte Universitätsbeispiel geändert oder gar gelöscht werden, ohne dass die Tests davon beeinflusst werden.
  * Zudem sind somit immer gleiche Instanzen der Testklassen verfügbar, ohne diese vor jedem Test manuell neu erstellen zu müssen.
  */
object UniversityExample_large_copy {

  // ===== NTs

  class Person(var name: String) extends MetaPersistenceNt {
    var birthday: Date = _

    def talk(): Unit = {
      println("----- I am a person")
    }
  }

  class Animal(var name: String) extends MetaPersistenceNt {
    var birthday: Date = _
    var race: String = _

    def talk(): Unit = {
      println("----- I am an animal")
    }
  }

  class Furniture extends MetaPersistenceNt {
    var color: String = _
    var material: String = _

    def talk(): Unit = {
      println("----- I am a furniture")
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

  class Room(var size: Float) extends MetaPersistenceCt {
    var windowCount: Int = _

    class Chair extends MetaPersistenceRt {
      var footCount: Int = _

      def talk(): Unit = {
        println("----- I am a chair")
      }
    }

    class Table extends MetaPersistenceRt {
      var footCount: Int = _
      var form: String = _

      def talk(): Unit = {
        println("----- I am a table")
      }
    }
  }

  // === Hilfsklasse für globale Ansicht

  /**
    * Um eine globale Ansicht der Instanzebene zu besitzen, da SCROLL selbst dies nicht liefert.
    * Muss also händisch bei dem erstellen der Testinstanzen redundant befüllt werden.
    */
  class Concepts {
    private[ressources] val nts = new util.ArrayList[MetaPersistenceNt]
    private[ressources] val cts = new util.ArrayList[MetaPersistenceCt]
    private[ressources] val rts = new util.ArrayList[MetaPersistenceRt]
  }

  // === Instanzen für Testklassen erzeugen

  /**
    * Erzeugt ein Beispielszenario auf Instanzebene:
    *
    * NT:
    * - Person per1
    * - Person per2
    *
    * RT:
    * - Student stu1
    * - Student stu2
    * - Professor prof1
    *
    * Played-By:
    * - per1 play stu1
    * - per2 play stu2
    * - per2 play prof1
    */
  def getInstanceExample(): Concepts = {
    // Rückgabe Objekt
    val concepts = new Concepts

    // NTs erzeugen
    val per1 = new Person("per1")
    concepts.nts.add(per1)
    val per2 = new Person("per2")
    concepts.nts.add(per2)
//    val per3 = new Person("per3")
//    concepts.nts.add(per3)

    // CTs erzeugen
    val u1 = new University {

      // RTs erzeugen
      val stu1 = new Student
      stu1.matNr = 123
      concepts.rts.add(stu1)
      val stu2 = new Student
      concepts.rts.add(stu2)
      val prof1 = new Professor
      concepts.rts.add(prof1)

      // Played-By Beziehungen erzeugen
      per1 play stu1
      per2 play stu2
      per2 play prof1
    }
    concepts.cts.add(u1)

    // Rückgabe
    return concepts
  }

}
