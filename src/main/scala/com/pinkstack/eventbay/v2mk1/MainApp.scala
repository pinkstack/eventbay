package com.pinkstack.eventbay.v2mk1

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import scala.concurrent.duration._

class PersonActor(firstName: String, lastName: String) extends Actor with ActorLogging {
  override def receive: Receive = {
    case "eat-ice-cream" =>
      log.info(s"$firstName has started eating ice cream. 🍦")
      Thread.sleep(5.seconds.toMillis)
      log.info(s"$firstName has eaten his ice cream. ✅")
  }
}

object MainApp extends App {
  val system = ActorSystem("first-system")

  val kid1: ActorRef = system.actorOf(Props(classOf[PersonActor], "Rudi", "Brglez"), "rudi")
  val kid2: ActorRef = system.actorOf(Props(classOf[PersonActor], "Tinkara", "Brglez"), "tinkara")
  val me: ActorRef = system.actorOf(Props(classOf[PersonActor], "Oto", "Brglez"), "oto")

  kid1 ! "eat-ice-cream"
  kid1 ! "eat-ice-cream"
  kid1 ! "eat-ice-cream"

  kid2 ! "eat-ice-cream"
  me ! "drink-beer"
}
