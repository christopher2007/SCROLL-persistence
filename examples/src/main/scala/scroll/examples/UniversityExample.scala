package scroll.examples

import com.github.fluent.hibernate.cfg.scanner.EntityScanner
import scroll.internal.Compartment
import org.hibernate._
import javax.persistence._
import scroll.persistence.{Database, HibernateUtil, MetaPersistenceCt, MetaPersistenceNtRt}
import java.util

import scroll.examples.UniversityExample.Person

object UniversityExample {

//  class University extends MetaPersistenceCt {
  class University extends Compartment {

    class Student {
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

////    val classes: List[Class[_]] = List(classOf[Person], classOf[Student])
//        val classes: List[Class[_]] = List(classOf[Person])
//    HibernateUtil.buildSessionFactory(classes)
//    val sessionF: SessionFactory = HibernateUtil.getSessionFactory
//    val session: Session = sessionF.openSession()

    val test = new University {
      val uwe = new Person("uwe")






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
//      // Rollen ermitteln
//      hansSelect.roles()
//      allPlayers()







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

      // persist example

//      // Klassen ermitteln
////      val classes: util.List[Class[_]] = EntityScanner.scanPackages("scroll").result
//      val classes: List[Class[_]] = List(classOf[Person], classOf[Student])
//      HibernateUtil.buildSessionFactory(classes)
//
//      // short
////      val session: Session = HibernateUtil.getSessionFactory.openSession()
//
//      // long
//      val sessionF: SessionFactory = HibernateUtil.getSessionFactory
//      val session: Session = sessionF.openSession()

//      session.beginTransaction()
//      val p = new Person("Heinrich")
//      p.something = "1"
//      session.save(p)
//      session.save(student)
//      session.getTransaction().commit()
//      println("FINISHED")
//
//      HibernateUtil.shutdown()
    }

//    session.beginTransaction()
//    session.save(test)
//    session.getTransaction().commit()

    System.exit(0)
  }
}
