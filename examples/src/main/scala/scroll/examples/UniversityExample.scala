package scroll.examples

import com.github.fluent.hibernate.cfg.scanner.EntityScanner
import scroll.internal.Compartment
import org.hibernate._
import javax.persistence._
import scroll.persistence.{Database, MetaPersistenceCt, MetaPersistenceNtRt}
import java.util

import scroll.examples.UniversityExample.Person

object UniversityExample {

//  class University extends MetaPersistenceCt {
  class University extends Compartment {
    var country = "Deutschland"

    class Student extends MetaPersistenceNtRt {
      var foo = 1
      def talk(): Unit = {
        println("I am a student")
      }
    }

    class Professor extends MetaPersistenceNtRt {
      def teach(student: Person): Unit = student match {
        case s if (+s).isPlaying[Student] =>
          val studentName: String = (+student).name
          println("Teaching: " + studentName)
        case _ => println("Nope! I am only teaching students!")
      }

      def talk(): Unit = {
        println("I am a professor")
      }
    }

  }

  class Person(var name: String) extends MetaPersistenceNtRt {
    def talk(): Unit = {
      println("I am a person")
    }
  }

  def main(args: Array[String]): Unit = {
    println("===== START =====");

    val test = new University {
      val uwe = new Person("uwe")





      // === NT

      // insert
      val hans = new Person("hans")
      Database.getInstance().createOrUpdateNT(hans)

      // update
      hans.name = "hans2"
      Database.getInstance().createOrUpdateNT(hans)

      // select
//      val hansSelect = Database.getInstance().selectNt((new Person("a")).getClass, "name", "hans2").asInstanceOf[Person]
//      val hansSelect = Database.getInstance().selectNt(new Person("a"), "name", "hans2").asInstanceOf[Person]
      var hansSelect = new Person("a")
      Database.getInstance().selectNt(hansSelect, "name", "hans2")
//      Database.getInstance().selectNt("scroll.examples.UniversityExample.Person", "name", "hans2")
//      val hansSelect2 = Database.getInstance().selectNt("name", "hans2").asInstanceOf[Person]

      // update 2: Entitäten, die abgefragt wurden, müssen immer noch wie das originale Objekt behandelt werden und dürfen kein INSERT triggern, sondern ein UPDATE
      hansSelect.name = "hans3"
      Database.getInstance().createOrUpdateNT(hansSelect)

      // select 2: Wenn kein Eintrag gefunden wird
      var hansSelect2 = new Person("a")
      try{
        Database.getInstance().selectNt(hansSelect2, "name", "hans4")
      }catch{
        case _: Throwable  => println("Kein Eintrag gefunden")
      }

      // delete
      //TODO




      // === Rollen ermitteln
//      hansSelect.roles()
//      allPlayers()



      // === RT

      // insert
      val student2 = new Student
      val student3 = new Student
      hans play student2
      hans play student3
      uwe play student2
      println("hans.roles() = " + hans.roles())
      println("uwe.roles() = " + uwe.roles())
      println("allPlayers = " + allPlayers)
      +hans talk()
      Database.getInstance().createOrUpdateRT(hans)
      Database.getInstance().createOrUpdateRT(student2)



      hans.talk()

      val student = new Student
      println("Player equals core: " + ((hans play student) == hans))
      +hans talk()

      println((+student).name)
      println("Role core equals core: " + (+student == hans))

      uwe play new Professor
      +uwe talk()
      println("Core equals core playing a role: " + (+uwe == uwe))

      +uwe teach hans
    }

    System.exit(0) // anderenfalls beendet die Anwendung nicht
  }
}
