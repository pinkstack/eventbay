package com.pinkstack.eventbay.mk5

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._

import scala.util.{Failure, Success}
import scala.concurrent.duration
import scala.concurrent.duration.DurationInt

object MainApp {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("mk5-app")
    import system.dispatcher

    final case class Person(name: String)

    val fileSink = Sink.foreach[Person](p => println(s"This person ${p}"))

    val transformFlow: Flow[Person, Person, NotUsed] = Flow[Person].map(person => person.copy(name = person.name.toUpperCase))


    val f = Source(List[Person](
      Person("Oto"), Person("Luka"), Person("Martina"), Person("Janez"))
    )
      .via(transformFlow)
      .throttle(1, 1.second)
      .alsoTo(fileSink)
      .runWith(Sink.foreach(println))

    f.onComplete {
      case Success(value) =>
        println(s"Got => ${value}")
      case Failure(exception) =>
        exception.printStackTrace()
    }

  }
}
