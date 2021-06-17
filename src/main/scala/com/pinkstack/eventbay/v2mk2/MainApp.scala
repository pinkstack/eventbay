package com.pinkstack.eventbay.v2mk2

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import scala.concurrent.duration.DurationInt

object Person {
  trait PersonCommand
  final case object EatIceCream extends PersonCommand
  final case object FinishedEatingIceCream extends PersonCommand

  def apply(firstName: String): Behavior[PersonCommand] = idle(firstName)

  private def idle(firstName: String): Behavior[PersonCommand] =
    Behaviors.receiveMessage[PersonCommand] {
      case EatIceCream => eatingIceCream(firstName)
      case _ => Behaviors.same
    }

  private def eatingIceCream(firstName: String): Behavior[PersonCommand] =
    Behaviors.setup { context =>
      Behaviors.withTimers[PersonCommand] { timers =>
        context.log.info(s"$firstName has started eating his ice-cream. 🍦")
        timers.startSingleTimer(FinishedEatingIceCream, 3.seconds)

        Behaviors.receiveMessage {
          case FinishedEatingIceCream =>
            context.log.info(s"$firstName is done with ice-cream. ✅")
            idle(firstName)
          case _ =>
            context.log.info(s"$firstName: Sorry, I'm still eating,...")
            Behaviors.same
        }
      }
    }
}

object Family {
  sealed trait FamilyCommand
  final case class AddPerson(firstName: String) extends FamilyCommand
  final case class EatCake(firstName: String) extends FamilyCommand

  def apply(): Behavior[FamilyCommand] = Behaviors.receive {
    case (context, AddPerson(firstName)) =>
      context.spawn(Person(firstName), firstName)
      Behaviors.same

    case (context, EatCake(firstName)) =>
      context.child(firstName)
        .map(_.asInstanceOf[ActorRef[Person.PersonCommand]])
        .foreach(ref => ref ! Person.EatIceCream)
      Behaviors.same

    case _ => Behaviors.unhandled
  }
}

object MainApp extends App {
  val system = ActorSystem[Family.FamilyCommand](Family(), "family")

  system ! Family.AddPerson("Rudi")
  system ! Family.AddPerson("Tinkara")
  system ! Family.AddPerson("Oto")

  system ! Family.EatCake("Rudi")
  system ! Family.EatCake("Tinkara")
  system ! Family.EatCake("Rudi")
  system ! Family.EatCake("Tinkara")

  // system ! "drink beer!"
}
