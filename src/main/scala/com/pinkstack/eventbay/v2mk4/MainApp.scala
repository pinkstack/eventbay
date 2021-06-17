package com.pinkstack.eventbay.v2mk4

import akka.actor._
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.pattern.ask
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.util.Success


sealed trait Command

sealed trait Event

case class SetName(name: String) extends Command

case class NameSet(name: String) extends Event

case class AppendTextNode(name: String) extends Command

case class TextNodeAppended(name: String) extends Event

case class AppendQuestionNode(name: String, options: List[String] = List.empty) extends Command

case class QuestionNodeAppended(name: String, options: List[String]) extends Event

case object GetSurvey extends Command

sealed trait Node {
  def name: String
}

case class Question(name: String, options: List[String]) extends Node

case class Text(name: String) extends Node

final case class Survey(name: Option[String] = None,
                        nodes: List[Node] = List.empty[Node])

class SurveyActor(id: UUID) extends PersistentActor with ActorLogging {
  override def persistenceId: String = id.toString

  var survey: Survey = Survey()

  def updateState(event: Event): Unit = {
    event match {
      case NameSet(name) =>
        survey = survey.copy(name = Some(name))
      case TextNodeAppended(name) =>
        survey = survey.copy(nodes = survey.nodes ++ List(Text(name)))
      case QuestionNodeAppended(name, options) =>
        survey = survey.copy(nodes = survey.nodes ++ List(Question(name, options)))
    }
  }

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case SnapshotOffer(_, surveySnapshot: Survey) => survey = surveySnapshot
  }

  override def receiveCommand: Receive = {
    case SetName(name) =>
      persist(NameSet(name)) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case AppendTextNode(name) =>
      persist(TextNodeAppended(name)) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case AppendQuestionNode(name, options: List[String]) =>
      persist(QuestionNodeAppended(name, options)) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case GetSurvey =>
      sender() ! survey
  }
}


object MainApp extends App {
  val system: ActorSystem = ActorSystem("surveys")

  import system.dispatcher

  val exising = UUID.fromString("08af3e69-846b-40aa-a5e0-165d05a12392")
  val s1 = system.actorOf(Props(classOf[SurveyActor], UUID.randomUUID()))

  s1 ! SetName("This is my first example")
  s1 ! SetName("First Survey V1")
  s1 ! AppendTextNode("Hello!")
  s1 ! AppendTextNode("This is example of Akka Persistence.")
  s1 ! SetName("Short name")
  s1 ! AppendQuestionNode("Do you like this", List("Yes", "No", "Maybe"))

  s1.ask(GetSurvey)(20.seconds).onComplete {
    case Success(value: Survey) =>
      println(value.asJson)
  }
}
