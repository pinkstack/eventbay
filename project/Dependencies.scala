import sbt._

object Dependencies {

  type Version = String

  object Versions {
    val Sttp: Version = "3.3.4"
    val ScalaTest: Version = "3.2.8"

    val Cats: Version = "2.6.0"
    val CatsEffect: Version = "3.1.1"
    val Circe: Version = "0.12.3"
    val Doobie: Version = "1.0.0-M4" // "0.12.1"
    val LogbackClassic: Version = "1.2.3"
    val ScalaLogging: Version = "3.9.3"
    val FS2: Version = "3.0.0"
    val Akka: Version = "2.6.14"
    val AkkaHttp: Version = "10.2.4"
    val Tapir: Version = "0.18.0-M15"
  }

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core" % Versions.Sttp,
    "com.softwaremill.sttp.client3" %% "circe" % Versions.Sttp
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core" % Versions.Cats,
    "org.typelevel" %% "cats-effect" % Versions.CatsEffect
  )

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.Circe)

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % Versions.Doobie,
    "org.xerial" % "sqlite-jdbc" % "3.34.0"

    // "org.tpolecat" %% "doobie-specs2"    % "0.12.1" % "test", // Specs2 support for typechecking statements.
    // "org.tpolecat" %% "doobie-scalatest" % "0.12.1" % "test"  // ScalaTest support for typechecking statements.
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % Versions.LogbackClassic,
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.ScalaLogging,
    "com.softwaremill.sttp.client3" %% "slf4j-backend" % Versions.Sttp
  )

  val apacheCommons = Seq(
    "org.apache.commons" % "commons-math3" % "3.6.1"
  )

  val fs2 = Seq(
    "co.fs2" %% "fs2-core",
    "co.fs2" %% "fs2-io",
    "co.fs2" %% "fs2-reactive-streams"
  ).map(_ % Versions.FS2)

  val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % Versions.ScalaTest
  )

  val akka = Seq(
    // Classic
    "com.typesafe.akka" %% "akka-actor" % Versions.Akka,
    "com.typesafe.akka" %% "akka-testkit" % Versions.Akka % Test,

    // Typed
    "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.Akka % Test,

    // Http
    "com.typesafe.akka" %% "akka-stream" % Versions.Akka,
    "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp,
    "com.typesafe.akka" %% "akka-http-xml" % Versions.AkkaHttp,

    // Persistance
    "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.0.5",
    "com.typesafe.akka" %% "akka-persistence" % Versions.Akka,
    "com.typesafe.akka" %% "akka-persistence-query" % Versions.Akka,
    "com.typesafe.akka" %% "akka-cluster-tools" % Versions.Akka
  )

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core",
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml",

    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http",
    "com.softwaremill.sttp.tapir" %% "tapir-redoc-akka-http"
  ).map(_ % Versions.Tapir)
}
