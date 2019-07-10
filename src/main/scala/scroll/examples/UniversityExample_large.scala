package scroll.examples

import scroll.internal.Compartment
import org.hibernate._
import javax.persistence._
import scroll.persistence.Database
import java.util
import java.util.List
import java.util.Date
import scroll.examples.UniversityExample_large.Person
import scroll.persistence.Inheritance.{MetaPersistenceCt, MetaPersistenceNt, MetaPersistenceRt}
import scroll.persistence.Util.{ReturnRT, Serializer}

object UniversityExample_large {

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

  // ===== Ein Szenario auf Instanzebene erzeugen und damit spielen

  def main(args: Array[String]): Unit = {
    println("===== START =====")

    // einen CT erzeugen
    val uni = new University {
      // In dem CT Dinge tun ...


      // Eigene Klassenvariablen der Univresität
      this.country = "Deutschland"



      // === NT
      println("----- NT")

      // create
      val hans = new Person("Hans Günter")
      Database.nt.createOrUpdate(hans)

      // update
      hans.name = "Hans Jürgen"
      Database.nt.createOrUpdate(hans)

      // select
      var hansSelectList: util.List[Person] = Database.nt.select(
        classOf[Person], "name", "Hans Jürgen").asInstanceOf[util.List[Person]]
      println("----- Anzahl der gefundenen NTs: " + hansSelectList.size())

      // update 2: Entitäten, die abgefragt wurden, müssen immer noch wie das originale Objekt behandelt werden und dürfen kein INSERT triggern, sondern ein UPDATE
      var hansSelect = hansSelectList.get(0)
      // ... Hier mögliche Änderungen, hier aber absichtlich nichts zu ändern, um zu zeigen, dass ein UPDATE und kein INSERT ausgeführt wird und man später immer noch nur einen Eintrag findet
      Database.nt.createOrUpdate(hansSelect)

      // select 2
      var hansSelectList2: util.List[Person] = Database.nt.select(
        classOf[Person], "name", "Hans Jürgen").asInstanceOf[util.List[Person]]
      println("----- Anzahl der gefundenen NTs 2: " + hansSelectList2.size())

      // delete
      if(Database.nt.delete(hansSelect))
        println("----- erfolgreich gelöscht")
      else
        println("----- nichts gelöscht")

      // select 3
      var hansSelectList3: util.List[Person] = Database.nt.select(
        classOf[Person], "name", "Hans Jürgen").asInstanceOf[util.List[Person]]
      println("----- Anzahl der gefundenen NTs 3: " + hansSelectList3.size())



      // === NT playing RT
      println("----- NT playing RT")

      // create
      val hansPlaying = new Person("Hans Günter")
      val studentHans = new Student
      studentHans.matNr = 12345
      hansPlaying play studentHans
      println("----- hansPlaying.roles() = " + hansPlaying.roles())
      println("----- allPlayers = " + allPlayers)
      Database.ct.createOrUpdate(this) // CT muss in der Datenbank existieren, wenn man einen RT speichern möchte
      Database.nt.createOrUpdate(hansPlaying) // NT muss in der Datenbank existieren, wenn man einen RT speichern möchte
      Database.rt.createOrUpdate(studentHans)
      // Alternativ kann der INSERT Befehl für einen RT auch die Spieler und umliegenden CT mit speichern. Kostet aber extra Laufzeit. Nur nutzen wenn wirklich benötigt
      // Database.rt.createOrUpdate(studentHans, true, true)

      // update
      studentHans.matNr = 6789
      Database.rt.createOrUpdate(studentHans)

      // select
      var studentHansSelectList: util.List[ReturnRT] = Database.rt.select(
        classOf[Student], "matNr", 6789, true, this) // Zurück kommt immer eine Liste von `ReturnRT`. Enthalten auf jeden falld er RT und ggf alle Spieler, falls angefragt
      println("----- Anzahl der gefundenen RTs: " + studentHansSelectList.size())

      // update 2: Entitäten, die abgefragt wurden, müssen immer noch wie das originale Objekt behandelt werden und dürfen kein INSERT triggern, sondern ein UPDATE
      var studentHansSelect = studentHansSelectList.get(0).rt.asInstanceOf[Student] // muss noch gecastet werden
      // ... Hier mögliche Änderungen, hier aber absichtlich nichts zu ändern, um zu zeigen, dass ein UPDATE und kein INSERT ausgeführt wird und man später immer noch nur einen Eintrag findet
      Database.nt.createOrUpdate(hansSelect)

      // select 2
      var studentHansSelectList2: util.List[ReturnRT] = Database.rt.select(
        classOf[Student], "matNr", 6789, true, this) // Zurück kommt immer eine Liste von `ReturnRT`. Enthalten auf jeden falld er RT und ggf alle Spieler, falls angefragt
      println("----- Anzahl der gefundenen RTs 2: " + studentHansSelectList2.size())
      println("----- Anzahl der gefundenen RTs 2 - Spieler: " + studentHansSelectList2.get(0).players.size())
      println("----- Anzahl der gefundenen RTs 2 - Name des Spielers: " + studentHansSelectList2.get(0).players.get(0).asInstanceOf[Person].name)

      // delete
      if(Database.rt.delete(studentHansSelect))
        println("----- erfolgreich gelöscht")
      else
        println("----- nichts gelöscht")

      // select 3
      var studentHansSelectList3: util.List[ReturnRT] = Database.rt.select(
        classOf[Student], "matNr", 6789, true, this) // Zurück kommt immer eine Liste von `ReturnRT`. Enthalten auf jeden falld er RT und ggf alle Spieler, falls angefragt
      println("----- Anzahl der gefundenen RTs 3: " + studentHansSelectList3.size())



      // === CT playing RT
      println("----- CT playing RT")

      // create
      // exakt analog zu `NT playing RT`
      val uni2 = new University{} // hier wird nun ein CT in einem CT erzeugt, macht semantisch keinen Sinn, aber im Zuge des Beispiels egal
      val studentUni = new Student
      uni2 play studentUni
      Database.rt.createOrUpdate(studentUni, true, true) // der RT speichert seine Spieler hier mit, was danna auch der CT ist. Direktes Speichern von CT kommt unten ausführlicher



      // === CT
      // Überwiegend analog zu NT bzw. RT playing NT. Hier nur mit CT statt NT (beides aber rigide Typen)
      println("----- CT")

      // create
      this.country = "Deutschland"
      Database.ct.createOrUpdate(this)

      // delete
//      Database.ct.delete(this) // geht hier nicht, da im CT noch RTs enthalten sind. Diese müssten zuerst gelöscht werden



      // === Über alle Spieler iterieren und diese in der Konsole dumpen
      //Database.rt.select(classOf[Student], "matNr", 6789, true, this)
      Serializer.printAllFields(this.allPlayers)
      for(obj <- this.allPlayers){
        println("=============================")
        for(inner <- obj.roles()){
          println("---------------------")
          Serializer.printAllFields(inner)
        }
      }

    }

    // Anwendung vollständig beenden
    System.exit(0) // anderenfalls läuft der Prozess weiter (danke Spring)
  }
}
