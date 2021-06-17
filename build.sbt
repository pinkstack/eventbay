import Dependencies._

name := "eventbay"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= {
  akka ++ logging ++ tapir ++ circe
}
