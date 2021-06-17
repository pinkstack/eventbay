package com.pinkstack.eventbay.mk4

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object MainApp {

  case class Person(firstName: String, lastName: String)

  sealed trait Command

  sealed trait Event

  case class AddPerson(person: Person) extends Command

  case class PersonAdded(id: UUID, person: Person) extends Event

  case object GetPeople extends Command

  case class PeopleResponse(people: Map[UUID, Person] = Map.empty[UUID, Person]) extends Event

  class Families extends Actor with ActorLogging {
    var people: Map[UUID, Person] = Map.empty

    override def receive: Receive = LoggingReceive {
      case AddPerson(person: Person) =>
        people += (UUID.randomUUID() -> person)

      case GetPeople =>
        sender ! PeopleResponse(people)

      case string: String if string.contains("boom") =>
        throw new RuntimeException("Got \"boom\" 💥💥💥")
    }
  }


  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("families")
    import system.dispatcher

    val families = system.actorOf(Props(classOf[Families]))
    families ! AddPerson(Person("Oto", "Brglez"))
    families ! AddPerson(Person("Martina", "Brglez"))
    families ! AddPerson(Person("Janez", "Novak"))
    families ! "Please go boom now!!!"
    families ! AddPerson(Person("Miha", "Novak"))
    families ! AddPerson(Person("Luka", "Novak"))

    implicit val timeout: Timeout = 3.seconds
    val r = (families ? GetPeople).mapTo[PeopleResponse]

    r.onComplete {
      case Success(response: PeopleResponse) =>
        println {
          response.people.map(t => s"${t._2.firstName} ${t._2.lastName}").mkString(", ")
        }

      case Failure(exception) =>
        exception.printStackTrace()
    }
  }
}
