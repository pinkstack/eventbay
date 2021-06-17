package com.pinkstack.eventbay.v2mk3


import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.ask
import akka.stream.RestartSettings
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.xml.NodeSeq

object Domain {
  type Temperature = Double
  type Measurement = Option[Temperature]
}

import com.pinkstack.eventbay.v2mk3.Domain._

class ChangeDetector extends Actor {

  import ChangeDetector._

  var measurement: Measurement = None

  override def receive: Receive = {
    case Some(temperature: Temperature) =>
      sender() ! detectSetAndReply(temperature)
  }

  private def detectSetAndReply(newTemperature: Temperature): ChangeType =
    if (measurement.isEmpty) {
      measurement = Some(newTemperature)
      Same(newTemperature)
    } else {
      val oldTemperature = measurement.getOrElse(Double.MinValue)
      if (oldTemperature != newTemperature) {
        measurement = Some(newTemperature)
        Changed(oldTemperature, newTemperature)
      } else Same(oldTemperature)
    }
}

object ChangeDetector {
  sealed trait ChangeType

  case class Same(temperature: Temperature) extends ChangeType

  case class Changed(from: Temperature, to: Temperature) extends ChangeType
}

object MainApp extends LazyLogging {

  import Domain._

  val ARSOLjubljanaURL: String = ConfigFactory.load().getConfig("examples.arso").getString("ljubljana-url")

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("temperature-system")
    import system.dispatcher

    val changeDetector: ActorRef = system.actorOf(Props(classOf[ChangeDetector]), "change-detector")

    val getMeasurementsFlow: Flow[String, Measurement, NotUsed] =
      RestartFlow.onFailuresWithBackoff(RestartSettings(10.seconds, 30.seconds, 0.3)) { () =>
        Flow[String].mapAsyncUnordered(1) { _ => getMeasurement }
      }

    Source.tick(1.seconds, 2.seconds, "Tick")
      .via(getMeasurementsFlow)
      .alsoTo(Sink.foreach(v => logger.info(s"Latest measurement is ${v.getOrElse("none")} 🌡")))
      .mapAsync(1)(measurement => changeDetector.ask(measurement)(1.second))
      .collect { case change: ChangeDetector.Changed => change }
      .runWith(Sink.foreach(change =>
        logger.info(s"The temperature has changed. From ${change.from} to ${change.to}, ${direction(change)}.")))
  }

  private def getMeasurement(implicit system: ActorSystem, ec: ExecutionContext): Future[Measurement] =
    for {
      response <- Http().singleRequest(HttpRequest(uri = ARSOLjubljanaURL))
      nodes <- Unmarshal(response.entity).to[NodeSeq]
      measurement = (nodes \\ "t").headOption.flatMap(_.text.toDoubleOption)
    } yield measurement

  private val direction: ChangeDetector.Changed => String = {
    case ChangeDetector.Changed(from, to) if to > from => "up 🔼"
    case ChangeDetector.Changed(from, to) if to < from => "down 🔽"
    case _ => "impossible."
  }
}
