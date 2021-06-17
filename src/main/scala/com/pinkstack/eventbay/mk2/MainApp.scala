package com.pinkstack.eventbay.mk2

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object MainApp {

  final case class User(name: String)
  final case class MembersList(names: List[User])

  final case class AddFamilyMember(name: String)

  final case object GetMembersList


  class Family extends Actor with ActorLogging {

    var members: List[User] = List.empty[User]

    override def receive: Receive = {
      case AddFamilyMember(name) =>
        members ++= List(User(name))
        log.info(s"Added ${name}")
      case GetMembersList =>
        sender ! MembersList(members)
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val timeout: Timeout = 3.seconds
    val system: ActorSystem = ActorSystem("family-example")
    import system.dispatcher

    val b = system.actorOf(Props(classOf[Family]), "brglez")

    b ! AddFamilyMember("Oto")
    b ! AddFamilyMember("Martina")
    b ! AddFamilyMember("Tinkara")
    b ! AddFamilyMember("Rudi")

    (b ? GetMembersList).onComplete {
      case Success(MembersList(members)) =>
        println(s"Got => ${members.mkString(", ")}")
      case Failure(exception) =>
        System.err.println(exception)
    }

  }

}
