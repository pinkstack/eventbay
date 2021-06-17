package com.pinkstack.eventbay.mk3

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import sttp.tapir.{Endpoint, _}
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.{OpenAPI, Server}
import sttp.tapir.swagger.akkahttp.SwaggerAkka
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import scala.concurrent.Future
import sttp.tapir.openapi.circe.yaml._

object MainApp extends App {
  final case class Result(result: Int)

  val calculatorEndpoint: Endpoint[(Int, Int), Unit, Int, Any] =
    endpoint.get
      .in("compute")
      .in(query[Int]("x").and(query[Int]("y")))
      .out(plainBody[Int])

  implicit val system: ActorSystem = ActorSystem("mk3")
  import system.dispatcher

  val calculatorRoutes = AkkaHttpServerInterpreter.toRoute(calculatorEndpoint) {
    case (x, y) =>
      Future.successful(Right(x + y))
  }

  val docs: OpenAPI = OpenAPIDocsInterpreter.toOpenAPI(calculatorEndpoint, "Calculator", "1.0")
    .servers(List(Server("http://localhost:9000").description("Development")))


  val routes = {
    import akka.http.scaladsl.server.Directives._
    concat(calculatorRoutes, new SwaggerAkka(docs.toYaml).routes)
  }

  val bc = Http().newServerAt("localhost", 9000).bindFlow(routes).map { _ =>
    println("Go to: http://localhost:9000/docs")
    scala.io.StdIn.readLine()
  }

  // Await.result(bc.transformWith { r => system.terminate().transform(_ => r) }, 1.minute)
}
