package com.pinkstack.eventbay.mk4

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.event.LoggingReceive
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object MainApp2 {

  case class Person(firstName: String, lastName: String)

  sealed trait Command

  sealed trait Event

  case class AddPerson(person: Person) extends Command

  case class PersonAdded(id: UUID, person: Person) extends Event

  case object GetPeople extends Command

  case class PeopleResponse(people: Map[UUID, Person] = Map.empty[UUID, Person]) extends Event

  case class Wave(person: Person) extends Command

  class Families extends Actor with ActorLogging {

    import context.dispatcher

    override def receive: Receive = /* LoggingReceive */ {
      case command@AddPerson(person) =>
        familyActor(person).forward(command)

      case command@GetPeople =>
        implicit val timeout: Timeout = 1.second
        Future.sequence(
          context.children.map { ref => (ref ? command).mapTo[PeopleResponse] })
          .map {
            _.foldLeft(Map.empty[UUID, Person]) { case (agg, c) => agg ++ c.people }
          }
          .map(PeopleResponse.apply) pipeTo sender

      case command@Wave(person: Person) =>
        familyActor(person).forward(command)
    }

    private val familyActor: Person => ActorRef = { person =>
      val actorName: String = s"family-${person.lastName}".toLowerCase

      context.child(actorName).getOrElse {
        context.watch(context.actorOf(Props(classOf[Family], person.lastName), actorName))
      }
    }
  }

  class Family(lastName: String) extends Actor with ActorLogging {
    var people: Map[UUID, Person] = Map.empty

    override def receive: Receive = /* LoggingReceive.withLabel(s"👨‍👩‍👧 $lastName") */ {

      case AddPerson(person) =>
        people += (UUID.randomUUID() -> person)

      case GetPeople =>
        sender ! PeopleResponse(people)

      case Wave(person: Person) if person.firstName.contains("boom") =>
        throw new RuntimeException("Got \"boom\" 💥💥💥")

      case Wave(person: Person) if people.values.exists(_ == person) =>
        log.info(s"👋 from ${person}")

      case Wave(person) =>
        log.info(s"☹️ no person ${person.firstName} in the family ${lastName}")
    }

    override def postStop(): Unit = {
      log.debug(s"👨‍👩‍👧‍👧 ${lastName} stopped.")
    }

    override def preStart(): Unit = {
      log.debug(s"👨‍👩‍👧‍👧 ${lastName} started.")
    }
  }


  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("families")
    import system.dispatcher

    val families = system.actorOf(Props(classOf[Families]))
    families ! AddPerson(Person("Oto", "Brglez"))
    families ! AddPerson(Person("Martina", "Brglez"))
    families ! AddPerson(Person("Janez", "Novak"))
    families ! AddPerson(Person("Miha", "Novak"))
    families ! AddPerson(Person("Luka", "Novak"))

    families ! Wave(Person("Luka", "Novak"))
    families ! Wave(Person("Dude", "Novak"))

    implicit val timeout: Timeout = 3.seconds
    (families ? GetPeople).mapTo[PeopleResponse].onComplete {
      case Success(PeopleResponse(people)) =>
        println(people.map(t => s"${t._2.firstName} ${t._2.lastName}").mkString(", "))
      case Failure(exception) =>
        exception.printStackTrace()
    }

    families ! Wave(Person("Please go boom now!!!", "boom"))
    families ! Wave(Person("boom now!!!", "Novak"))


    (families ? GetPeople).mapTo[PeopleResponse].onComplete {
      case Success(PeopleResponse(people)) =>
        println(people.map(t => s"${t._2.firstName} ${t._2.lastName}").mkString(", "))
      case Failure(exception) =>
        exception.printStackTrace()
    }
  }
}
