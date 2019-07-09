package scroll.examples

//import com.github.fluent.hibernate.cfg.scanner.EntityScanner
import scroll.internal.Compartment
import org.hibernate._
import javax.persistence._
import scroll.persistence.Database
import java.util
import java.util.List

import scroll.examples.UniversityExample.Person
import scroll.persistence.Inheritance.{MetaPersistenceCt, MetaPersistenceNt, MetaPersistenceRt}
import scroll.persistence.Util.{ReturnRT, Serializer}

object UniversityExample {

//  class University extends Compartment {
  class University extends MetaPersistenceCt {
    var country: String = _

    class Student extends MetaPersistenceRt {
      var matNr: Int = _
      def talk(): Unit = {
        println("----- I am a student")
      }
    }

    class Professor extends MetaPersistenceRt {
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

  }

  class Person(var name: String) extends MetaPersistenceNt {
    def talk(): Unit = {
      println("----- I am a person")
    }
  }

  def main(args: Array[String]): Unit = {
    println("===== START =====")

    val uni = new University {
/*
      // Eigene Klassenvariablen der Univresität
      this.country = "Deutschland"



      // === NT
      println("----- NT")

      // insert
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

      // insert
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

      println("xxxxxxxxxxxxxxxxxxxxxxx 2")
      studentHansSelectList2.get(0).players.get(0).asInstanceOf[Person].roles()
      println("xxxxxxxxxxxxxxxxxxxxxxx 2")

      println("xxxxxxxxxxxxxxxxxxxxxxx 3")
      Database.rt.test(studentHans)
      println("xxxxxxxxxxxxxxxxxxxxxxx 3")



      // === CT playing RT
      println("----- CT playing RT")

      // insert
      // exakt analog zu `NT playing RT`
      val uni2 = new University{} // hier wird nun ein CT in einem CT erzeugt, macht semantisch keinen Sinn, aber im Zuge des Beispiels egal
      val studentUni = new Student
      uni2 play studentUni
      Database.rt.createOrUpdate(studentUni, true, true) // der RT speichert seine Spieler hier mit, was danna auch der CT ist. Direktes Speichern von CT kommt unten ausführlicher



      // === CT
      // Überwiegend analog zu NT bzw. RT playing NT. Hier nur mit CT statt NT (beides aber rigide Typen)
      println("----- CT")

      // insert
      this.country = "Deutschland"
      Database.ct.createOrUpdate(this)

      // delete
//      Database.ct.delete(this) // geht hier nicht, da im CT noch RTs enthalten sind. Diese müssten zuerst gelöscht werden

*/





/*
      val hans = new Person("Hans Irgendwas")
      val student2 = new Student
      student2.matNr = 6789
      hans play student2
      Database.rt.createOrUpdate(student2, true, true)
*/






      Database.rt.select(classOf[Student], "matNr", 6789, true, this)

      Serializer.printAllFields(this.allPlayers)
      for(obj <- this.allPlayers){
        println("=============================")
        for(inner <- obj.roles()){
          println("---------------------")
          Serializer.printAllFields(inner)
        }
      }










      // === Rollen ermitteln
//      hansSelect.roles()
//      allPlayers()



//      // === RT
//
//      // insert
//      val student2 = new Student
//      val student3 = new Student
//      hans play student2
//      hans play student3
//      uwe play student2
//      println("hans.roles() = " + hans.roles())
//      println("uwe.roles() = " + uwe.roles())
//      println("allPlayers = " + allPlayers)
//      Database.rt.createOrUpdate(student2, true, true)






//      hans.talk()
//
//      val student = new Student
//      println("Player equals core: " + ((hans play student) == hans))
//      +hans talk()
//
//      println((+student).name)
//      println("Role core equals core: " + (+student == hans))
//
//      uwe play new Professor
//      +uwe talk()
//      println("Core equals core playing a role: " + (+uwe == uwe))
//
//      +uwe teach hans




    }

    System.exit(0) // anderenfalls beendet die Anwendung nicht
  }
}
